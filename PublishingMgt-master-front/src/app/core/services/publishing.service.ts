import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publishing } from '../models/publishing.model';

@Injectable({
  providedIn: 'root'
})
export class PublishingService {

  private http = inject(HttpClient);
  private api = 'api/publish';

  // GET all publishings
  getAll(): Observable<Publishing[]> {
    return this.http.get<Publishing[]>(`${this.api}/all`, { withCredentials: true });
  }

  // GET one publishing by ID
  getById(id: number): Observable<Publishing> {
    return this.http.get<Publishing>(`${this.api}/${id}`, { withCredentials: true });
  }

  // CREATE publishing
  create(payload: any): Observable<any> {
    
    if (!payload.book || !payload.book.book_id) {
      throw new Error("Book ID is required to create a publishing.");
    }
    payload.book = { book_id: payload.book.book_id };
    return this.http.post(this.api, payload, { withCredentials: true });
  }

  // UPDATE publishing
  update(id: number, payload: any): Observable<any> {
  if (!payload.book || !payload.book.book_id) {
    throw new Error("Book ID is required to update a publishing.");
  }
   return this.http.put(`${this.api}/${id}`, payload, { withCredentials: true });
}
  // DELETE publishing
  delete(id: number): Observable<any> {
    return this.http.delete(`${this.api}/${id}`, { withCredentials: true });
  }
}