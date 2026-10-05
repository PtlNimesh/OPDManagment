package com.OPD.OPDManagment.service;

import com.OPD.OPDManagment.dto.DoctorRequestDto;
import com.OPD.OPDManagment.dto.DoctorResponseDto;
import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.exception.DoctorNotFoundException;
import com.OPD.OPDManagment.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorResponseDto> getAllDoctors() {
        log.info("Fetching all doctors");
        return doctorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorResponseDto getDoctorById(Long id) {
        log.info("Fetching doctor by id: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        return toDto(doctor);
    }

    public DoctorResponseDto createDoctor(DoctorRequestDto doctorRequestDto) {
        log.info("Creating doctor with name: {}", doctorRequestDto.getName());
        Doctor doctor = toEntity(doctorRequestDto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return toDto(savedDoctor);
    }

    public void deleteDoctor(Long id) {
        log.info("Deleting doctor with id: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);
    }

    private Doctor toEntity(DoctorRequestDto dto) {
        Doctor doctor = new Doctor();
        doctor.setName(dto.getName());
        doctor.setEmail(dto.getEmail());
        doctor.setSpecialization(dto.getSpecialization());
        return doctor;
    }

    public DoctorResponseDto toDto(Doctor doctor) {
        return new DoctorResponseDto(
                doctor.getId(),
                doctor.getName(),
                doctor.getEmail(),
                doctor.getSpecialization()
        );
    }
}
