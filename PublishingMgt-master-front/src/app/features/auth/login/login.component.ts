import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  login = '';
  password = '';
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {

    this.authService.login({
      login: this.login,
      password: this.password
    }).subscribe({
      next: (user) => {

        switch (user.role) {
          case 'AUTHOR':
            this.router.navigate(['/author-overview']);
            break;
          case 'MANAGER':
            this.router.navigate(['/mgr-overview']);
            break;
          case 'USER':
            this.router.navigate(['/user-overview']);
            break;
          case 'ADMIN':
            this.router.navigate(['/admin-overview']);
            break;
          default:
            this.router.navigate(['/']);
        }

      },
      error: () => {
        this.error = "Login incorrect";
      }
    });

  }

}