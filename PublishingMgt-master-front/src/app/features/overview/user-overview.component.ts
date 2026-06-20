import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

import { BookSaleService } from '../../core/services/book-sale.service';
import { BookSale } from '../../core/models/book-sale.model';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-user-overview',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BaseChartDirective,
    NavbarComponent
  ],
  templateUrl: './user-overview.component.html',
  styleUrls: ['./admin-overview.component.css']
})
export class UserOverviewComponent implements OnInit {

  // année par défaut
  selectedYear = 2025;

  sales: BookSale[] = [];

  totalSold = 0;
  totalReturns = 0;
  totalNet = 0;
  totalAmount = 0;

  topBook = '';

  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'CA mensuel (€)' }
    ]
  };

  constructor(
    private bookSaleService: BookSaleService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initDashboard();
  }

  // =============================
  // LOAD DATA
  // =============================
  initDashboard() {
    this.bookSaleService.getAll().subscribe(data => {

      this.sales = data || [];

      
      // stabilisation 
      this.cdr.detectChanges();

      this.refresh();

      // re-stabilization after calculation
      this.cdr.detectChanges();
    });
  }

  // =============================
  refresh() {
    this.compute();
    this.buildChart();

    this.barChartData = { ...this.barChartData };
  }

  // =============================
  compute() {

    const filtered = this.sales.filter(s =>
      Number(s.year) === Number(this.selectedYear)
    );

    this.totalSold = filtered.reduce((a, s) => a + (s.quantitySold || 0), 0);
    this.totalReturns = filtered.reduce((a, s) => a + (s.quantityReturn || 0), 0);
    this.totalNet = this.totalSold - this.totalReturns;

    this.totalAmount = filtered.reduce((sum, s) => {

      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      const discount = s.averageDiscount || 0;
      const estimatedPrice = 10;

      return sum + net * estimatedPrice * (1 - discount);

    }, 0);

    const map = new Map<string, number>();

    filtered.forEach(s => {

      const title = s.book?.title ?? s.publishing?.book?.title ?? 'Livre inconnu';
      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      const discount = s.averageDiscount || 0;
      const estimatedPrice = 10;

      const value = net * estimatedPrice * (1 - discount);

      map.set(title, (map.get(title) || 0) + value);
    });

    this.topBook =
      [...map.entries()]
        .sort((a, b) => b[1] - a[1])[0]?.[0] || 'N/A';
  }

  // =============================
  buildChart() {

    const filtered = this.sales.filter(s =>
      Number(s.year) === Number(this.selectedYear)
    );

    const monthMap = new Map<number, number>();

    filtered.forEach(s => {

      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      const discount = s.averageDiscount || 0;
      const estimatedPrice = 10;

      const value = net * estimatedPrice * (1 - discount);

      monthMap.set(s.month, (monthMap.get(s.month) || 0) + value);
    });

    const sortedMonths = [...monthMap.keys()].sort((a, b) => a - b);

    this.barChartData = {
      labels: sortedMonths.map(m => this.monthLabel(m)),
      datasets: [
        {
          data: sortedMonths.map(m => monthMap.get(m) || 0),
          label: 'CA mensuel (€)'
        }
      ]
    };
  }

  // =============================
  //month label
  // =============================
  monthLabel(m: number): string {
    const names = [
      '',
      'Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Jun',
      'Jul', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'
    ];
    return names[m] || 'Inconnu';
  }

  // =============================
  // year change
  // =============================
  onYearChange() {
    this.refresh();
  }
}