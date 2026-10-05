package com.OPD.OPDManagment.controller;

import com.OPD.OPDManagment.dto.ConsultationRequestDto;
import com.OPD.OPDManagment.dto.ConsultationResponseDto;
import com.OPD.OPDManagment.service.ConsultationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<ConsultationResponseDto> createConsultation(@Valid @RequestBody ConsultationRequestDto consultationRequestDto) {
        ConsultationResponseDto createdConsultation = consultationService.createConsultation(consultationRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConsultation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationResponseDto> updateConsultation(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationRequestDto consultationRequestDto) {
        ConsultationResponseDto updatedConsultation = consultationService.updateConsultation(id, consultationRequestDto);
        return ResponseEntity.ok(updatedConsultation);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ConsultationResponseDto> getConsultationByAppointment(@PathVariable Long appointmentId) {
        return consultationService.getConsultationByAppointmentId(appointmentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}
