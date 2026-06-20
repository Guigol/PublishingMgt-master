import {
  Component,
  inject,
  OnInit,
  ChangeDetectorRef,
  ViewChild,
  AfterViewInit
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { Author } from '../../core/models/author.model';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-authors-read',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatSidenavModule
  ],
  templateUrl: './author-read-manager.component.html',
  styleUrls: ['./author-read-manager.component.css']
})
export class AuthorReadMgrComponent implements OnInit, AfterViewInit {
  public authService = inject(AuthService);
  public userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  /* =========================
     TABLE + PAGINATOR
  ========================= */

  displayedColumns: string[] = [
    'authorId',
    'firstname',
    'surname'
    ];

  dataSource = new MatTableDataSource<Author>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  /* =========================
     DATA
  ========================= */

  authors: Author[] = [];
  selectedAuthor: Author | null = null;

  loading = false;
  error: string | null = null;
  showForm = false;

  formAuthor: Partial<Author> = {
    login: '',
    firstname: '',
    surname: ''
  };

  /* =========================
     LIFECYCLE
  ========================= */

ngOnInit(): void {

  this.dataSource.filterPredicate = (data: Author, filter: string): boolean => {

    return (
      (data.authorId?.toString() ?? '').includes(filter) ||
      (data.firstname?.toLowerCase() ?? '').includes(filter) ||
      (data.surname?.toLowerCase() ?? '').includes(filter)
    );
  };

  this.loadAuthors();
}

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  /* =========================
     LOAD AUTHORS
  ========================= */

  loadAuthors(): void {
    this.loading = true;
    this.error = null;

    this.userService
      .getAllAuthors()
      .pipe(
        catchError(err => {
          console.error(err);
          this.error = 'Impossible de charger les auteurs';
          this.loading = false;
          return of([]);
        })
      )
      .subscribe((data: any[]) => {
        const mappedAuthors: Author[] = data.map(a => ({
          id: a.id,               // USERS.id
          login: a.login,
          role: a.role,
          authorId: a.authorId,   // AUTHORS.authorId
          firstname: a.firstname,
          surname: a.surname,
          createdAt: a.createdAt
        }));

        this.authors = mappedAuthors;
        this.dataSource.data = mappedAuthors;

        this.loading = false;
        this.cdr.detectChanges();

        if (this.paginator) {
          this.dataSource.paginator = this.paginator;
        }
      });
  }

  applyFilter(event: Event): void {

  const filterValue = (event.target as HTMLInputElement)
    .value
    .trim()
    .toLowerCase();

  this.dataSource.filter = filterValue;
}

  
}