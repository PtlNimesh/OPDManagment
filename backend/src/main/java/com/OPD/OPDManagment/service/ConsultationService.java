package com.OPD.OPDManagment.service;

import org.springframework.stereotype.Service;

import com.OPD.OPDManagment.entity.Consultation;
import com.OPD.OPDManagment.repository.ConsultationRepository;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationService(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }
    
    public ConsultationRepository getConsultationRepository() {
        return consultationRepository;
    }

    public Consultation createConsultation(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    public Consultation getConsultationById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation not found with id: " + id));
    }

    public Consultation deleteConsultation(Long id) {
        Consultation consultation = getConsultationById(id);
        consultationRepository.delete(consultation);
        return consultation;
    }

    public java.util.Optional<Consultation> getConsultationByAppointmentId(Long appointmentId) {
        return consultationRepository.findByApointmentId(appointmentId);
    }
}
