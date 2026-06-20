import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthorParticipation } from '../models/author-participation.model';

@Injectable({
  providedIn: 'root'
})
export class AuthorParticipationService {

  private http = inject(HttpClient);
  private api = 'api/author-part';

  /** Collect all the entries */
  getAll(): Observable<AuthorParticipation[]> {
    return this.http.get<AuthorParticipation[]>(this.api, { withCredentials: true });
  }

  /** Retrieve a participation by ID */
  getById(id: number): Observable<AuthorParticipation> {
    return this.http.get<AuthorParticipation>(`${this.api}/${id}`, { withCredentials: true });
  }

  /** Retrieve an author's contributions */
  getByAuthor(authorId: number): Observable<AuthorParticipation[]> {
    return this.http.get<AuthorParticipation[]>(`${this.api}/author/${authorId}`, { withCredentials: true });
  }

  /** Create author participation */
  create(payload: AuthorParticipation) {
    return this.http.post<AuthorParticipation>(this.api, payload, { withCredentials: true });
  }

  /** Update participation */
  update(id: number, payload: AuthorParticipation) {
    return this.http.put<AuthorParticipation>(`${this.api}/${id}`, payload, { withCredentials: true });
  }

  /** Delete participation */
  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`, { withCredentials: true });
  }
}