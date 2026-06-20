import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';


@Injectable({
  providedIn: 'root'
})
export class BookService {

  private http = inject(HttpClient);
  private api = 'api/tools/book';

  getAll(): Observable<Book[]> {
  
    return this.http.get<Book[]>(this.api, { withCredentials: true });
  }

  create(payload: any) {
    return this.http.post(this.api, payload, { withCredentials: true });
  }

  update(id: number, payload: any) {
    return this.http.put(`${this.api}/${id}`, payload, { withCredentials: true });
  }

 
  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`, { withCredentials: true });
  }

  
}