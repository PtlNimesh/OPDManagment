package com.OPD.OPDManagment.security;

import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.repository.DoctorRepository;
import com.OPD.OPDManagment.repository.PatientRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(PatientRepository patientRepository,
                                    DoctorRepository doctorRepository,
                                    @Lazy PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null || identifier.isBlank()) {
            throw new UsernameNotFoundException("Identifier cannot be empty");
        }

        String trimmed = identifier.trim();

        if ("admin".equalsIgnoreCase(trimmed) || "admin@opd.com".equalsIgnoreCase(trimmed)) {
            return new User(
                    "admin@opd.com",
                    passwordEncoder.encode("admin123"),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 1. Check Doctor Repository by Email
        var doctorByEmail = doctorRepository.findByEmailIgnoreCase(trimmed);
        if (doctorByEmail.isPresent()) {
            Doctor d = doctorByEmail.get();
            String pwd = (d.getPassword() != null && !d.getPassword().isBlank()) 
                    ? d.getPassword() 
                    : passwordEncoder.encode("doctor123");
            return new User(
                    d.getEmail() != null ? d.getEmail() : d.getName(),
                    pwd,
                    List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        // 2. Check Patient Repository by Email
        var patientByEmail = patientRepository.findByEmailIgnoreCase(trimmed);
        if (patientByEmail.isPresent()) {
            Patient p = patientByEmail.get();
            String pwd = (p.getPassword() != null && !p.getPassword().isBlank()) 
                    ? p.getPassword() 
                    : passwordEncoder.encode("patient123");
            return new User(
                    p.getEmail() != null ? p.getEmail() : (p.getPhoneNumber() != null ? p.getPhoneNumber() : p.getName()),
                    pwd,
                    List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
            );
        }

        // 3. Fallback search doctors by name or ID
        List<Doctor> doctors = doctorRepository.findAll();
        for (Doctor d : doctors) {
            if (d.getName().equalsIgnoreCase(trimmed) || String.valueOf(d.getId()).equals(trimmed)) {
                String pwd = (d.getPassword() != null && !d.getPassword().isBlank()) 
                        ? d.getPassword() 
                        : passwordEncoder.encode("doctor123");
                return new User(
                        d.getEmail() != null ? d.getEmail() : d.getName(),
                        pwd,
                        List.of(new SimpleGrantedAuthority("ROLE_DOCTOR"))
                );
            }
        }

        // 4. Fallback search patients by phone, name, or ID
        List<Patient> patients = patientRepository.findAll();
        for (Patient p : patients) {
            if ((p.getPhoneNumber() != null && p.getPhoneNumber().equals(trimmed)) || 
                p.getName().equalsIgnoreCase(trimmed) || 
                String.valueOf(p.getId()).equals(trimmed)) {
                String pwd = (p.getPassword() != null && !p.getPassword().isBlank()) 
                        ? p.getPassword() 
                        : passwordEncoder.encode("patient123");
                return new User(
                        p.getEmail() != null ? p.getEmail() : (p.getPhoneNumber() != null ? p.getPhoneNumber() : p.getName()),
                        pwd,
                        List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
                );
            }
        }

        throw new UsernameNotFoundException("User not found with identifier: " + identifier);
    }
}
