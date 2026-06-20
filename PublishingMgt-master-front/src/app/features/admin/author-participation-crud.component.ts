import {
  Component,
  inject,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthorParticipationService } from '../../core/services/author-participation.service';
import { AuthorParticipation } from '../../core/models/author-participation.model';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-author-participation-crud',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './author-participation-crud.component.html',
  styleUrls: ['./author-participation-crud.component.css']
})
export class AuthorParticipationCrudComponent implements OnInit {

  private service = inject(AuthorParticipationService);
  private cdr = inject(ChangeDetectorRef);

  participations: AuthorParticipation[] = [];
  filteredParticipations: AuthorParticipation[] = [];

  grouped: any[] = [];
  paginatedGrouped: any[] = [];

  /* PAGINATOR AUTHOR */
  paginatedAuthorParticipations: AuthorParticipation[] = [];
  authorCurrentPage = 1;
  authorItemsPerPage = 6;
  authorTotalPages = 1;

  loading = false;
  error: string | null = null;

  /* DEFAULT */
  sortMode: 'book' | 'author' = 'author';

  /* SEARCH */
  searchAuthor = '';
  searchBook = '';

  /* BOOK PAGINATOR */
  currentPage = 1;
  itemsPerPage = 2;
  totalPages = 1;

  ngOnInit() {
    this.loadParticipations();
  }

  loadParticipations() {
    this.loading = true;

    this.service.getAll()
      .pipe(
        catchError(() => {
          this.error = 'Erreur chargement author participations';
          this.loading = false;
          this.cdr.detectChanges();
          return of([]);
        })
      )
      .subscribe(data => {
        this.participations = data;
        this.applyView();
        this.loading = false;
        this.cdr.detectChanges();
      });
  }

  setSort(mode: 'book' | 'author') {
    this.sortMode = mode;

    /* paginator reset book */
    this.currentPage = 1;

    /* paginator reset,author */
    this.authorCurrentPage = 1;

    this.applyView();
  }

  applyView() {

    /* SORT BY AUTHOR */
    if (this.sortMode === 'author') {
      this.grouped = [];
      this.paginatedGrouped = [];

      this.filteredParticipations = [...this.participations]
        .filter(p =>
          p.authorName
            .toLowerCase()
            .includes(this.searchAuthor.toLowerCase())
        )
        .sort((a, b) =>
          a.authorName.localeCompare(b.authorName)
        );

      /* paginator */
      this.authorTotalPages = Math.ceil(
        this.filteredParticipations.length / this.authorItemsPerPage
      );

      this.updateAuthorPagination();

      return;
    }

    /* GROUP BY BOOK + SEARCH */
    const filtered = this.participations.filter(p =>
      p.bookTitle
        .toLowerCase()
        .includes(this.searchBook.toLowerCase())
    );

    const map = new Map<string, any>();

    filtered.forEach(p => {
      if (!map.has(p.bookTitle)) {
        map.set(p.bookTitle, {
          bookTitle: p.bookTitle,
          items: []
        });
      }

      map.get(p.bookTitle).items.push(p);
    });

    this.grouped = Array.from(map.values());

    this.totalPages = Math.ceil(
      this.grouped.length / this.itemsPerPage
    );

    this.updatePagination();
  }

  /* =========================
     BOOK PAGINATOR 
  ========================= */

  updatePagination() {
    const start =
      (this.currentPage - 1) * this.itemsPerPage;

    const end =
      start + this.itemsPerPage;

    this.paginatedGrouped =
      this.grouped.slice(start, end);
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  /* =========================
     PAGINATION AUTHOR
  ========================= */

  updateAuthorPagination() {
    const start =
      (this.authorCurrentPage - 1) * this.authorItemsPerPage;

    const end =
      start + this.authorItemsPerPage;

    this.paginatedAuthorParticipations =
      this.filteredParticipations.slice(start, end);
  }

  nextAuthorPage() {
    if (this.authorCurrentPage < this.authorTotalPages) {
      this.authorCurrentPage++;
      this.updateAuthorPagination();
    }
  }

  previousAuthorPage() {
    if (this.authorCurrentPage > 1) {
      this.authorCurrentPage--;
      this.updateAuthorPagination();
    }
  }
}