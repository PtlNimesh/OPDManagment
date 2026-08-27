package com.OPD.OPDManagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.OPD.OPDManagment.entity.Consultation;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByApointmentIdIn(List<Long> apointmentIds);
    Optional<Consultation> findByApointmentId(Long apointmentId);
}
