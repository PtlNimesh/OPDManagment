import { Routes } from '@angular/router';
import { PatientDashboardComponent } from './patient-dashboard/patient-dashboard.component';
import { DoctorDashboardComponent } from './doctor-dashboard/doctor-dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: '/patient-dashboard', pathMatch: 'full' },
  { path: 'patient-dashboard', component: PatientDashboardComponent },
  { path: 'doctor-dashboard', component: DoctorDashboardComponent }
];
