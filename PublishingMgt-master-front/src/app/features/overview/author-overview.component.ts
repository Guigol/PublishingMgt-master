import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

import { RoyaltiesService } from '../../core/services/royalties.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';

@Component({
  selector: 'app-author-overview',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    BaseChartDirective,
    NavbarComponent
  ],
  templateUrl: './author-overview.component.html',
  styleUrls: ['./author-overview.component.css']
})
export class AuthorOverviewComponent implements OnInit {

  selectedYear = 2025;

  yearlyTotals: BookYearRoyaltyDTO[] = [];

  totalSold = 0;
  totalAmount = 0;
  topBook = 'N/A';

  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [
      {
        data: [],
        label: 'Royalties par livre (€)'
      }
    ]
  };

  constructor(
    private royaltiesService: RoyaltiesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOverview();
  }

  loadOverview() {
    this.royaltiesService
      .getMyYearlyRoyalties(this.selectedYear)
      .subscribe(data => {
       
        this.yearlyTotals = data || [];

        this.compute();
        this.buildChart();

        this.cdr.detectChanges();
      });
  }

  compute() {
    this.totalSold = this.yearlyTotals.reduce(
  (sum, item) => sum + (item.quantitySold || 0),
  0
);

    this.totalAmount = this.yearlyTotals.reduce(
      (sum, item) => sum + (item.totalAmount || 0),
      0
    );

    const best = [...this.yearlyTotals]
      .sort((a, b) => (b.totalAmount || 0) - (a.totalAmount || 0))[0];

    this.topBook = best?.title || 'N/A';
  }

  buildChart() {
    this.barChartData = {
      labels: this.yearlyTotals.map(item => item.title),
      datasets: [
        {
          data: this.yearlyTotals.map(item => item.totalAmount || 0),
          label: 'Royalties par livre (€)'
        }
      ]
    };

    this.barChartData = { ...this.barChartData };
  }

  onYearChange() {
    this.loadOverview();
  }
}
