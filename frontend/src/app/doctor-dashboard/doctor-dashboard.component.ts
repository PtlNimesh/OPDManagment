import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { AuthService, UserSession } from '../auth.service';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.component.css']
})
export class DoctorDashboardComponent implements OnInit {
  currentUser: UserSession | null = null;
  activeTab: 'patient-directory' | 'todays-appointments' | 'consultation' = 'todays-appointments';

  patients: any[] = [];
  searchQuery = '';
  todaysAppointments: any[] = [];

  selectedApptId: number | null = null;
  consultationData = { id: null, bloodPressure: '', heartRate: '', notes: '' };
  isUpdating = false;
  editMode = false;

  constructor(private api: ApiService, public authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.loadPatients();
    this.loadAppointments();
  }

  loadPatients() {
    this.api.getPatients(this.searchQuery).subscribe(data => this.patients = data);
  }

  deletePatient(id: number) {
    if (confirm('Delete this patient from clinic records?')) {
      this.api.deletePatient(id).subscribe(() => this.loadPatients());
    }
  }

  loadAppointments() {
    this.api.getTodaysAppointments().subscribe(data => this.todaysAppointments = data);
  }

  startConsultationForAppt(apptId: number, mode: string = 'create') {
    this.selectedApptId = apptId;
    this.activeTab = 'consultation';
    this.openConsultation(apptId, mode);
  }

  openConsultation(apptId: number, mode: string) {
    this.selectedApptId = apptId;
    this.api.getConsultationByAppointment(apptId).subscribe({
      next: (data) => {
        if (data) {
          if (mode === 'create') {
            alert('Consultation already exists for this appointment! Switching to View mode.');
            mode = 'view';
          }
          this.consultationData = data;
          this.isUpdating = true;
          this.editMode = (mode === 'update');
        } else {
          if (mode === 'view' || mode === 'update') {
            alert('No consultation exists yet for this appointment! Switching to Create mode.');
            mode = 'create';
          }
          this.isUpdating = false;
          this.editMode = true;
          this.consultationData = { id: null, bloodPressure: '', heartRate: '', notes: '' };
        }
      },
      error: () => {
        if (mode === 'view' || mode === 'update') {
          alert('No consultation exists yet for this appointment! Switching to Create mode.');
        }
        this.isUpdating = false;
        this.editMode = true;
        this.consultationData = { id: null, bloodPressure: '', heartRate: '', notes: '' };
      }
    });
  }

  deleteConsultationFromTable(apptId: number) {
    this.api.getConsultationByAppointment(apptId).subscribe({
      next: (data) => {
        if (data && data.id) {
          if (confirm('Delete this consultation?')) {
            this.api.deleteConsultation(data.id).subscribe({
              next: () => {
                alert('Consultation deleted!');
                if (this.selectedApptId === apptId) this.selectedApptId = null;
              },
              error: () => alert('Failed to delete consultation.')
            });
          }
        } else {
          alert('No consultation exists to delete.');
        }
      },
      error: () => alert('No consultation exists to delete.')
    });
  }

  saveConsultation() {
    if (!this.selectedApptId) {
      alert('Please select an appointment first.');
      return;
    }

    if (!this.consultationData.bloodPressure || !this.consultationData.heartRate) {
      alert('Please enter Blood Pressure and Heart Rate.');
      return;
    }

    const payload = {
      apointmentId: this.selectedApptId,
      bloodPressure: this.consultationData.bloodPressure,
      heartRate: this.consultationData.heartRate,
      notes: this.consultationData.notes
    };

    if (this.isUpdating && this.consultationData.id) {
      this.api.updateConsultation(this.consultationData.id, payload).subscribe({
        next: () => {
          alert('Consultation updated successfully!');
          this.activeTab = 'todays-appointments';
          this.loadAppointments();
        },
        error: () => alert('Failed to update consultation.')
      });
    } else {
      this.api.addConsultation(payload).subscribe({
        next: () => {
          alert('Consultation saved successfully!');
          this.activeTab = 'todays-appointments';
          this.loadAppointments();
        },
        error: () => alert('Failed to save consultation.')
      });
    }
  }

  deleteConsultation() {
    if (this.consultationData.id && confirm('Delete this consultation?')) {
      this.api.deleteConsultation(this.consultationData.id).subscribe({
        next: () => {
          alert('Consultation deleted!');
          this.selectedApptId = null;
          this.consultationData = { id: null, bloodPressure: '', heartRate: '', notes: '' };
        },
        error: () => alert('Failed to delete consultation.')
      });
    }
  }
}
