package com.OPD.OPDManagment.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.repository.PatientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.OPD.OPDManagment.exception.PatientNotFountException;

import java.util.List;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }


    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        return patientRepository.save(patient);
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFountException("Patient not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam(required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(patientRepository.findAll());
        }
        List<Patient> patients = patientRepository.findByNameContainingIgnoreCaseOrPhoneNumberContaining(query, query);
        return ResponseEntity.ok(patients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
