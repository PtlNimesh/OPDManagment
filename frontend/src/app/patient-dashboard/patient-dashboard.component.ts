import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-dashboard.component.html',
  styleUrls: ['./patient-dashboard.component.css']
})
export class PatientDashboardComponent implements OnInit {
  patients: any[] = [];
  doctors: any[] = [];
  selectedPatientId: number | null = null;
  patientAppointments: any[] = [];
  
  newPatient = { name: '', gender: '', age: null, phoneNumber: '' };
  newAppt = { doctorId: null, appointmentDateTime: '' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.api.getPatients().subscribe(data => this.patients = data);
    this.api.getDoctors().subscribe(data => this.doctors = data);
  }

  registerPatient() {
    this.api.addPatient(this.newPatient).subscribe({
      next: (res) => {
        this.newPatient = { name: '', gender: '', age: null, phoneNumber: '' };
        this.loadData();
        alert('Patient Registered!');
      },
      error: () => alert('Failed to register patient.')
    });
  }

  onPatientSelect() {
    if (this.selectedPatientId) {
      this.api.getAppointmentsByPatient(this.selectedPatientId).subscribe(data => {
        this.patientAppointments = data;
      });
    } else {
      this.patientAppointments = [];
    }
  }

  selectedConsultation: any = null;
  consultationError: string = '';

  bookAppointment() {
    if (!this.selectedPatientId) {
      alert("Please select a patient first.");
      return;
    }
    
    let dt = this.newAppt.appointmentDateTime;
    if (dt && dt.length === 16) dt += ':00';

    const apptData = {
      patientId: this.selectedPatientId,
      doctorId: this.newAppt.doctorId,
      appointmentDateTime: dt
    };
    
    this.api.bookAppointment(apptData).subscribe({
      next: () => {
        this.newAppt = { doctorId: null, appointmentDateTime: '' };
        this.onPatientSelect();
        alert('Appointment booked successfully!');
      },
      error: (err) => {
        let msg = 'Failed to book appointment.';
        if (err.error && err.error.errors && err.error.errors.length > 0) {
          msg += ' ' + err.error.errors.map((e: any) => e.defaultMessage).join(', ');
        }
        alert(msg);
      }
    });
  }

  viewConsultation(apptId: number) {
    this.selectedConsultation = null;
    this.consultationError = '';
    
    this.api.getConsultationByAppointment(apptId).subscribe({
      next: (data) => {
        if (data) {
          this.selectedConsultation = data;
        } else {
          this.consultationError = 'No consultation found for this appointment yet.';
        }
      },
      error: () => {
        this.consultationError = 'No consultation found for this appointment yet.';
      }
    });
  }
}
