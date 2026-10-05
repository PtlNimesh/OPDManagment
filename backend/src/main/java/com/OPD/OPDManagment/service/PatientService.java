package com.OPD.OPDManagment.service;

import com.OPD.OPDManagment.dto.PatientRequestDto;
import com.OPD.OPDManagment.dto.PatientResponseDto;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.exception.PatientNotFountException;
import com.OPD.OPDManagment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto) {
        log.info("Creating new patient with name: {}", patientRequestDto.getName());
        Patient patient = toEntity(patientRequestDto);
        Patient savedPatient = patientRepository.save(patient);
        return toDto(savedPatient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDto> getAllPatients() {
        log.info("Fetching all patients");
        return patientRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long id) {
        log.info("Fetching patient by id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFountException("Patient not found with id: " + id));
        return toDto(patient);
    }

    public void deletePatient(Long id) {
        log.info("Deleting patient with id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFountException("Patient not found with id: " + id));
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDto> searchPatients(String query) {
        log.info("Searching patients with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.findByNameContainingIgnoreCaseOrPhoneNumberContaining(query.trim(), query.trim())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Patient toEntity(PatientRequestDto dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setGender(dto.getGender());
        patient.setAge(dto.getAge());
        patient.setPhoneNumber(dto.getPhoneNumber());
        return patient;
    }

    public PatientResponseDto toDto(Patient patient) {
        return new PatientResponseDto(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getGender(),
                patient.getAge(),
                patient.getPhoneNumber()
        );
    }
}
