import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.component.css']
})
export class DoctorDashboardComponent implements OnInit {
  patients: any[] = [];
  searchQuery = '';
  todaysAppointments: any[] = [];
  
  newDoctor = { name: '', specialization: '' };

  selectedApptId: number | null = null;
  consultationData = { id: null, bloodPressure: '', heartRate: '', notes: '' };
  isUpdating = false;
  editMode = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPatients();
    this.loadAppointments();
  }

  registerDoctor() {
    this.api.addDoctor(this.newDoctor).subscribe({
      next: () => {
        this.newDoctor = { name: '', specialization: '' };
        alert('Doctor Registered!');
      },
      error: () => alert('Failed to register doctor')
    });
  }

  loadPatients() {
    this.api.getPatients(this.searchQuery).subscribe(data => this.patients = data);
  }

  deletePatient(id: number) {
    if (confirm('Delete this patient?')) {
      this.api.deletePatient(id).subscribe(() => this.loadPatients());
    }
  }

  loadAppointments() {
    this.api.getTodaysAppointments().subscribe(data => this.todaysAppointments = data);
  }

  openConsultation(apptId: number, mode: string) {
    this.selectedApptId = apptId;
    this.api.getConsultationByAppointment(apptId).subscribe({
      next: (data) => {
        if (data) {
          // Consultation exists
          if (mode === 'create') {
            alert('Consultation already exists for this appointment! Switching to View mode.');
            mode = 'view';
          }
          this.consultationData = data;
          this.isUpdating = true;
          this.editMode = (mode === 'update');
        } else {
          // No consultation exists
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
    const payload = {
      apointmentId: this.selectedApptId, // Matches backend field 'apointmentId'
      bloodPressure: this.consultationData.bloodPressure,
      heartRate: this.consultationData.heartRate,
      notes: this.consultationData.notes
    };

    if (this.isUpdating && this.consultationData.id) {
      this.api.updateConsultation(this.consultationData.id, payload).subscribe({
        next: () => {
          alert('Consultation updated!');
          this.selectedApptId = null;
        },
        error: () => alert('Failed to update consultation.')
      });
    } else {
      this.api.addConsultation(payload).subscribe({
        next: () => {
          alert('Consultation saved!');
          this.selectedApptId = null;
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
