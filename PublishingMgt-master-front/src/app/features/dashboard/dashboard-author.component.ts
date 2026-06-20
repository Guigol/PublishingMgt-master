import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatPaginatorModule } from '@angular/material/paginator';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { RoyaltiesService } from '../../core/services/royalties.service';
import { Royalty } from '../../core/models/royalty.model';
import { PubUser } from '../../core/models/pubuser.model';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavbarComponent, MatPaginatorModule],
  templateUrl: './dashboard-author.component.html',
  styleUrls: ['./dashboard-author.component.css']
})
export class DashboardAuthorComponent implements OnInit {

  private authService = inject(AuthService);
  private userService = inject(UserService);
  private royaltiesService = inject(RoyaltiesService);
  private cdr = inject(ChangeDetectorRef);

  user$: Observable<User | null> = this.authService.currentUser$;

  royalties: Royalty[] = [];
  authors: PubUser[] = [];
  selectedAuthorId: number | null = null;

  loading = false;
  error: string | null = null;

  selectedYear = 2025;
  yearlyTotals: any[] = [];
  monthlyDetails: any[] = [];
  selectedBook: string | null = null;

  ngOnInit() {
    this.loadYearlyRoyalties();
  }

  // =============================
  // LOAD YEARLY
  // =============================
  loadYearlyRoyalties() {
    this.loading = true;
    this.error = null;

    this.royaltiesService.getMyYearlyRoyalties(this.selectedYear)
      .pipe(
        catchError(err => {
          this.error = 'Erreur chargement année';
          this.loading = false;
          return of([]);
        })
      )
      .subscribe(data => {
        this.yearlyTotals = data || [];
        this.loading = false;
        this.cdr.detectChanges();
      });
  }

  // =============================
// SELECT BOOK
// =============================
selectBook(title: string) {

  this.selectedBook = title;
  this.loading = true;
  this.error = null;

  this.royaltiesService.getMonthlyDetails(title, this.selectedYear)
    .pipe(
      catchError(err => {
        console.error(err);
        this.error = 'Erreur chargement détail';
        this.loading = false;
        return of([]);
      })
    )
    .subscribe(data => {

  const monthOrder: { [key: string]: number } = {
    janvier: 1,
    février: 2,
    mars: 3,
    avril: 4,
    mai: 5,
    juin: 6,
    juillet: 7,
    août: 8,
    septembre: 9,
    octobre: 10,
    novembre: 11,
    décembre: 12
  };

  this.monthlyDetails = (data || []).sort((a: any, b: any) => {

    const monthA = (a.month || '').toLowerCase().trim();
    const monthB = (b.month || '').toLowerCase().trim();

    return (monthOrder[monthA] || 99) - (monthOrder[monthB] || 99);

  });

  

  this.loading = false;
  this.cdr.detectChanges();

  });
}
        
  
  // =============================
  // YEAR CHANGE
  // =============================
  onYearChange() {
    this.selectedBook = null;
    this.monthlyDetails = [];
    this.loadYearlyRoyalties();
  }

  exportToExcel(): void {

  let data: any[] = [];

  /* =========================
     VIEW 1 : BOOKS
  ========================= */

  if (!this.selectedBook) {

    data = (this.yearlyTotals || []).map(b => ({
      Livre: b.title,
      Total: b.totalAmount
    }));
  }

  /* =========================
     VIEW 2 : MONTH DETAILS
  ========================= */

  else {

    data = (this.monthlyDetails || []).map(m => ({
      Mois: m.month,
      QuantiteVendue: m.quantitySold ?? 0,
      Retours: m.quantityReturn ?? 0,
      Net: m.quantityNet ?? 0,
      Montant: m.montant
    }));
  }

  /* =========================
     CREATE XLSX
  ========================= */

  const worksheet: XLSX.WorkSheet =
    XLSX.utils.json_to_sheet(data);

  const workbook: XLSX.WorkBook = {
    Sheets: { Royalties: worksheet },
    SheetNames: ['Royalties']
  };

  const excelBuffer: any =
    XLSX.write(workbook, {
      bookType: 'xlsx',
      type: 'array'
    });

  const blob: Blob = new Blob(
    [excelBuffer],
    {
      type:
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8'
    }
  );

  const fileName = this.selectedBook
    ? `royalties_${this.selectedBook}_${this.selectedYear}.xlsx`
    : `royalties_${this.selectedYear}.xlsx`;

  saveAs(blob, fileName);
    }


     // GO BACK TO BOOKS
    goBackToBooks() {
        this.selectedBook = null;
        this.monthlyDetails = [];
        this.loading = false;
        this.error = null;

        // Angular safe refresh 
        this.cdr.detectChanges();
      }
}