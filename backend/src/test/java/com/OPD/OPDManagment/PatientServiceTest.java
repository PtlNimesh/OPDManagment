package com.OPD.OPDManagment;

import com.OPD.OPDManagment.dto.PatientRequestDto;
import com.OPD.OPDManagment.dto.PatientResponseDto;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.exception.PatientNotFountException;
import com.OPD.OPDManagment.repository.PatientRepository;
import com.OPD.OPDManagment.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePatient() {
        PatientRequestDto requestDto = new PatientRequestDto();
        requestDto.setName("John Doe");
        requestDto.setGender("Male");
        requestDto.setAge(30);
        requestDto.setPhoneNumber("9876543210");

        Patient savedPatient = new Patient();
        savedPatient.setId(1L);
        savedPatient.setName("John Doe");
        savedPatient.setGender("Male");
        savedPatient.setAge(30);
        savedPatient.setPhoneNumber("9876543210");

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientResponseDto responseDto = patientService.createPatient(requestDto);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getId());
        assertEquals("John Doe", responseDto.getName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void testGetPatientById_Success() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Jane Doe");
        patient.setGender("Female");
        patient.setAge(25);
        patient.setPhoneNumber("1234567890");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponseDto responseDto = patientService.getPatientById(1L);

        assertNotNull(responseDto);
        assertEquals("Jane Doe", responseDto.getName());
    }

    @Test
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PatientNotFountException.class, () -> patientService.getPatientById(99L));
    }

    @Test
    void testSearchPatients() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Smith");

        when(patientRepository.findByNameContainingIgnoreCaseOrPhoneNumberContaining("John", "John"))
                .thenReturn(List.of(patient));

        List<PatientResponseDto> result = patientService.searchPatients("John");

        assertEquals(1, result.size());
        assertEquals("John Smith", result.get(0).getName());
    }
}
