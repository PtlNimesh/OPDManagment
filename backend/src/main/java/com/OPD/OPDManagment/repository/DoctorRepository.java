
package com.OPD.OPDManagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.OPD.OPDManagment.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
}
