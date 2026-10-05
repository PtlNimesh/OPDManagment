import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'overview' | 'patients' | 'doctors' | 'appointments' = 'overview';

  patients: any[] = [];
  doctors: any[] = [];
  todaysAppointments: any[] = [];
  
  searchQuery: string = '';
  
  newPatient = { name: '', gender: 'Male', age: 30, phoneNumber: '' };
  newDoctor = { name: '', specialization: '' };

  minDateTime: string = '';
  newAppt = { patientId: null, doctorId: null, appointmentDateTime: '' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.setMinDateTime();
    this.loadAllData();
  }

  setMinDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    this.minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  loadAllData() {
    this.loadPatients();
    this.loadDoctors();
    this.loadAppointments();
  }

  loadPatients() {
    this.api.getPatients(this.searchQuery).subscribe(data => this.patients = data);
  }

  loadDoctors() {
    this.api.getDoctors().subscribe(data => this.doctors = data);
  }

  loadAppointments() {
    this.api.getTodaysAppointments().subscribe(data => this.todaysAppointments = data);
  }

  registerPatient() {
    if (!this.newPatient.name || !this.newPatient.phoneNumber) {
      alert('Please provide patient name and phone number.');
      return;
    }
    this.api.addPatient(this.newPatient).subscribe({
      next: () => {
        this.newPatient = { name: '', gender: 'Male', age: 30, phoneNumber: '' };
        this.loadPatients();
        alert('Patient registered successfully!');
      },
      error: () => alert('Failed to register patient.')
    });
  }

  deletePatient(id: number) {
    if (confirm('Are you sure you want to delete this patient record?')) {
      this.api.deletePatient(id).subscribe(() => this.loadPatients());
    }
  }

  registerDoctor() {
    if (!this.newDoctor.name || !this.newDoctor.specialization) {
      alert('Please enter doctor name and specialization.');
      return;
    }
    this.api.addDoctor(this.newDoctor).subscribe({
      next: () => {
        this.newDoctor = { name: '', specialization: '' };
        this.loadDoctors();
        alert('Doctor added to clinic successfully!');
      },
      error: () => alert('Failed to add doctor.')
    });
  }

  bookAppointment() {
    if (!this.newAppt.patientId || !this.newAppt.doctorId || !this.newAppt.appointmentDateTime) {
      alert('Please fill in all appointment fields.');
      return;
    }

    let dt = this.newAppt.appointmentDateTime;
    if (dt && dt.length === 16) dt += ':00';

    this.api.bookAppointment({
      patientId: this.newAppt.patientId,
      doctorId: this.newAppt.doctorId,
      appointmentDateTime: dt
    }).subscribe({
      next: () => {
        this.newAppt = { patientId: null, doctorId: null, appointmentDateTime: '' };
        this.loadAppointments();
        alert('Appointment booked successfully!');
      },
      error: (err: any) => alert('Failed to book appointment: ' + (err.error?.message || 'Check date/time'))
    });
  }
}
