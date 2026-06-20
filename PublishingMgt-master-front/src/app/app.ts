import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from './shared/footer/footer.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, 
            MatTableModule,
            MatPaginatorModule,
            MatSortModule,
            MatButtonModule,
           FooterComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css'] 
})
export class App {
  protected readonly title = signal('publishfrontend');
}