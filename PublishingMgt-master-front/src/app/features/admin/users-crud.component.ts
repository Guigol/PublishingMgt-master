import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { PubUser } from '../../core/models/pubuser.model';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ChangeDetectorRef } from '@angular/core';
import { ViewChild } from '@angular/core';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';


@Component({
  selector: 'app-users-crud',
  standalone: true,
  imports: [CommonModule, 
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatButtonModule,
    MatSidenavModule],
  templateUrl: './users-crud.component.html',
  styleUrls: ['./book-sales-crud.component.css']

})

export class UsersCrudComponent implements OnInit {
  private authService = inject(AuthService);
  private userService = inject(UserService);

  dataSource = new MatTableDataSource<PubUser>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  selectedUser: PubUser | null = null;
  loading = false;
  error: string | null = null;
  successMessage: string | null = null;

  showForm = false;

  displayedColumns: string[] = [
  'authorId',
  'createdAt',
  'id',
  'login',
  'password',
  'role',
  'actions'
];

  // Initialized form with author
  formUser: any = {
    login: '',
    role: 'AUTHOR',
    password: '',
    author: { firstname: '', surname: '' }
  };

  ngOnInit() {

     this.dataSource.filterPredicate =
    (data: PubUser, filter: string) => {

      return data.login
        ?.toLowerCase()
        .includes(filter);
    };

    this.loadUsers();
  }

private cdr = inject(ChangeDetectorRef);
  loadUsers() {
  this.loading = true;

  this.userService.getAllUsers()
    .pipe(
      catchError(() => {
        this.error = 'Erreur chargement users';
        this.loading = false;
        this.cdr.detectChanges();   
        return of([]);
      })
    )
    .subscribe(data => {
      this.dataSource.data = data;
      this.dataSource.paginator = this.paginator;
      this.loading = false;
      this.cdr.detectChanges();    
    });
}

  edit(user: PubUser) {
  this.selectedUser = user;
  this.showForm = true;

  this.formUser = {
    login: user.login,
    role: user.role,
    password: '',
    author: {
      firstname: user.author?.firstname || '',
      surname: user.author?.surname || ''
    }
  };
}

  create() {
  this.error = null;
  this.successMessage = null;

  if (!this.formUser.login || !this.formUser.password) {
    this.error = 'Login et mot de passe obligatoires';
    return;
  }

  const payload = {
    login: this.formUser.login,
    password: this.formUser.password,
    role: this.formUser.role,
    author: {
      firstname: this.formUser.author.firstname,
      surname: this.formUser.author.surname
    }
  };

  this.userService.createUser(payload)
    .subscribe({
      next: () => {

        this.successMessage = 'Succès création utilisateur';
        this.clearMessagesAfterDelay();

        this.formUser = {
          login: '',
          role: 'AUTHOR',
          password: '',
          author: { firstname: '', surname: '' }
        };

        this.showForm = false;

        this.loadUsers();
      },

      error: err => {
        this.error = 'Échec création utilisateur';
        this.clearMessagesAfterDelay();
        console.error(err);
      }
    });
}


  update() {

  this.error = null;
  this.successMessage = null;

  if (!this.selectedUser) return;

  const payload = {
    login: this.formUser.login,
    password: this.formUser.password || undefined,
    role: this.formUser.role,
    author: {
      firstname: this.formUser.author.firstname,
      surname: this.formUser.author.surname
    }
  };

  this.userService.updateUser(this.selectedUser.id, payload)
    .subscribe({

      next: () => {

        this.successMessage = 'Succès modification User';
        this.clearMessagesAfterDelay();

        this.selectedUser = null;

        this.formUser = {
          login: '',
          role: 'AUTHOR',
          password: '',
          author: { firstname: '', surname: '' }
        };

        this.showForm = false;

        this.loadUsers();
      },

      error: err => {

        this.error = 'Échec modification User';
        this.clearMessagesAfterDelay();

        console.error(err);
      }
    });
}


  delete(user: PubUser) {

  this.error = null;
  this.successMessage = null;

  if (user.role === 'AUTHOR') {

    this.error =
      'Merci de convertir l’auteur en USER pour le supprimer';
      this.clearMessagesAfterDelay();

    return;
  }

  this.userService.deleteUser(user.id)
    .subscribe({

      next: () => {

        this.successMessage =
          'Succès suppression User';

        this.loadUsers();
      },

      error: err => {

        this.error =
          'Échec suppression User';
          this.clearMessagesAfterDelay();

        console.error(err);
      }
    });
  }

  // --------- MESSAGES -------------------

            private clearMessagesAfterDelay() {

              setTimeout(() => {

                this.successMessage = null;
                this.error = null;

                this.cdr.detectChanges();

              }, 3000);

            }

          cancel() {
          this.selectedUser = null;
          this.showForm = false;
        }

        showCreate() {

          this.selectedUser = null;

          this.showForm = true;

          this.formUser = {
            login: '',
            role: 'AUTHOR',
            password: '',
            author: {
              firstname: '',
              surname: ''
            }
          };
        }


// --------- FILTER -------------------
        applyFilter(event: Event) {

          const filterValue = (event.target as HTMLInputElement)
            .value;

          this.dataSource.filter = filterValue
            .trim()
            .toLowerCase();

          if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
          }
        }

}
  
