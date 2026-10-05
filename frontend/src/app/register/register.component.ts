import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  selectedRole: 'PATIENT' | 'DOCTOR' = 'PATIENT';

  // Form Fields
  name: string = '';
  email: string = '';
  gender: string = 'Male';
  age: number | null = 25;
  phoneNumber: string = '';
  specialization: string = 'General Medicine';
  password: string = '';

  errorMessage: string = '';
  successMessage: string = '';
  loading: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  selectRole(role: 'PATIENT' | 'DOCTOR') {
    this.selectedRole = role;
    this.errorMessage = '';
    this.successMessage = '';
  }

  onRegister() {
    if (!this.name.trim()) {
      this.errorMessage = 'Please enter your full name.';
      return;
    }

    if (!this.email.trim() || !this.email.includes('@')) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

    if (this.selectedRole === 'PATIENT' && (!this.phoneNumber || this.phoneNumber.length !== 10)) {
      this.errorMessage = 'Phone number must be exactly 10 digits.';
      return;
    }

    if (!this.password) {
      this.errorMessage = 'Please enter a password.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const payload = {
      name: this.name.trim(),
      email: this.email.trim(),
      role: this.selectedRole,
      gender: this.selectedRole === 'PATIENT' ? this.gender : undefined,
      age: this.selectedRole === 'PATIENT' ? this.age : undefined,
      phoneNumber: this.selectedRole === 'PATIENT' ? this.phoneNumber.trim() : undefined,
      specialization: this.selectedRole === 'DOCTOR' ? this.specialization.trim() : undefined,
      password: this.password
    };

    this.authService.register(payload).subscribe({
      next: (user) => {
        this.loading = false;
        this.successMessage = 'Registration successful! Redirecting to dashboard...';
        setTimeout(() => {
          const targetRoute = this.authService.getDashboardRouteForRole(user.role);
          this.router.navigate([targetRoute]);
        }, 800);
      },
      error: (err: any) => {
        this.loading = false;
        console.error('Registration error:', err);
        if (err.error && err.error.message) {
          if (typeof err.error.message === 'object') {
            this.errorMessage = Object.values(err.error.message).join(', ');
          } else {
            this.errorMessage = err.error.message;
          }
        } else {
          this.errorMessage = 'Registration failed. Please check your inputs.';
        }
      }
    });
  }
}
