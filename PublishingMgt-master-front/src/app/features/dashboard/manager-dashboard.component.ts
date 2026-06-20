import { CommonModule } from '@angular/common';
import {  RouterModule} from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { RoyaltiesService } from '../../core/services/royalties.service';
import { Component, inject, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, 
            RouterModule,
            NavbarComponent,
            MatCardModule,
            MatButtonModule],
  templateUrl: './manager-dashboard.component.html',
   styleUrls: ['./admin-dashboard.component.css']
})

export class ManagerDashboardComponent {
    protected royaltiesService = inject(RoyaltiesService);
}