package com.OPD.OPDManagment.service;

import com.OPD.OPDManagment.dto.LoginRequestDto;
import com.OPD.OPDManagment.dto.LoginResponseDto;
import com.OPD.OPDManagment.dto.RegisterRequestDto;
import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.exception.BadRequestException;
import com.OPD.OPDManagment.repository.DoctorRepository;
import com.OPD.OPDManagment.repository.PatientRepository;
import com.OPD.OPDManagment.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        String rawIdentifier = request.getEmail() != null ? request.getEmail().trim() : "";
        if (rawIdentifier.isBlank() && request.getUsername() != null) {
            rawIdentifier = request.getUsername().trim();
        }
        final String searchId = rawIdentifier;
        String password = request.getPassword() != null ? request.getPassword() : "";

        log.info("Processing login attempt for identifier: {}", searchId);

        if (searchId.isBlank()) {
            throw new BadRequestException("Email/Username is required");
        }
        if (password.isBlank()) {
            throw new BadRequestException("Password is required");
        }

        // 1. Check Admin
        if ("admin@opd.com".equalsIgnoreCase(searchId) || "admin".equalsIgnoreCase(searchId)) {
            if ("admin123".equals(password) || passwordEncoder.matches(password, passwordEncoder.encode("admin123"))) {
                String token = jwtUtils.generateToken("admin@opd.com", "ADMIN", 1L, "System Administrator");
                return new LoginResponseDto(token, "admin@opd.com", "System Administrator", "ADMIN", 1L, "Admin login successful");
            } else {
                throw new BadRequestException("Invalid email or password");
            }
        }

        // 2. Check Doctor repository
        Optional<Doctor> doctorOpt = doctorRepository.findByEmailIgnoreCase(searchId);
        if (doctorOpt.isEmpty()) {
            doctorOpt = doctorRepository.findAll().stream()
                    .filter(d -> (d.getName() != null && d.getName().equalsIgnoreCase(searchId))
                            || String.valueOf(d.getId()).equals(searchId))
                    .findFirst();
        }

        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            if (verifyPassword(password, doctor.getPassword())) {
                String doctorName = doctor.getName().startsWith("Dr.") ? doctor.getName() : "Dr. " + doctor.getName();
                String emailOrUsername = doctor.getEmail() != null ? doctor.getEmail() : doctor.getName();
                String token = jwtUtils.generateToken(emailOrUsername, "DOCTOR", doctor.getId(), doctorName);
                return new LoginResponseDto(token, emailOrUsername, doctorName, "DOCTOR", doctor.getId(), "Doctor login successful");
            } else {
                throw new BadRequestException("Invalid email or password");
            }
        }

        // 3. Check Patient repository
        Optional<Patient> patientOpt = patientRepository.findByEmailIgnoreCase(searchId);
        if (patientOpt.isEmpty()) {
            patientOpt = patientRepository.findAll().stream()
                    .filter(p -> (p.getPhoneNumber() != null && p.getPhoneNumber().equals(searchId))
                            || (p.getName() != null && p.getName().equalsIgnoreCase(searchId))
                            || String.valueOf(p.getId()).equals(searchId))
                    .findFirst();
        }

        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            if (verifyPassword(password, patient.getPassword())) {
                String emailOrPhone = patient.getEmail() != null ? patient.getEmail() : patient.getPhoneNumber();
                String token = jwtUtils.generateToken(emailOrPhone, "PATIENT", patient.getId(), patient.getName());
                return new LoginResponseDto(token, emailOrPhone, patient.getName(), "PATIENT", patient.getId(), "Patient login successful");
            } else {
                throw new BadRequestException("Invalid email or password");
            }
        }

        throw new BadRequestException("Invalid email or password");
    }

    public LoginResponseDto register(RegisterRequestDto request) {
        log.info("Processing registration request for email: {} with role: {}", request.getEmail(), request.getRole());

        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (email.isBlank()) {
            throw new BadRequestException("Email is required for registration");
        }

        if (doctorRepository.existsByEmail(email) || patientRepository.existsByEmail(email)) {
            throw new BadRequestException("Email '" + email + "' is already registered");
        }

        String role = request.getRole() != null ? request.getRole().toUpperCase().trim() : "PATIENT";
        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new BadRequestException("Password is required for registration");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        if ("PATIENT".equals(role)) {
            Patient patient = new Patient();
            patient.setName(request.getName().trim());
            patient.setEmail(email);
            patient.setGender(request.getGender() != null && !request.getGender().isBlank() ? request.getGender() : "Male");
            patient.setAge(request.getAge() != null ? request.getAge() : 30);
            patient.setPhoneNumber(request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank() ? request.getPhoneNumber() : "9876543210");
            patient.setPassword(encodedPassword);

            Patient saved = patientRepository.save(patient);
            String token = jwtUtils.generateToken(saved.getEmail(), "PATIENT", saved.getId(), saved.getName());

            return new LoginResponseDto(
                    token,
                    saved.getEmail(),
                    saved.getName(),
                    "PATIENT",
                    saved.getId(),
                    "Patient registered successfully!"
            );

        } else if ("DOCTOR".equals(role)) {
            Doctor doctor = new Doctor();
            doctor.setName(request.getName().trim());
            doctor.setEmail(email);
            doctor.setSpecialization(request.getSpecialization() != null && !request.getSpecialization().isBlank() ? request.getSpecialization() : "General Medicine");
            doctor.setPassword(encodedPassword);

            Doctor saved = doctorRepository.save(doctor);
            String doctorName = saved.getName().startsWith("Dr.") ? saved.getName() : "Dr. " + saved.getName();
            String token = jwtUtils.generateToken(saved.getEmail(), "DOCTOR", saved.getId(), doctorName);

            return new LoginResponseDto(
                    token,
                    saved.getEmail(),
                    doctorName,
                    "DOCTOR",
                    saved.getId(),
                    "Doctor registered successfully!"
            );

        } else {
            throw new BadRequestException("Self-registration is available for PATIENT and DOCTOR roles.");
        }
    }

    private boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }
}
