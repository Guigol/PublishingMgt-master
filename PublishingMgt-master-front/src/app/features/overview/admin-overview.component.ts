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
  selector: 'app-admin-overview',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BaseChartDirective,
    NavbarComponent
  ],
  templateUrl: './admin-overview.component.html',
  styleUrls: ['./admin-overview.component.css']
})
export class AdminOverviewComponent implements OnInit {

  selectedYear = 2025;

  sales: BookSale[] = [];

  totalSold = 0;
  totalReturns = 0;
  totalNet = 0;
  totalAmount = 0;

  topBook = '';

  // BAR CHART (monthly revenue - CA mensuel)
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

      this.refresh();

      this.cdr.detectChanges();
    });
  }

  // =============================
  refresh() {
    this.compute();
    this.buildChart();

    // IMPORTANT : Enforce Angular chart update
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

    const estimatedPrice = 10;

    this.totalAmount = filtered.reduce((sum, s) => {
      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      return sum + net * estimatedPrice * (1 - (s.averageDiscount || 0));
    }, 0);

    const map = new Map<string, number>();

    filtered.forEach(s => {
      const title = s.book?.title ?? s.publishing?.book?.title ?? 'Livre inconnu';
      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      const value = net * estimatedPrice * (1 - (s.averageDiscount || 0));

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
    const estimatedPrice = 10;

    filtered.forEach(s => {

      const net = (s.quantitySold || 0) - (s.quantityReturn || 0);
      const value = net * estimatedPrice * (1 - (s.averageDiscount || 0));

      monthMap.set(s.month, (monthMap.get(s.month) || 0) + value);
    });

    const months = [...monthMap.keys()].sort((a, b) => a - b);

    this.barChartData = {
      labels: months.map(m => this.monthLabel(m)),
      datasets: [
        {
          data: months.map(m => monthMap.get(m) || 0),
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
  //year change
  // =============================
  onYearChange() {
    this.refresh();
  }
}