package com.OPD.OPDManagment.service;

import com.OPD.OPDManagment.dto.ConsultationRequestDto;
import com.OPD.OPDManagment.dto.ConsultationResponseDto;
import com.OPD.OPDManagment.entity.Consultation;
import com.OPD.OPDManagment.exception.AppointmentNotFoundException;
import com.OPD.OPDManagment.exception.ConsultationNotFoundException;
import com.OPD.OPDManagment.repository.AppointmentRepository;
import com.OPD.OPDManagment.repository.ConsultationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ConsultationService {

    private static final Logger log = LoggerFactory.getLogger(ConsultationService.class);

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;

    public ConsultationService(ConsultationRepository consultationRepository, AppointmentRepository appointmentRepository) {
        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ConsultationResponseDto createConsultation(ConsultationRequestDto dto) {
        log.info("Creating consultation for Appointment ID: {}", dto.getApointmentId());

        if (!appointmentRepository.existsById(dto.getApointmentId())) {
            throw new AppointmentNotFoundException("Appointment not found with id: " + dto.getApointmentId());
        }

        Consultation consultation = new Consultation();
        consultation.setApointmentId(dto.getApointmentId());
        consultation.setBloodPressure(dto.getBloodPressure());
        consultation.setHeartRate(dto.getHeartRate());
        consultation.setNotes(dto.getNotes());

        Consultation saved = consultationRepository.save(consultation);
        return toDto(saved);
    }

    public ConsultationResponseDto updateConsultation(Long id, ConsultationRequestDto dto) {
        log.info("Updating consultation with ID: {}", id);

        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationNotFoundException("Consultation not found with id: " + id));

        consultation.setBloodPressure(dto.getBloodPressure());
        consultation.setHeartRate(dto.getHeartRate());
        consultation.setNotes(dto.getNotes());

        Consultation updated = consultationRepository.save(consultation);
        return toDto(updated);
    }

    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationById(Long id) {
        log.info("Fetching consultation by ID: {}", id);
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationNotFoundException("Consultation not found with id: " + id));
        return toDto(consultation);
    }

    @Transactional(readOnly = true)
    public Optional<ConsultationResponseDto> getConsultationByAppointmentId(Long appointmentId) {
        log.info("Fetching consultation for Appointment ID: {}", appointmentId);
        return consultationRepository.findByApointmentId(appointmentId)
                .map(this::toDto);
    }

    public void deleteConsultation(Long id) {
        log.info("Deleting consultation with ID: {}", id);
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ConsultationNotFoundException("Consultation not found with id: " + id));
        consultationRepository.delete(consultation);
    }

    public ConsultationResponseDto toDto(Consultation consultation) {
        return new ConsultationResponseDto(
                consultation.getId(),
                consultation.getApointmentId(),
                consultation.getBloodPressure(),
                consultation.getHeartRate(),
                consultation.getNotes()
        );
    }
}
