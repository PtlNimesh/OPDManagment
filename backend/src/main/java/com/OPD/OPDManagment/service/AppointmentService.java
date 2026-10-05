package com.OPD.OPDManagment.service;

import com.OPD.OPDManagment.dto.AppointmentRequestDto;
import com.OPD.OPDManagment.dto.AppointmentResponseDto;
import com.OPD.OPDManagment.entity.Appointment;
import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.exception.AppointmentNotFoundException;
import com.OPD.OPDManagment.exception.BadRequestException;
import com.OPD.OPDManagment.exception.DoctorNotFoundException;
import com.OPD.OPDManagment.exception.PatientNotFountException;
import com.OPD.OPDManagment.repository.AppointmentRepository;
import com.OPD.OPDManagment.repository.DoctorRepository;
import com.OPD.OPDManagment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        log.info("Booking appointment for Patient ID: {}, Doctor ID: {}", dto.getPatientId(), dto.getDoctorId());

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new PatientNotFountException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        if (dto.getAppointmentDateTime() != null && dto.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book appointment for a past date or time.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());
        appointment.setAppointmentDateTime(dto.getAppointmentDateTime());

        Appointment saved = appointmentRepository.save(appointment);
        return toDto(saved, patient, doctor);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getTodaysAppointments() {
        log.info("Fetching today's appointments");
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        return appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(this::enrichAndToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsByPatient(Long patientId) {
        log.info("Fetching appointments for Patient ID: {}", patientId);

        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFountException("Patient not found with id: " + patientId);
        }

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::enrichAndToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDto getAppointmentById(Long id) {
        log.info("Fetching appointment by ID: {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with id: " + id));
        return enrichAndToDto(appointment);
    }

    private AppointmentResponseDto enrichAndToDto(Appointment appointment) {
        Patient patient = patientRepository.findById(appointment.getPatientId()).orElse(null);
        Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
        return toDto(appointment, patient, doctor);
    }

    private AppointmentResponseDto toDto(Appointment appointment, Patient patient, Doctor doctor) {
        return new AppointmentResponseDto(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                patient != null ? patient.getName() : "Unknown Patient",
                doctor != null ? doctor.getName() : "Unknown Doctor",
                doctor != null ? doctor.getSpecialization() : "N/A",
                appointment.getAppointmentDateTime()
        );
    }
}
