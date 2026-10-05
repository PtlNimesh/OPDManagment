package com.OPD.OPDManagment.repository;

import com.OPD.OPDManagment.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByNameContainingIgnoreCaseOrPhoneNumberContaining(String name, String phoneNumber);
    Optional<Patient> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
}
