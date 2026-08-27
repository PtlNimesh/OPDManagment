package com.OPD.OPDManagment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.OPD.OPDManagment.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByNameContainingIgnoreCaseOrPhoneNumberContaining(String name, String phoneNumber);
}
