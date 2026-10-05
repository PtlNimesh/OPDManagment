import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, tap } from 'rxjs';
import { ApiService } from './api.service';
import { Router } from '@angular/router';

export interface UserSession {
  token: string;
  username: string;
  name: string;
  role: 'PATIENT' | 'DOCTOR' | 'ADMIN';
  entityId?: number;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<UserSession | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apiService: ApiService, private router: Router) {
    const savedUser = localStorage.getItem('opd_user');
    if (savedUser) {
      try {
        this.currentUserSubject.next(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('opd_user');
      }
    }
  }

  public get currentUserValue(): UserSession | null {
    return this.currentUserSubject.value;
  }

  login(credentials: { email?: string; username?: string; password?: string; role?: string }): Observable<UserSession> {
    return this.apiService.login(credentials).pipe(
      tap((user: UserSession) => {
        localStorage.setItem('opd_user', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  register(userData: any): Observable<UserSession> {
    return this.apiService.register(userData).pipe(
      tap((user: UserSession) => {
        localStorage.setItem('opd_user', JSON.stringify(user));
        this.currentUserSubject.next(user);
      })
    );
  }

  logout() {
    localStorage.removeItem('opd_user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }

  getRole(): string {
    return this.currentUserSubject.value ? this.currentUserSubject.value.role : '';
  }

  hasRole(allowedRoles: string[]): boolean {
    const userRole = this.getRole();
    return allowedRoles.includes(userRole);
  }

  getDashboardRouteForRole(role?: string): string {
    const r = role || this.getRole();
    switch (r) {
      case 'DOCTOR':
        return '/doctor-dashboard';
      case 'PATIENT':
        return '/patient-dashboard';
      case 'ADMIN':
        return '/admin-dashboard';
      default:
        return '/login';
    }
  }
}
