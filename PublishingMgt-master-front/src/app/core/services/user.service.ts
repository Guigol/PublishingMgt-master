// src/app/core/services/auth.service.ts
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, switchMap, tap, catchError } from 'rxjs';
import { PubUser } from '../models/pubuser.model';
import { Injectable, inject } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private API = "api";

  // -----------------------------
  // CRUD PubUser (Admin)
  // -----------------------------
  getAllUsers(): Observable<PubUser[]> {
    return this.http.get<PubUser[]>(`${this.API}/tools/pubuser`, { withCredentials: true });
  }

  getUserById(id: number): Observable<PubUser> {
    return this.http.get<PubUser>(`${this.API}/tools/pubuser/${id}`, { withCredentials: true });
  }

  createUser(user: any): Observable<any> {
    return this.http.post(`${this.API}/tools/pubuser`, user, { withCredentials: true });
  }

  updateUser(id: number, user: any): Observable<any> {
    return this.http.put(`${this.API}/tools/pubuser/${id}`, user, { withCredentials: true });
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.API}/tools/pubuser/${id}`, { withCredentials: true });
  }

  // Get All authors (Manager/Admin)
  getAllAuthors(): Observable<PubUser[]> {
    return this.http.get<PubUser[]>(`${this.API}/auth/authors`, { withCredentials: true });
  }
  
}