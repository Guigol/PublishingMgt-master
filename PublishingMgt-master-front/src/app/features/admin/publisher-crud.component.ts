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

import { PublisherService } from '../../core/services/publisher.service';
import { Publisher } from '../../core/models/publisher.model';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';

@Component({
  selector: 'app-publisher-crud',
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
  templateUrl: './publisher-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']
})
export class PublisherCrudComponent implements OnInit, AfterViewInit {

  private publisherService = inject(PublisherService);
  private cdr = inject(ChangeDetectorRef);

  /* =========================
     TABLE + PAGINATOR
  ========================= */

  displayedColumns: string[] = [
    'publisher_id',
    'name',
    'actions'
  ];

  dataSource = new MatTableDataSource<Publisher>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  /* =========================
     DATA
  ========================= */

  publishers: Publisher[] = [];
  selectedPublisher: Publisher | null = null;

  loading = false;
  error: string | null = null;
  showForm = false;
  successMessage: string | null = null;

  formPublisher: any = {
    name: ''
  };

  /* =========================
     LIFECYCLE
  ========================= */

  ngOnInit(): void {
    this.loadPublishers();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  /* =========================
     LOAD PUBLISHERS
  ========================= */

  loadPublishers(): void {
    this.loading = true;
    this.error = null;

    this.publisherService.getAll()
      .pipe(
        catchError((err) => {
          console.error(err);
          this.error = 'Erreur chargement publishers';
          this.loading = false;
          this.cdr.detectChanges();
          return of([]);
        })
      )
      .subscribe((data: Publisher[]) => {
       

        this.publishers = data;
        this.dataSource.data = data;

        this.loading = false;
        this.cdr.detectChanges();

        if (this.paginator) {
          this.dataSource.paginator = this.paginator;
        }
      });
  }

  /* =========================
     CREATE
  ========================= */

  openCreate(): void {
    this.selectedPublisher = null;
    this.showForm = true;

    this.formPublisher = {
      name: ''
    };
  }

  showCreate(): void {
    this.openCreate();
  }

  create(): void {

  this.error = null;
  this.successMessage = null;

  this.publisherService.create(this.formPublisher)
    .subscribe({

      next: () => {

        this.successMessage = 'Editeur créé avec succès';

        this.clearMessagesAfterDelay();

        this.resetForm();
        this.loadPublishers();
      },

      error: (err) => {

        console.error(err);

        this.error = 'Erreur création éditeur';

        this.clearMessagesAfterDelay();
      }
    });
}

  /* =========================
     EDIT
  ========================= */

  edit(pub: Publisher): void {
    this.selectedPublisher = pub;
    this.showForm = true;

    this.formPublisher = {
      name: pub.name
    };
  }

  /* =========================
     UPDATE
  ========================= */

  update(): void {

  if (!this.selectedPublisher) return;

  this.error = null;
  this.successMessage = null;

  this.publisherService
    .update(this.selectedPublisher.publisher_id, this.formPublisher)
    .subscribe({

      next: () => {

        this.successMessage = 'Editeur modifié avec succès';

        this.clearMessagesAfterDelay();

        this.resetForm();
        this.loadPublishers();
      },

      error: (err) => {

        console.error(err);

        this.error = 'Erreur modification éditeur';

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

  this.publisherService.delete(id)
    .subscribe({

      next: () => {

        this.successMessage = 'Editeur supprimé avec succès';

        this.clearMessagesAfterDelay();

        this.loadPublishers();

        this.cdr.detectChanges();
      },

      error: (err) => {

        console.error(err);

        this.error = 'Impossible de supprimer cet éditeur';

        this.clearMessagesAfterDelay();

        this.cdr.detectChanges();
      }
    });
}

  /* =========================
     FORM HELPERS
  ========================= */

  cancel(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.showForm = false;
    this.selectedPublisher = null;

    this.formPublisher = {
      name: ''
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