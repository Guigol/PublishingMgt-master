import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {  RouterModule} from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component'
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavbarComponent,
    MatCardModule,
    MatButtonModule
      ],
  templateUrl: './admin-dashboard.component.html',
   styleUrls: ['./admin-dashboard.component.css']
})

export class AdminDashboardComponent {}