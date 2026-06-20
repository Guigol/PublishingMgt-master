import {
  Component,
  inject,
  OnInit,
  ViewChild,
  AfterViewInit,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { BookService } from '../../core/services/book.service';
import { AuthService } from '../../core/services/auth.service';
import { PublisherService } from '../../core/services/publisher.service';
import { BookResponse } from '../../core/models/bookresponse';
import { BookRequest } from '../../core/models/bookrequest';
import { Publisher } from '../../core/models/publisher.model';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ChangeDetectorRef } from '@angular/core';

interface Author {
  id: number;
  authorId: number;
  firstname: string;
  surname: string;
}

@Component({
  selector: 'app-books-crud',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatSidenavModule,
    MatFormFieldModule,
    MatSelectModule,
    
  ],
  templateUrl: './books-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']
})
export class BooksCrudComponent implements OnInit, AfterViewInit {

  private bookService = inject(BookService);
  private authService = inject(AuthService);
  private publisherService = inject(PublisherService);
  private cdr = inject(ChangeDetectorRef);

  /* ========================= */

  displayedColumns: string[] = [
    'id',
    'title',
    'publisher',
    'authors',
    'actions'
  ];

  dataSource = new MatTableDataSource<BookResponse>([]);
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  books: BookResponse[] = [];
  authors: Author[] = [];
  publishers: Publisher[] = [];

  selectedBook: BookResponse | null = null;
  showForm = false;

  formBook: BookRequest = {
    title: '',
    publisherId: null,
    authorIds: []
  };

  error: string | null = null;
  successMessage: string | null = null;

  /* ========================= */

  ngOnInit(): void {
    this.loadBooks();
    this.loadAuthors();
    this.loadPublishers();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  /* ========================= */
  /* MESSAGES */
  /* ========================= */

  private clearMessagesAfterDelay(): void {

  setTimeout(() => {

    this.error = null;
    this.successMessage = null;

     this.cdr.detectChanges();

  }, 5000);
}

  /* ========================= */
  /* LOAD DATA */
  /* ========================= */

  loadBooks(): void {
    this.bookService.getAll()
      .pipe(
        catchError(err => {
          console.error(err);
          return of([]);
        })
      )
      .subscribe((data: BookResponse[]) => {
        this.books = data;
        this.dataSource.data = data;
      });
  }

  loadAuthors(): void {
    this.authService.getAuthors().subscribe(data => {
      this.authors = data;
    });
  }

  loadPublishers(): void {
    this.publisherService.getAll().subscribe(data => {
      this.publishers = data;
    });
  }

  /* ========================= */

  getPublisherName(id: number): string {
    return this.publishers.find(p => p.publisher_id === id)?.name ?? '';
  }

  /* ========================= */
  /* CREATE */
  /* ========================= */

  openCreate(): void {
    this.selectedBook = null;
    this.showForm = true;

    this.formBook = {
      title: '',
      publisherId: null,
      authorIds: []
    };
  }

  create(): void {

    this.error = null;
    this.successMessage = null;

    const payload: BookRequest = {
      title: this.formBook.title,
      publisherId: this.formBook.publisherId,
      authorIds: this.formBook.authorIds
    };

    this.bookService.create(payload).subscribe({
      next: () => {
        this.successMessage = 'Livre créé avec succès';
        this.clearMessagesAfterDelay();
        this.resetForm();
        this.loadBooks();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Échec supression livre';
        this.clearMessagesAfterDelay();
      }
    });
  }

  /* ========================= */
  /* EDIT */
  /* ========================= */

  edit(book: BookResponse): void {
    this.selectedBook = book;

    this.formBook = {
      title: book.title,
      publisherId: book.publisher,
      authorIds: (book.authors ?? []).map(a => a.id)
    };

    this.showForm = true;
  }

  /* ========================= */
  /* UPDATE */
  /* ========================= */

  update(): void {

    if (!this.selectedBook) return;

    this.error = null;
    this.successMessage = null;

    const payload: BookRequest = {
      title: this.formBook.title,
      publisherId: this.formBook.publisherId,
      authorIds: this.formBook.authorIds
    };

    this.bookService.update(this.selectedBook.id, payload).subscribe({
      next: () => {
        this.successMessage = 'Livre modifié avec succès';
        this.clearMessagesAfterDelay();
        this.resetForm();
        this.loadBooks();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Échec modification livre';
        this.clearMessagesAfterDelay();
      }
    });
  }

  /* ========================= */
  /* DELETE */
  /* ========================= */

  delete(id: number): void {

  this.error = null;
  this.successMessage = null;

  this.bookService.delete(id)
    .subscribe({
      next: () => {

        this.successMessage = 'Livre supprimé avec succès';
        this.clearMessagesAfterDelay();

        this.loadBooks();
        this.cdr.detectChanges();
      },

      error: (err) => {

        const backendMessage =
          err?.error?.error ||
          err?.error?.message;

       this.error =
          'Suppression impossible ! Il faut : ' +
          '1- Supprimer les ventes, ' +
          '2- Supprimer les publications, ' +
          '3- Supprimer la participation des auteurs';

        this.clearMessagesAfterDelay();

      
        this.cdr.detectChanges();
       }
    });
}
  /* ========================= */
  /* CANCEL */
  /* ========================= */

  cancel(): void {
    this.resetForm();
  }

  /* ========================= */
  /* RESET FORM */
  /* ========================= */

  private resetForm(): void {
    this.showForm = false;
    this.selectedBook = null;

    this.formBook = {
      title: '',
      publisherId: null,
      authorIds: []
    };
  }
}