import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { PatientDashboardComponent } from './patient-dashboard/patient-dashboard.component';
import { DoctorDashboardComponent } from './doctor-dashboard/doctor-dashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'patient-dashboard', 
    component: PatientDashboardComponent, 
    canActivate: [authGuard], 
    data: { roles: ['PATIENT', 'ADMIN'] } 
  },
  { 
    path: 'doctor-dashboard', 
    component: DoctorDashboardComponent, 
    canActivate: [authGuard], 
    data: { roles: ['DOCTOR', 'ADMIN'] } 
  },
  { 
    path: 'admin-dashboard', 
    component: AdminDashboardComponent, 
    canActivate: [authGuard], 
    data: { roles: ['ADMIN'] } 
  },
  { path: '**', redirectTo: '/login' }
];
