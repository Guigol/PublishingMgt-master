import {
  Component,
  inject,
  OnInit,
  ChangeDetectorRef,
  ViewChild
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { catchError, of } from 'rxjs';

import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSidenavModule } from '@angular/material/sidenav';
import {
  MatTableModule,
  MatTableDataSource
} from '@angular/material/table';

import { MatButtonModule } from '@angular/material/button';

import { AuthorParticipationService } from '../../core/services/author-participation.service';
import { AuthService } from '../../core/services/auth.service';
import { AuthorParticipation } from '../../core/models/author-participation.model';
import { BookService } from '../../core/services/book.service';

@Component({
  selector: 'app-author-participation-manager',
  standalone: true,

  imports: [
    CommonModule,
    FormsModule,

    MatSidenavModule,
    MatTableModule,
    MatButtonModule,
    MatPaginatorModule
  ],

  templateUrl: './author-participation-manager.component.html',
  styleUrls: ['./author-participation-manager.component.css']
})
export class AuthorParticipationManagerComponent implements OnInit {

  private service = inject(AuthorParticipationService);
  private authService = inject(AuthService);
  private bookService = inject(BookService);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild(MatPaginator)
  paginator!: MatPaginator;

  participations: AuthorParticipation[] = [];

  authors: any[] = [];
  books: any[] = [];

  dataSource =
    new MatTableDataSource<AuthorParticipation>([]);

  displayedColumns = [
    'id',
    'author',
    'book',
    'royalties',
    'actions'
  ];

  search = {
    author: '',
    book: ''
  };

  selected: AuthorParticipation | null = null;

  loading = false;
  error: string | null = null;
  successMessage: string | null = null;
  showForm = false;

  form: any = {
    authorId: null,
    bookId: null,
    pctRateRoyalties: null
  };

  ngOnInit(): void {
    this.load();
    this.loadAuthors();
    this.loadBooks();
  }

  // =====================
  // LOAD PARTICIPATIONS
  // =====================
  load(): void {

    this.loading = true;

    this.service.getAll()
      .pipe(

        catchError(() => {

          this.error = 'Erreur chargement';

          this.loading = false;

          this.cdr.detectChanges();

          return of([]);

        })

      )
      .subscribe(data => {

        this.participations = data;

        this.dataSource.data = this.participations;

        this.dataSource.paginator = this.paginator;

        this.loading = false;

        this.cdr.detectChanges();

      });
  }

  // =====================
  // LOAD AUTHORS (FROM AuthService)
  // =====================
  loadAuthors(): void {

  this.authService.getAuthors()
    .subscribe({

      next: data => {

        this.authors = data.map(u => ({
          authorId: u.authorId,
          name: `${u.firstname ?? ''} ${u.surname ?? ''}`.trim()
        }));

        this.cdr.detectChanges();
      },

      error: () => {

        this.error = 'Erreur chargement auteurs';
      }

    });
}

 // =====================
  // LOAD BOOKS
  // =====================
loadBooks(): void {

  this.bookService.getAll()
    .subscribe({

      next: data => {

        this.books = data;

        this.cdr.detectChanges();
      },

      error: () => {

        this.error = 'Erreur chargement livres';
      }

    });
}

  // =====================
  // FILTER
  // =====================
  applyFilters(): void {

    this.dataSource.filterPredicate =
      (data: AuthorParticipation, filter: string) => {

        const search = JSON.parse(filter);

        const authorMatch =
          !search.author ||
          data.authorName
            ?.toLowerCase()
            .includes(search.author);

        const bookMatch =
          !search.book ||
          data.bookTitle
            ?.toLowerCase()
            .includes(search.book);

        return authorMatch && bookMatch;
      };

    this.dataSource.filter = JSON.stringify({

      author: this.search.author.toLowerCase(),

      book: this.search.book.toLowerCase()

    });

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }

    this.cdr.detectChanges();
  }

  // =====================
  // CREATE
  // =====================
  showCreate(): void {

    this.selected = null;

    this.showForm = true;

    this.form = {
      authorId: null,
      bookId: null,
      pctRateRoyalties: null
    };
  }

 create(): void {

      this.error = null;
      this.successMessage = null;

      this.service.create(this.form)
        .subscribe({

          next: () => {

            this.successMessage =
              'Participation créée avec succès';

            this.showForm = false;

            this.load();

            this.clearMessagesAfterDelay();

            this.cdr.detectChanges();
          },

          error: () => {

            this.error = 'Erreur création participation';

            this.clearMessagesAfterDelay();

            this.cdr.detectChanges();
          }

        });
    }

  // =====================
  // EDIT
  // =====================
  edit(p: AuthorParticipation): void {

    this.selected = p;

    this.showForm = true;

    this.form = {

      authorId: p.authorId,

      bookId: p.bookId,

      pctRateRoyalties: p.pctRateRoyalties
    };
  }

  // =====================
  // UPDATE
  // =====================

  update(): void {

      if (!this.selected) return;

      this.error = null;
      this.successMessage = null;

      this.service.update(this.selected.id, this.form)
        .subscribe({

          next: () => {

            this.successMessage =
              'Participation modifiée avec succès';

            this.showForm = false;

            this.selected = null;

            this.load();

            this.clearMessagesAfterDelay();

            this.cdr.detectChanges();
          },

          error: () => {

            this.error = 'Erreur modification participation';

            this.clearMessagesAfterDelay();

            this.cdr.detectChanges();
          }

        });
    }

  // =====================
  // DELETE
  // =====================
 delete(id: number): void {

  this.error = null;
  this.successMessage = null;

  this.service.delete(id)
    .subscribe({

      next: () => {

        this.successMessage =
          'Participation supprimée avec succès';

        this.load();

        this.clearMessagesAfterDelay();

        this.cdr.detectChanges();
      },

      error: () => {

        this.error = 'Erreur suppression participation';

        this.clearMessagesAfterDelay();

        this.cdr.detectChanges();
      }

    });
}

  // =====================
  // CANCEL
  // =====================
  cancel(): void {

    this.showForm = false;

    this.selected = null;
  }

   // =====================
  // TIMER
  // =====================

        private clearMessagesAfterDelay(): void {

        setTimeout(() => {

          this.error = null;
          this.successMessage = null;

          this.cdr.detectChanges();

         }, 3000);
      }
}