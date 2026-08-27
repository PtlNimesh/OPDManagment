package com.OPD.OPDManagment.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.OPD.OPDManagment.entity.Patient;

import com.OPD.OPDManagment.repository.PatientRepository;

import com.OPD.OPDManagment.exception.PatientNotFountException;



@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFountException("Patient not found with id: " + id));
    }

    public Patient deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientRepository.deleteById(id);
        return patient;
    }

   
}
