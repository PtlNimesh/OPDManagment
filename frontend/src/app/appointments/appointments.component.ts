import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointments.component.html',
  styleUrls: ['./appointments.component.css']
})
export class AppointmentsComponent implements OnInit {
  patients: any[] = [];
  doctors: any[] = [];
  todaysAppointments: any[] = [];
  
  minDateTime: string = '';
  newAppt = { patientId: null, doctorId: null, appointmentDateTime: '' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.setMinDateTime();
    this.loadData();
    this.loadTodaysAppointments();
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
    this.api.getPatients().subscribe(data => this.patients = data);
    this.api.getDoctors().subscribe(data => this.doctors = data);
  }

  loadTodaysAppointments() {
    this.api.getTodaysAppointments().subscribe(data => this.todaysAppointments = data);
  }

  bookAppointment() {
    if (!this.newAppt.appointmentDateTime) {
      alert('Please select appointment date and time.');
      return;
    }

    const selectedDate = new Date(this.newAppt.appointmentDateTime);
    const now = new Date();
    if (selectedDate < now) {
      alert('Cannot book appointment for a past date or time.');
      return;
    }

    let dt = this.newAppt.appointmentDateTime;
    if (dt && dt.length === 16) {
      dt += ':00'; // Append seconds for Spring Boot LocalDateTime parsing
    }

    const apptData = {
      patientId: this.newAppt.patientId,
      doctorId: this.newAppt.doctorId,
      appointmentDateTime: dt
    };
    
    this.api.bookAppointment(apptData).subscribe({
      next: () => {
        this.newAppt = { patientId: null, doctorId: null, appointmentDateTime: '' };
        this.loadTodaysAppointments();
        alert('Appointment booked successfully!');
      },
      error: (err) => {
        console.error('Error booking appointment:', err);
        let msg = 'Failed to book appointment.';
        if (err.error && err.error.errors && err.error.errors.length > 0) {
          msg += ' ' + err.error.errors.map((e: any) => e.defaultMessage).join(', ');
        } else if (err.error && err.error.message) {
          msg += ' ' + err.error.message;
        }
        alert(msg);
      }
    });
  }
}
