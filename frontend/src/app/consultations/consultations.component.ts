import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-consultations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consultations.component.html',
  styleUrls: ['./consultations.component.css']
})
export class ConsultationsComponent implements OnInit {
  patients: any[] = [];
  selectedPatientId: number | null = null;
  patientAppointments: any[] = [];
  
  // For the form
  selectedApptId: number | null = null;
  newConsultation = { bloodPressure: '', heartRate: '', notes: '' };
  
  // Completed consultations for selected patient
  completedConsultations: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.getPatients().subscribe(data => this.patients = data);
  }

  onPatientChange() {
    if (this.selectedPatientId) {
      this.api.getAppointmentsByPatient(this.selectedPatientId).subscribe(data => {
        this.patientAppointments = data;
        this.loadCompletedConsultations();
      });
    } else {
      this.patientAppointments = [];
      this.completedConsultations = [];
    }
    this.selectedApptId = null;
  }

  loadCompletedConsultations() {
    this.completedConsultations = [];
    for (let appt of this.patientAppointments) {
      this.api.getConsultationByAppointment(appt.id).subscribe({
        next: (consult) => {
          if (consult) {
            this.completedConsultations.push({ ...consult, appointmentDate: appt.appointmentDateTime });
          }
        },
        error: () => {
          // No consultation found for this appointment (404)
        }
      });
    }
  }

  saveConsultation() {
    if (!this.selectedApptId) return;

    const data = {
      apointmentId: this.selectedApptId, // Notice spelling match with backend
      bloodPressure: this.newConsultation.bloodPressure,
      heartRate: this.newConsultation.heartRate,
      notes: this.newConsultation.notes
    };

    this.api.addConsultation(data).subscribe(() => {
      this.newConsultation = { bloodPressure: '', heartRate: '', notes: '' };
      this.selectedApptId = null;
      this.loadCompletedConsultations();
      alert('Consultation marked as complete!');
    });
  }
}
