package com.OPD.OPDManagment.repository;

import com.OPD.OPDManagment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
}
