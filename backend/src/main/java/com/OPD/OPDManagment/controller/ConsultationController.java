package com.OPD.OPDManagment.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.util.Optional;

import com.OPD.OPDManagment.entity.Consultation;
import com.OPD.OPDManagment.service.ConsultationService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public Consultation createConsultation(@Valid @RequestBody Consultation consultation) {
        return consultationService.createConsultation(consultation);
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public Consultation updateConsultation(@PathVariable Long id, @Valid @RequestBody Consultation consultationDetails) {
        Consultation consultation = consultationService.getConsultationById(id);
        consultation.setBloodPressure(consultationDetails.getBloodPressure());
        consultation.setHeartRate(consultationDetails.getHeartRate());
        consultation.setNotes(consultationDetails.getNotes());
        return consultationService.createConsultation(consultation); // Using save
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Consultation> getConsultationByAppointment(@PathVariable Long appointmentId) {
        Optional<Consultation> consultation = consultationService.getConsultationByAppointmentId(appointmentId);
        return consultation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}
