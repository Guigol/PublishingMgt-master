import { Component, inject, OnInit, ChangeDetectorRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';

import { BookSaleService } from '../../core/services/book-sale.service';
import { BookService } from '../../core/services/book.service';
import { PublishingService } from '../../core/services/publishing.service';

import { BookSale } from '../../core/models/book-sale.model';
import { Book } from '../../core/models/book.model';
import { Publishing } from '../../core/models/publishing.model';

import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-book-sales-crud',
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
  templateUrl: './book-sales-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']
})
export class BookSalesCrudComponent implements OnInit, AfterViewInit {

  private saleService = inject(BookSaleService);
  private bookService = inject(BookService);
  private pubService = inject(PublishingService);
  private cdr = inject(ChangeDetectorRef);

  sales: BookSale[] = [];
  books: Book[] = [];
  publishings: Publishing[] = [];

  search = {
    isbn: '',
    book: '',
    publishing: ''
  };

  dataSource = new MatTableDataSource<BookSale>();

  selected: BookSale | null = null;
  drawerOpen = false;
  isbnError = false;
  error: string | null = null;
  successMessage: string | null = null;

  currentDate = new Date();
  currentYear = this.currentDate.getFullYear();
  currentMonth = this.currentDate.getMonth() + 1;

  form: any = {
    bookId: null,
    publishingIsbn: null,
    month: null,
    year: null,
    quantitySold: 0,
    quantityReturn: 0,
    averageDiscount: 0
  };

  displayedColumns: string[] = [
    'id',
    'isbn',
    'book',
    'publishing',
    'month',
    'year',
    'quantitySold',
    'quantityReturn',
    'averageDiscount',
    'actions'
  ];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  monthNames = [
    'Janvier','Février','Mars','Avril','Mai','Juin',
    'Juillet','Août','Septembre','Octobre','Novembre','Décembre'
  ];

  ngOnInit() {
    this.load();
    this.loadBooks();
    this.loadPublishings();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  get availableMonths(): string[] {

    if (this.form.year === this.currentYear) {
      return this.monthNames.slice(0, this.currentMonth);
    }

    return this.monthNames;
  }

  // ---------------- LOAD ----------------

  load() {
    this.saleService.getAll()
      .pipe(catchError(() => of([])))
      .subscribe(data => {
        this.dataSource.data = data;

        setTimeout(() => {
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });

        this.cdr.detectChanges();
      });
  }

  loadBooks() {
    this.bookService.getAll()
      .pipe(catchError(() => of([])))
      .subscribe(data => this.books = data);
  }

  loadPublishings() {
    this.pubService.getAll()
      .pipe(catchError(() => of([])))
      .subscribe(data => {
       this.publishings = data;
      });
  }

  // ---------------- DRAWER ----------------

  openCreate() {
    this.selected = null;
    this.form = this.emptyForm();
    this.isbnError = false;
    this.drawerOpen = true;
  }

  edit(row: BookSale) {
    this.selected = row;

    this.form = {
      bookId: row.bookId ?? null,
      publishingIsbn: row.publishing?.isbn ?? null,
      month: row.month,
      year: row.year,
      quantitySold: row.quantitySold,
      quantityReturn: row.quantityReturn,
      averageDiscount: row.averageDiscount
    };

    this.isbnError = false;

    this.drawerOpen = true;
  }

  closeDrawer() {
    this.drawerOpen = false;
    this.selected = null;
  }

  onBookChange() {
    this.validateIsbn();
  }

  onYearChange() {

    if (this.form.year > this.currentYear) {
      this.form.year = this.currentYear;
    }

    if (
      this.form.year === this.currentYear &&
      this.form.month > this.currentMonth
    ) {
      this.form.month = null;
    }
  }

  // ---------------- SAVE ----------------

  save() {

  this.error = null;
  this.successMessage = null;

  if (this.isbnError) {
    this.error = 'ISBN invalide ou non associé à un livre';
    this.clearMessagesAfterDelay();
    return;
  }

  if (this.form.year > this.currentYear) {
    this.error = 'Année invalide';
    this.clearMessagesAfterDelay();
    return;
  }

  if (
    this.form.year === this.currentYear &&
    this.form.month > this.currentMonth
  ) {
    this.error = 'Mois invalide';
    this.clearMessagesAfterDelay();
    return;
  }

  const payload = this.mapFormToPayload();

  if (this.selected) {

    this.saleService.update(this.selected.id, payload)
      .subscribe({

        next: () => {

          this.successMessage =
            'Succès modification Vente';

          this.clearMessagesAfterDelay();

          this.closeDrawer();
          this.load();
        },

        error: err => {

          this.error =
            'Échec modification Vente';

          this.clearMessagesAfterDelay();

          console.error(err);
        }
      });

  } else {

    this.saleService.create(payload)
      .subscribe({

        next: () => {

          this.successMessage =
            'Vente créée avec succès';

          this.clearMessagesAfterDelay();

          this.closeDrawer();
          this.load();
        },

        error: err => {

          this.error =
            'Échec création Vente';

          this.clearMessagesAfterDelay();

          console.error(err);
        }
      });
  }
}

// ---------------- DELETE ----------------

delete(id: number) {

  this.error = null;
  this.successMessage = null;

  this.saleService.delete(id)
    .subscribe({

      next: () => {

        this.successMessage =
          'Succès suppression Vente';

        this.clearMessagesAfterDelay();

        this.load();
      },

      error: err => {

        this.error =
          'Cette vente n’a pu être supprimée';

        this.clearMessagesAfterDelay();

        console.error(err);
      }
    });
}

  // ---------------- MAPPING ----------------

  private mapFormToPayload(): any {
    return {
      month: this.form.month,
      year: this.form.year,
      quantitySold: this.form.quantitySold,
      quantityReturn: this.form.quantityReturn,
      averageDiscount: this.form.averageDiscount,

      book: {
        book_id: this.form.bookId
      },

      publishing: {
        isbn: this.form.publishingIsbn
      }
    };
  }

  private emptyForm() {
    return {
      bookId: null,
      publishingIsbn: null,
      month: null,
      year: null,
      quantitySold: 0,
      quantityReturn: 0,
      averageDiscount: 0
    };
  }

  // ---------------- DISPLAY ----------------

  getMonthName(m: number) {
    return this.monthNames[m - 1] ?? '';
  }

  // ---------------- FILTER ----------------

  applyFilters() {
    this.dataSource.filterPredicate = (data: BookSale, filter: string) => {
      const s = JSON.parse(filter);

      const isbn = (data.publishing?.isbn || '').toLowerCase();
      const book = (data.book?.title || '').toLowerCase();
      const pub = (data.publishing?.name || '').toLowerCase();

      return (!s.isbn || isbn.includes(s.isbn))
          && (!s.book || book.includes(s.book))
          && (!s.publishing || pub.includes(s.publishing));
    };

    this.dataSource.filter = JSON.stringify({
      isbn: this.search.isbn.trim().toLowerCase(),
      book: this.search.book.trim().toLowerCase(),
      publishing: this.search.publishing.trim().toLowerCase()
    });

    this.dataSource.paginator?.firstPage();
  }

  // ---------------- VALIDATION ----------------

  validateIsbn() {

    this.isbnError = false;

    const isbn = this.form.publishingIsbn?.trim();
    const bookId = Number(this.form.bookId);

    if (!isbn || !bookId) {
      return;
    }

    const pub = this.publishings.find(
      p => p.isbn?.trim() === isbn
    );

    // ISBN not available
    if (!pub) {
      this.isbnError = true;
      return;
    }

    // ISBN association <-> book
    if (Number(pub.book?.book_id) !== bookId) {
      this.isbnError = true;
      return;
    }

    this.isbnError = false;
  }

  // --------- MESSAGES -------------------

private clearMessagesAfterDelay() {

  setTimeout(() => {

    this.successMessage = null;
    this.error = null;

    this.cdr.detectChanges();

  }, 3000);
}
}