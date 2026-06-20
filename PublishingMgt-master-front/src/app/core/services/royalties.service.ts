import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Royalty } from '../models/royalty.model';


@Injectable({
  providedIn: 'root'
})
export class RoyaltiesService {

  private API = 'api/royalties';

  // BehaviorSubject to persist the royalties 
  private royaltiesSubject = new BehaviorSubject<Royalty[]>([]);
  royalties$ = this.royaltiesSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * For AUTHOR 
   * Load royalties and update BehaviorSubject
   */
  getMyRoyalties(): Observable<Royalty[]> {
    return this.http.get<Royalty[]>(
      `${this.API}/mine`,
      { withCredentials: true } // JWT cookies
    ).pipe(
      tap(data => this.royaltiesSubject.next(data)) //update the flow
    );
  }

  /**
   * For MANAGER / ADMIN → royalties for an author
   */
   getRoyaltiesByAuthor(authorId: number): Observable<Royalty[]> {
    return this.http.get<Royalty[]>(
      `${this.API}/by-author/${authorId}`,
      { withCredentials: true }
    ).pipe(
      tap(data => this.royaltiesSubject.next(data))
    );
  }

        /**
         * For MANAGER / ADMIN → Book's royalties 
         */
        getRoyaltiesByBook(bookId: number): Observable<Royalty[]> {
        return this.http.get<Royalty[]>(
          `${this.API}/by-book/${bookId}`,
          { withCredentials: true }
        ).pipe(
          tap(data => this.royaltiesSubject.next(data))
        );
      }

        /**
         * Clear royalties if needed
         */
        clearRoyalties() {
          this.royaltiesSubject.next([]);
        }

        getMyYearlyRoyalties(year: number): Observable<any[]> {
        return this.http.get<any[]>(
          `${this.API}/mine/year/${year}`,
          { withCredentials: true }
        );
      }

      getMonthlyDetails(title: string, year: number): Observable<any[]> {
        return this.http.get<any[]>(
          `${this.API}/mine/book/${encodeURIComponent(title)}/year/${year}`,
          { withCredentials: true }
        );
      }



      getBooksByAuthor(authorId: number) {
        return this.http.get<any[]>(`${this.API}/books/author/${authorId}`,
      { withCredentials: true }
        );
      }


}