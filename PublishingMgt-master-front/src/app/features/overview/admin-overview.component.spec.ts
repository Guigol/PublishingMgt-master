import { TestBed } from '@angular/core/testing';
import { AdminOverviewComponent } from './admin-overview.component';
import { BookSaleService } from '../../core/services/book-sale.service';
import { ChangeDetectorRef } from '@angular/core';
import { of } from 'rxjs';
import { createBookSale } from '../../testing/book-sale.factory';

describe('AdminOverviewComponent', () => {

  let component: AdminOverviewComponent;

  let bookSaleServiceMock: jasmine.SpyObj<BookSaleService>;
  let cdrMock: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(() => {

    bookSaleServiceMock = jasmine.createSpyObj('BookSaleService', [
      'getAll'
    ]);

    cdrMock = jasmine.createSpyObj('ChangeDetectorRef', [
      'detectChanges'
    ]);

    TestBed.configureTestingModule({
      providers: [
        AdminOverviewComponent,
        { provide: BookSaleService, useValue: bookSaleServiceMock },
        { provide: ChangeDetectorRef, useValue: cdrMock }
      ]
    });

    component = TestBed.inject(AdminOverviewComponent);
  });

  afterEach(() => {
    bookSaleServiceMock.getAll.calls.reset();
    cdrMock.detectChanges.calls.reset();
  });

  // =========================
  // INIT DASHBOARD
  // =========================
  it('should load sales and refresh dashboard', () => {

    bookSaleServiceMock.getAll.and.returnValue(
      of([createBookSale({ year: 2025 })])
    );

    component.initDashboard();

    expect(bookSaleServiceMock.getAll).toHaveBeenCalled();
    expect(component.sales.length).toBe(1);
  });

  // =========================
  // COMPUTE
  // =========================
  it('should compute totals and top book', () => {

    component.sales = [
      createBookSale({
        year: 2025,
        month: 1,
        quantitySold: 10,
        quantityReturn: 2,
        averageDiscount: 0,
        book: {
          id: 1,
          title: 'Book A',
          publisher: 1,
          authors: []
        }
      }),
      createBookSale({
        year: 2025,
        month: 2,
        quantitySold: 5,
        quantityReturn: 1,
        averageDiscount: 0.1,
        book: {
          id: 2,
          title: 'Book B',
          publisher: 2,
          authors: []
        }
      })
    ];

    component.compute();

    expect(component.totalSold).toBe(15);
    expect(component.totalReturns).toBe(3);
    expect(component.totalNet).toBe(12);
    expect(component.topBook).toBeDefined();
  });

  // =========================
  // CHART
  // =========================
  it('should build chart data', () => {

    component.sales = [
      createBookSale({ month: 1 }),
      createBookSale({ month: 2 })
    ];

    component.buildChart();

    expect(component.barChartData.labels!.length).toBe(2);
    expect(component.barChartData.datasets[0].data.length).toBe(2);
  });

  // =========================
  // MONTH LABEL
  // =========================
  it('should return correct month label', () => {

    expect(component.monthLabel(1)).toBe('Jan');
    expect(component.monthLabel(12)).toBe('Déc');
    expect(component.monthLabel(99)).toBe('Inconnu');
  });

  // =========================
  // YEAR CHANGE
  // =========================
  it('should refresh on year change', () => {

    const refreshSpy = jasmine.createSpy('refresh');
    component.refresh = refreshSpy;

    component.onYearChange();

    expect(refreshSpy).toHaveBeenCalled();
  });

});