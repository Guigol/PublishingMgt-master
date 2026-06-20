import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';

import { RoyaltiesService } from './royalties.service';

describe('RoyaltiesService', () => {

  let service: RoyaltiesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(RoyaltiesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });


  // TEST 1

  it('should load royalties and update BehaviorSubject', () => {

  const royaltiesMock = [
    {
      title: 'Spring Book',
      amount: 18,
      month: 'janvier',
      year: '2025'
    }
  ];

        service.getMyRoyalties().subscribe();

        const req = httpMock.expectOne(
            'api/royalties/mine'
        );

        expect(req.request.method).toBe('GET');

        req.flush(royaltiesMock);

        service.royalties$.subscribe(data => {
            expect(data.length).toBe(1);
            expect(data[0].title).toBe('Spring Book');
        });

    });

      // TEST 2
            it('should clear royalties', () => {

            service.clearRoyalties();

            service.royalties$.subscribe(data => {
                    expect(data.length).toBe(0);
                });

            });

    // TEST 3
    it('should call royalties by author endpoint', () => {

            service.getRoyaltiesByAuthor(5).subscribe();

            const req = httpMock.expectOne(
                'api/royalties/by-author/5'
            );

            expect(req.request.method).toBe('GET');

            req.flush([]);

            });

    // TEST 4
    it('should call royalties by book endpoint', () => {

    service.getRoyaltiesByBook(12).subscribe();

    const req = httpMock.expectOne(
        'api/royalties/by-book/12'
    );

    expect(req.request.method).toBe('GET');

    req.flush([]);

    });

    // TEST 5
    it('should call yearly royalties endpoint', () => {

    service.getMyYearlyRoyalties(2025).subscribe();

    const req = httpMock.expectOne(
        'api/royalties/mine/year/2025'
    );

    expect(req.request.method).toBe('GET');

    req.flush([]);

    });

    // TEST 6
    it('should encode title when requesting monthly details', () => {

    service.getMonthlyDetails(
        'Spring Boot Advanced',
        2025
    ).subscribe();

    const req = httpMock.expectOne(
        'api/royalties/mine/book/Spring%20Boot%20Advanced/year/2025'
    );

    expect(req.request.method).toBe('GET');

    req.flush([]);

    });

    // TEST 7
    it('should retrieve books by author', () => {

    service.getBooksByAuthor(7).subscribe();

    const req = httpMock.expectOne(
        'api/royalties/books/author/7'
    );

    expect(req.request.method).toBe('GET');

    req.flush([]);

    });

});


