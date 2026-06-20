import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookSale } from '../models/book-sale.model';

@Injectable({ providedIn: 'root' })
export class BookSaleService {

  private http = inject(HttpClient);
  private api = '/api/book-sales';

  getAll(): Observable<BookSale[]> {
    return this.http.get<BookSale[]>(
      `${this.api}/all`,
      { withCredentials: true }
    );
  }

  create(data: any): Observable<BookSale> {
    return this.http.post<BookSale>(
      `${this.api}/add`,
      data,
     { withCredentials: true }
    );
  }

  update(id: number, data: any): Observable<BookSale> {
    return this.http.put<BookSale>(
      `${this.api}/update/${id}`,
      data,
      { withCredentials: true }
    );
  }

  delete(id: number) {
    return this.http.delete(
      `${this.api}/delete/${id}`,
      { withCredentials: true }
    );
  }

   getByBook(bookId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/book/${bookId}`, {
      withCredentials: true
    });
  }
}