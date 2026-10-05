import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthService, UserSession } from '../auth.service';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-dashboard.component.html',
  styleUrls: ['./patient-dashboard.component.css']
})
export class PatientDashboardComponent implements OnInit {
  currentUser: UserSession | null = null;
  activeTab: 'book' | 'my-appointments' = 'book';

  patients: any[] = [];
  doctors: any[] = [];
  selectedPatientId: number | null = null;
  patientAppointments: any[] = [];
  
  minDateTime: string = '';
  newAppt = { doctorId: null, appointmentDateTime: '' };

  selectedConsultation: any = null;
  selectedApptForConsultation: number | null = null;
  consultationError: string = '';
  loadingConsultation: boolean = false;

  constructor(private api: ApiService, public authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.setMinDateTime();
    this.loadData();
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

  loadData() {
    this.api.getPatients().subscribe(data => {
      this.patients = data;
      if (this.currentUser && this.currentUser.entityId) {
        const found = this.patients.find(p => p.id === this.currentUser?.entityId);
        if (found) {
          this.selectedPatientId = found.id;
        } else if (this.patients.length > 0) {
          this.selectedPatientId = this.patients[0].id;
        }
      } else if (this.patients.length > 0) {
        this.selectedPatientId = this.patients[0].id;
      }
      this.loadMyAppointments();
    });

    this.api.getDoctors().subscribe({
      next: data => this.doctors = data || [],
      error: err => console.error('Error fetching doctors list:', err)
    });
  }

  loadMyAppointments() {
    if (this.selectedPatientId) {
      this.api.getAppointmentsByPatient(this.selectedPatientId).subscribe(data => {
        this.patientAppointments = data;
      });
    } else {
      this.patientAppointments = [];
    }
  }

  bookAppointment() {
    if (!this.selectedPatientId) {
      alert("No patient session identified. Please re-login.");
      return;
    }

    if (!this.newAppt.doctorId) {
      alert("Please select a doctor.");
      return;
    }

    if (!this.newAppt.appointmentDateTime) {
      alert("Please select appointment date and time.");
      return;
    }

    const selectedDate = new Date(this.newAppt.appointmentDateTime);
    const now = new Date();
    if (selectedDate < now) {
      alert("Cannot book appointment for a past date or time.");
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
        this.loadMyAppointments();
        this.activeTab = 'my-appointments';
        alert('Appointment booked successfully!');
      },
      error: (err) => {
        let msg = 'Failed to book appointment.';
        if (err.error && err.error.message) {
          msg += ' ' + err.error.message;
        }
        alert(msg);
      }
    });
  }

  viewConsultation(apptId: number) {
    this.selectedApptForConsultation = apptId;
    this.selectedConsultation = null;
    this.consultationError = '';
    this.loadingConsultation = true;
    
    this.api.getConsultationByAppointment(apptId).subscribe({
      next: (data) => {
        this.loadingConsultation = false;
        if (data) {
          this.selectedConsultation = data;
        } else {
          this.consultationError = 'No consultation record has been added by doctor yet.';
        }
      },
      error: () => {
        this.loadingConsultation = false;
        this.consultationError = 'No consultation record has been added by doctor yet.';
      }
    });
  }
}
