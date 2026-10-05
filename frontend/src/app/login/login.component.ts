import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email: string = '';
  password: string = '';

  errorMessage: string = '';
  loading: boolean = false;
  returnUrl: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      const route = this.authService.getDashboardRouteForRole();
      this.router.navigate([route]);
    }
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '';
  }

  onLogin() {
    if (!this.email.trim()) {
      this.errorMessage = 'Please enter your email or username.';
      return;
    }

    if (!this.password) {
      this.errorMessage = 'Please enter your password.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login({
      email: this.email.trim(),
      username: this.email.trim(),
      password: this.password,
      role: '' // Auto-detected by backend
    }).subscribe({
      next: (user) => {
        this.loading = false;
        const targetRoute = this.returnUrl || this.authService.getDashboardRouteForRole(user.role);
        this.router.navigate([targetRoute]);
      },
      error: (err) => {
        this.loading = false;
        console.error('Login error:', err);
        if (err.error && err.error.message) {
          this.errorMessage = typeof err.error.message === 'string' ? err.error.message : 'Invalid credentials.';
        } else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
      }
    });
  }
}
