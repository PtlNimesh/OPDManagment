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
  
  newAppt = { patientId: null, doctorId: null, appointmentDateTime: '' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
    this.loadTodaysAppointments();
  }

  loadData() {
    this.api.getPatients().subscribe(data => this.patients = data);
    this.api.getDoctors().subscribe(data => this.doctors = data);
  }

  loadTodaysAppointments() {
    this.api.getTodaysAppointments().subscribe(data => this.todaysAppointments = data);
  }

  bookAppointment() {
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
        alert('Failed to book appointment. Ensure all fields are selected correctly.');
      }
    });
  }
}
