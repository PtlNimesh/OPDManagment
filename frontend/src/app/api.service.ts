import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:7777/api';

  constructor(private http: HttpClient) { }

  // Auth
  login(credentials: { email?: string; username?: string; password?: string; role?: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credentials);
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, userData);
  }

  getRoles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/auth/roles`);
  }

  // Patients
  getPatients(query?: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/patients/search${query ? '?query=' + query : ''}`);
  }
  
  addPatient(patient: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/patients`, patient);
  }

  deletePatient(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/patients/${id}`);
  }

  // Doctors
  getDoctors(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/doctors`);
  }

  addDoctor(doctor: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/doctors`, doctor);
  }

  // Appointments
  getTodaysAppointments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/appointments/today`);
  }

  bookAppointment(appointment: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/appointments`, appointment);
  }

  getAppointmentsByPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/appointments/patient/${patientId}`);
  }

  // Consultations
  addConsultation(consultation: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/consultations`, consultation);
  }

  updateConsultation(id: number, consultation: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/consultations/${id}`, consultation);
  }

  deleteConsultation(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/consultations/${id}`);
  }

  getConsultationByAppointment(appointmentId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/consultations/appointment/${appointmentId}`);
  }
}
