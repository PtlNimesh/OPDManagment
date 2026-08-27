import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patients.component.html',
  styleUrls: ['./patients.component.css']
})
export class PatientsComponent implements OnInit {
  patients: any[] = [];
  searchQuery: string = '';
  newPatient = { name: '', gender: '', age: null, phoneNumber: '' };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients() {
    this.api.getPatients(this.searchQuery).subscribe(data => {
      this.patients = data;
    });
  }

  search() {
    this.loadPatients();
  }

  registerPatient() {
    this.api.addPatient(this.newPatient).subscribe({
      next: () => {
        this.newPatient = { name: '', gender: '', age: null, phoneNumber: '' };
        this.loadPatients();
      },
      error: (err) => {
        console.error('Error creating patient:', err);
        alert('Failed to register patient. Please check the input fields.');
      }
    });
  }

  deletePatient(id: number) {
    if (confirm('Are you sure you want to delete this patient?')) {
      this.api.deletePatient(id).subscribe(() => {
        this.loadPatients();
      });
    }
  }
}
