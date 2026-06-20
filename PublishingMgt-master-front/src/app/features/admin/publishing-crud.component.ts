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

import { catchError, of } from 'rxjs';

import { PublishingService } from '../../core/services/publishing.service';
import { Publishing } from '../../core/models/publishing.model';
import { BookService } from '../../core/services/book.service';
import { Book } from '../../core/models/book.model';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-publishing-crud',
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
  templateUrl: './publishing-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']
})
export class PublishingCrudComponent implements OnInit, AfterViewInit {

  private publishingService = inject(PublishingService);
  private bookService = inject(BookService);
  private cdr = inject(ChangeDetectorRef);

  /* =========================
     TABLE
  ========================= */

  displayedColumns: string[] = [
    'publishingId',
    'name',
    'isbn',
    'noTprice',
    'royalties',
    'bookId',
    'bookTitle',
    'actions'
  ];

  dataSource = new MatTableDataSource<Publishing>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  /* =========================
     DATA
  ========================= */

  publishings: Publishing[] = [];
  books: Book[] = [];

  selectedPublishing: Publishing | null = null;

  loading = false;
  error: string | null = null;
  showForm = false;
  successMessage: string | null = null;

  selectedBookId: number | null = null;

  formPublishing: any = {
    name: '',
    isbn: '',
    noTprice: 0,
    royalties: 0
  };

  /* =========================
     INIT
  ========================= */

  ngOnInit(): void {
    this.loadPublishings();
    this.loadBooks();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  /* =========================
     LOAD PUBLISHINGS
  ========================= */

  loadPublishings(): void {
    this.loading = true;
    this.error = null;

    this.publishingService.getAll()
      .pipe(
        catchError(err => {
          console.error(err);
          this.error = 'Erreur chargement publishings';
          this.loading = false;
          this.cdr.detectChanges();
          return of([]);
        })
      )
      .subscribe((data: Publishing[]) => {
        this.publishings = data;
        this.dataSource.data = data;

        this.loading = false;
        this.cdr.detectChanges();

        if (this.paginator) {
          this.dataSource.paginator = this.paginator;
        }
      });
  }

  /* =========================
     LOAD BOOKS
  ========================= */

  loadBooks(): void {
    this.bookService.getAll()
      .pipe(
        catchError(err => {
          console.error(err);
          this.error = 'Erreur chargement books';
          return of([]);
        })
      )
      .subscribe((data: Book[]) => {
        this.books = data;
        this.cdr.detectChanges();
      });
  }

  /* =========================
     CREATE
  ========================= */

  openCreate(): void {
    this.selectedPublishing = null;
    this.showForm = true;

    this.selectedBookId = null;

    this.formPublishing = {
      name: '',
      isbn: '',
      noTprice: 0,
      royalties: 0
    };
  }

  showCreate(): void {
    this.openCreate();
  }

  create(): void {

  this.error = null;
  this.successMessage = null;

    const isbnRegex = /^[0-9]{13}$/;

    if (!isbnRegex.test(this.formPublishing.isbn)) {

      this.error = 'ISBN invalide (13 chiffres obligatoires)';

      this.clearMessagesAfterDelay();

      return;
    }

  if (!this.selectedBookId) {

    this.error = 'Livre obligatoire';

    this.clearMessagesAfterDelay();

    return;
  }

  const payload = {
    ...this.formPublishing,
    book: { book_id: this.selectedBookId }
  };

  this.publishingService.create(payload)
    .subscribe({

      next: () => {

        this.successMessage = 'Publication créée avec succès';

              this.showForm = false;
              this.selectedPublishing = null;
              this.selectedBookId = null;

              this.formPublishing = {
                name: '',
                isbn: '',
                noTprice: 0,
                royalties: 0
              };

              this.loadPublishings();

              this.clearMessagesAfterDelay();

              this.cdr.detectChanges();
                    },

      error: err => {

        console.error(err);

        this.error = 'Erreur création publication';

        this.clearMessagesAfterDelay();
      }
    });
}

  /* =========================
     EDIT
  ========================= */

  edit(pub: any): void {
    this.selectedPublishing = pub;
    this.showForm = true;

    this.formPublishing = {
      name: pub.name,
      isbn: pub.isbn,
      noTprice: pub.noTprice,
      royalties: pub.royalties
    };

    this.selectedBookId = pub.book?.book_id ?? null;
  }

  /* =========================
     UPDATE
  ========================= */

  update(): void {

  this.error = null;
  this.successMessage = null;
    
  const isbnRegex = /^[0-9]{13}$/;

      if (!isbnRegex.test(this.formPublishing.isbn)) {

        this.error = 'ISBN invalide (13 chiffres obligatoires)';

        this.clearMessagesAfterDelay();

        return;
      }

  if (!this.selectedPublishing || !this.selectedBookId) {

    this.error = 'Livre obligatoire pour la modification';

    this.clearMessagesAfterDelay();

    return;
  }

  const payload = {
    name: this.formPublishing.name,
    isbn: this.formPublishing.isbn,
    noTprice: this.formPublishing.noTprice,
    royalties: this.formPublishing.royalties,
    book: { book_id: this.selectedBookId }
  };

  this.publishingService
    .update(this.selectedPublishing.publishingId, payload)
    .subscribe({

      next: () => {

        this.successMessage = 'Publication modifiée avec succès';

        this.clearMessagesAfterDelay();

        this.showForm = false;
        this.selectedPublishing = null;
        this.selectedBookId = null;

        this.loadPublishings();
      },

      error: err => {

        console.error(err);

        this.error = 'Erreur modification publication';

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

  this.publishingService.delete(id)
    .subscribe({

      next: () => {

        this.successMessage = 'Publication supprimée avec succès';

        this.clearMessagesAfterDelay();

        this.loadPublishings();

        this.cdr.detectChanges();
      },

      error: (err) => {

        console.error(err);

        this.error =
          'Suppression impossible : cette publication est rattachée.';

        this.clearMessagesAfterDelay();

        this.cdr.detectChanges();
      }
    });
}

  /* =========================
     CANCEL
  ========================= */

  cancel(): void {
    this.showForm = false;
    this.selectedPublishing = null;
    this.selectedBookId = null;

    this.formPublishing = {
      name: '',
      isbn: '',
      noTprice: 0,
      royalties: 0
    };

    this.error = null;
    this.successMessage = null;
  }

        /* =========================
          TIMER
        ========================= */

        private clearMessagesAfterDelay(): void {

        setTimeout(() => {

          this.error = null;
          this.successMessage = null;

          this.cdr.detectChanges();

        }, 5000);
      }
}