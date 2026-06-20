import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, switchMap, tap, catchError } from 'rxjs';
import { User } from '../models/user.model';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
 
  private API = '/api/auth';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

   login(data: any): Observable<User> {
    return this.http.post<User>(`${this.API}/login`, data, { withCredentials: true })
      .pipe(tap(user => this.currentUserSubject.next(user)));
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.API}/logout`, {}, { withCredentials: true })
      .pipe(tap(() => this.currentUserSubject.next(null)));
  }

  refresh(): Observable<User | null> {
    return this.http.post(`${this.API}/refresh`, {}, { withCredentials: true })
      .pipe(switchMap(() => this.me()));
  }

   /**
   * Retrieves the connected user via JWT cookie 
   * To use in provideAppInitializer
   */
  me(): Observable<User | null> {
    return this.http.get<User>(`${this.API}/me`, { withCredentials: true }).pipe(
    tap(user => {
      this.currentUserSubject.next(user);
    }),
    catchError(err => {
      console.warn('[AuthService] me() no session', err);
      this.currentUserSubject.next(null);
      return of(null);
    })
  );
}

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

//   // -----------------------------
//   // GET ALL AUTHORS
//   // -----------------------------

getAuthors() {
  return this.http.get<any[]>(`${this.API}/authors`,
 { withCredentials: true }
  );
}

}