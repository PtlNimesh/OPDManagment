package com.OPD.OPDManagment.service;

import org.springframework.stereotype.Service;
import com.OPD.OPDManagment.repository.AppointmentRepository;
import com.OPD.OPDManagment.entity.Appointment;
import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getAppointmentDateTime() != null && appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}
