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
import { RouterModule } from '@angular/router';

import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

// SERVICES
import { BookSaleService } from '../../core/services/book-sale.service';
import { BookService } from '../../core/services/book.service';
import { PublishingService } from '../../core/services/publishing.service';

// MODELS
import { BookSale } from '../../core/models/book-sale.model';
import { Book } from '../../core/models/book.model';
import { Publishing } from '../../core/models/publishing.model';

// NAVBAR
import { NavbarComponent } from '../../shared/navbar/navbar.component';

// MATERIAL
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,

    NavbarComponent,

    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatSidenavModule
  ],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit, AfterViewInit {

  private saleService = inject(BookSaleService);
  private bookService = inject(BookService);
  private pubService = inject(PublishingService);
  private cdr = inject(ChangeDetectorRef);

  // =====================
  // TABLE
  // =====================
  dataSource = new MatTableDataSource<BookSale>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = [
    'id',
    'book',
    'publishing',
    'month',
    'year',
    'quantitySold',
    'quantityReturn',
    'averageDiscount'
  ];

  // =====================
  // DATA
  // =====================
  sales: BookSale[] = [];
  books: Book[] = [];
  publishings: Publishing[] = [];

  loading = false;
  error: string | null = null;

  // =====================
  // SEARCH
  // =====================
  search = {
    isbn: '',
    book: '',
    publishing: ''
  };

  // =====================
  // INIT
  // =====================
  ngOnInit(): void {
    this.load();
    this.loadBooks();
    this.loadPublishings();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  // =====================
  // LOAD SALES
  // =====================
  load(): void {

    this.loading = true;

    this.saleService.getAll()
      .pipe(
        catchError(() => {
          this.error = 'Erreur chargement sales';
          this.loading = false;
          this.cdr.detectChanges();
          return of([]);
        })
      )
      .subscribe(data => {

        this.sales = data || [];

        this.dataSource = new MatTableDataSource(this.sales);

        // rebind après recréation datasource
        setTimeout(() => {
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        });

        this.loading = false;
        this.cdr.detectChanges();
      });
  }

  // =====================
  // LOAD BOOKS
  // =====================
  loadBooks(): void {
    this.bookService.getAll()
      .pipe(catchError(() => of([])))
      .subscribe(data => this.books = data);
  }

  // =====================
  // LOAD PUBLISHINGS
  // =====================
  loadPublishings(): void {
    this.pubService.getAll()
      .pipe(catchError(() => of([])))
      .subscribe(data => this.publishings = data);
  }

  // =====================
  // FILTER
  // =====================
  applyFilters(): void {

    this.dataSource.filterPredicate = (data: BookSale, filter: string) => {

      const search = JSON.parse(filter);

      const isbn = (data.publishing?.isbn || '').toLowerCase();
      const book = (data.book?.title || '').toLowerCase();
      const publishing = (data.publishing?.name || '').toLowerCase();

      return (
        (!search.isbn || isbn.includes(search.isbn)) &&
        (!search.book || book.includes(search.book)) &&
        (!search.publishing || publishing.includes(search.publishing))
      );
    };

    this.dataSource.filter = JSON.stringify({
      isbn: this.search.isbn.trim().toLowerCase(),
      book: this.search.book.trim().toLowerCase(),
      publishing: this.search.publishing.trim().toLowerCase()
    });

    this.dataSource.paginator?.firstPage();
  }

  // =====================
  // UTIL
  // =====================
  getMonthName(m: number): string {
    const months = [
      'Janvier','Février','Mars','Avril','Mai','Juin',
      'Juillet','Août','Septembre','Octobre','Novembre','Décembre'
    ];
    return months[m - 1] ?? '';
  }
}