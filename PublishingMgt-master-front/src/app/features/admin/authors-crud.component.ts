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
  selector: 'app-authors-crud',
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
  templateUrl: './authors-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']
})
export class AuthorsCrudComponent implements OnInit, AfterViewInit {
  public authService = inject(AuthService);
  public userService = inject(UserService);
  private cdr = inject(ChangeDetectorRef);

  /* =========================
     TABLE + PAGINATOR
  ========================= */

  displayedColumns: string[] = [
    'authorId',
    'firstname',
    'surname',
    'actions'
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
  successMessage: string | null = null;

  formAuthor: Partial<Author> = {
    login: '',
    firstname: '',
    surname: ''
  };

  /* =========================
     LIFECYCLE
  ========================= */

  ngOnInit(): void {
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

  /* =========================
     EDIT
  ========================= */

  edit(author: Author): void {
    this.selectedAuthor = author;
    this.showForm = true;
    this.formAuthor = { ...author };
  }

  /* =========================
     CREATE
  ========================= */

  openCreate(): void {
    this.selectedAuthor = null;
    this.showForm = true;

    this.formAuthor = {
      login: '',
      firstname: '',
      surname: ''
    };
  }

      create(): void {

      if (!this.formAuthor.firstname || !this.formAuthor.surname) {
        return;
      }

      this.error = null;
      this.successMessage = null;

      const payload = {
        login: `${this.formAuthor.firstname.toLowerCase()}.${this.formAuthor.surname.toLowerCase()}`,
        password: 'changeme',
        role: 'AUTHOR',
        author: {
          firstname: this.formAuthor.firstname,
          surname: this.formAuthor.surname
        }
      };

      this.userService.createUser(payload).subscribe({

        next: () => {

          this.successMessage = 'Auteur créé avec succès';

          this.clearMessagesAfterDelay();

          this.resetForm();
          this.loadAuthors();
        },

        error: err => {

          console.error(err);

          this.error = 'Erreur création auteur';

          this.clearMessagesAfterDelay();
        }
      });
    }

      /* =========================
        UPDATE
      ========================= */

      update(): void {

      if (!this.selectedAuthor) return;

      this.error = null;
      this.successMessage = null;

      const payload = {
        login: this.selectedAuthor.login,
        role: 'AUTHOR',
        author: {
          firstname: this.formAuthor.firstname,
          surname: this.formAuthor.surname
        }
      };

      this.userService.updateUser(this.selectedAuthor.id, payload).subscribe({

        next: () => {

          this.successMessage = 'Auteur modifié avec succès';

          this.clearMessagesAfterDelay();

          this.resetForm();
          this.loadAuthors();
        },

        error: err => {

          console.error(err);

          this.error = 'Erreur modification auteur';

          this.clearMessagesAfterDelay();
        }
      });
    }

  /* =========================
     DELETE
  ========================= */

        delete(id: number): void {

        this.error = null;
        this.successMessage = null;

        this.userService.deleteUser(id).subscribe({

          next: () => {

            this.successMessage = 'Auteur supprimé avec succès';

            this.clearMessagesAfterDelay();

            this.loadAuthors();

            this.cdr.detectChanges();
          },

          error: err => {

            console.error(err);

            this.error =
              'Cet auteur est rattaché, impossible de le supprimer.';

            this.clearMessagesAfterDelay();

            this.cdr.detectChanges();
          }
        });
      }

  /* =========================
     CANCEL / RESET
  ========================= */

  cancel(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.selectedAuthor = null;
    this.showForm = false;

    this.formAuthor = {
      login: '',
      firstname: '',
      surname: ''
    };
  }

  /* =========================
     CLEAR MESSAGE
  ========================= */

          private clearMessagesAfterDelay(): void {

          setTimeout(() => {

            this.error = null;
            this.successMessage = null;

            this.cdr.detectChanges();

          }, 4000);
        }
}