import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Publisher } from '../models/publisher.model';

@Injectable({
  providedIn: 'root'
})
export class PublisherService {

  private http = inject(HttpClient);
  private api = 'api/publisher';

  getAll(): Observable<Publisher[]> {
    return this.http.get<Publisher[]>(`${this.api}/all`, { withCredentials: true });
  }

  getById(id: number): Observable<Publisher> {
    return this.http.get<Publisher>(`${this.api}/${id}`, { withCredentials: true });
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