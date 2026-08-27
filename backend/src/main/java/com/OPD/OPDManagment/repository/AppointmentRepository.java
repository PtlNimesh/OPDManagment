
package com.OPD.OPDManagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.OPD.OPDManagment.entity.Appointment;
import java.util.List;
import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientId(Long patientId);
}
