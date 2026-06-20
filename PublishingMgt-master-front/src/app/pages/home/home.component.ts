import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginComponent } from '../../features/auth/login/login.component';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, LoginComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent {
  showLogin = false;
}
