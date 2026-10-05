package com.OPD.OPDManagment;

import com.OPD.OPDManagment.dto.AppointmentRequestDto;
import com.OPD.OPDManagment.dto.AppointmentResponseDto;
import com.OPD.OPDManagment.entity.Appointment;
import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.exception.BadRequestException;
import com.OPD.OPDManagment.exception.PatientNotFountException;
import com.OPD.OPDManagment.repository.AppointmentRepository;
import com.OPD.OPDManagment.repository.DoctorRepository;
import com.OPD.OPDManagment.repository.PatientRepository;
import com.OPD.OPDManagment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAppointment_Success() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice");

        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setName("Dr. Bob");
        doctor.setSpecialization("Cardiology");

        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);

        AppointmentRequestDto requestDto = new AppointmentRequestDto(1L, 2L, futureTime);

        Appointment savedAppointment = new Appointment(10L, 1L, 2L, futureTime);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponseDto responseDto = appointmentService.createAppointment(requestDto);

        assertNotNull(responseDto);
        assertEquals(10L, responseDto.getId());
        assertEquals("Alice", responseDto.getPatientName());
        assertEquals("Dr. Bob", responseDto.getDoctorName());
    }

    @Test
    void testCreateAppointment_PatientNotFound() {
        AppointmentRequestDto requestDto = new AppointmentRequestDto(99L, 2L, LocalDateTime.now().plusDays(1));
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFountException.class, () -> appointmentService.createAppointment(requestDto));
    }

    @Test
    void testCreateAppointment_PastDate() {
        Patient patient = new Patient();
        patient.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(2L);

        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        AppointmentRequestDto requestDto = new AppointmentRequestDto(1L, 2L, pastTime);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));

        assertThrows(BadRequestException.class, () -> appointmentService.createAppointment(requestDto));
    }
}
