import { TestBed } from '@angular/core/testing';
import { DashboardAuthorComponent } from './dashboard-author.component';
import { AuthService } from '../../core/services/auth.service';
import { RoyaltiesService } from '../../core/services/royalties.service';
import { UserService } from '../../core/services/user.service';
import { of } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';

describe('DashboardAuthorComponent', () => {

  let component: DashboardAuthorComponent;

  let royaltiesServiceMock: jasmine.SpyObj<RoyaltiesService>;
  let authServiceMock: any;
  let userServiceMock: any;
  let cdrMock: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(() => {

    royaltiesServiceMock = jasmine.createSpyObj<RoyaltiesService>(
      'RoyaltiesService',
      ['getMyYearlyRoyalties', 'getMonthlyDetails']
    );

    // 🔥 FIX IMPORTANT : toujours retourner un Observable
    royaltiesServiceMock.getMyYearlyRoyalties.and.returnValue(of([]));
    royaltiesServiceMock.getMonthlyDetails.and.returnValue(of([]));

    authServiceMock = {
      currentUser$: of({ id: 1, role: 'AUTHOR' })
    };

    userServiceMock = {};

    cdrMock = jasmine.createSpyObj<ChangeDetectorRef>(
      'ChangeDetectorRef',
      ['detectChanges']
    );

    TestBed.configureTestingModule({
      providers: [
        DashboardAuthorComponent,
        { provide: RoyaltiesService, useValue: royaltiesServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: UserService, useValue: userServiceMock },
        { provide: ChangeDetectorRef, useValue: cdrMock }
      ]
    });

    component = TestBed.inject(DashboardAuthorComponent);
  });

  // =========================
  // TEST 1
  // =========================
  it('should load yearly royalties', () => {

    royaltiesServiceMock.getMyYearlyRoyalties.and.returnValue(
      of([{ title: 'Book A', totalAmount: 100 }])
    );

    component.selectedYear = 2025;

    component.loadYearlyRoyalties();

    expect(royaltiesServiceMock.getMyYearlyRoyalties)
      .toHaveBeenCalledWith(2025);

    expect(component.yearlyTotals.length).toBe(1);
    expect(component.loading).toBeFalse();
  });

  // =========================
  // TEST 2
  // =========================
  it('should load and sort monthly details', () => {

    royaltiesServiceMock.getMonthlyDetails.and.returnValue(
      of([
        { month: 'mars', quantityNet: 10 },
        { month: 'janvier', quantityNet: 5 }
      ])
    );

    component.selectBook('Book A');

    expect(royaltiesServiceMock.getMonthlyDetails)
      .toHaveBeenCalled();

    expect(component.selectedBook).toBe('Book A');

    expect(component.monthlyDetails[0].month)
      .toBe('janvier');
  });

  // =========================
  // TEST 3
  // =========================
  it('should reset and reload on year change', () => {

    component.selectedBook = 'X';
    component.monthlyDetails = [{ month: 'janvier' } as any];

    component.onYearChange();

    expect(component.selectedBook).toBeNull();
    expect(component.monthlyDetails.length).toBe(0);
  });

  // =========================
  // TEST 4
  // =========================
  it('should reset book view', () => {

    component.selectedBook = 'Book A';
    component.monthlyDetails = [{ month: 'janvier' } as any];

    component.goBackToBooks();

    expect(component.selectedBook).toBeNull();
    expect(component.loading).toBeFalse();
  });

});