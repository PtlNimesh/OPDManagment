package com.OPD.OPDManagment.config;

import com.OPD.OPDManagment.entity.Doctor;
import com.OPD.OPDManagment.entity.Patient;
import com.OPD.OPDManagment.repository.DoctorRepository;
import com.OPD.OPDManagment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking initial seed data...");

        if (doctorRepository.count() == 0) {
            log.info("Seeding default doctor accounts...");
            Doctor doc1 = new Doctor();
            doc1.setName("Dr. Sarah Jenkins");
            doc1.setEmail("doctor@opd.com");
            doc1.setSpecialization("Cardiology");
            doc1.setPassword(passwordEncoder.encode("doctor123"));
            doctorRepository.save(doc1);

            Doctor doc2 = new Doctor();
            doc2.setName("Dr. Alex Morgan");
            doc2.setEmail("alex.morgan@opd.com");
            doc2.setSpecialization("General Medicine");
            doc2.setPassword(passwordEncoder.encode("doctor123"));
            doctorRepository.save(doc2);
        } else {
            // Ensure existing doctors have emails and encoded passwords if missing
            for (Doctor doc : doctorRepository.findAll()) {
                boolean updated = false;
                if (doc.getEmail() == null || doc.getEmail().isBlank()) {
                    doc.setEmail(doc.getName().toLowerCase().replaceAll("[^a-z0-9]", "") + "@opd.com");
                    updated = true;
                }
                if (doc.getPassword() == null || doc.getPassword().isBlank() || !doc.getPassword().startsWith("$2a$")) {
                    doc.setPassword(passwordEncoder.encode(doc.getPassword() != null && !doc.getPassword().isBlank() ? doc.getPassword() : "doctor123"));
                    updated = true;
                }
                if (updated) {
                    doctorRepository.save(doc);
                }
            }
        }

        if (patientRepository.count() == 0) {
            log.info("Seeding default patient accounts...");
            Patient pat1 = new Patient();
            pat1.setName("Jane Doe");
            pat1.setEmail("patient@opd.com");
            pat1.setGender("Female");
            pat1.setAge(28);
            pat1.setPhoneNumber("9876543210");
            pat1.setPassword(passwordEncoder.encode("patient123"));
            patientRepository.save(pat1);

            Patient pat2 = new Patient();
            pat2.setName("John Smith");
            pat2.setEmail("john.smith@opd.com");
            pat2.setGender("Male");
            pat2.setAge(42);
            pat2.setPhoneNumber("9876543211");
            pat2.setPassword(passwordEncoder.encode("patient123"));
            patientRepository.save(pat2);
        } else {
            // Ensure existing patients have emails and encoded passwords if missing
            for (Patient pat : patientRepository.findAll()) {
                boolean updated = false;
                if (pat.getEmail() == null || pat.getEmail().isBlank()) {
                    pat.setEmail("patient" + pat.getId() + "@opd.com");
                    updated = true;
                }
                if (pat.getPassword() == null || pat.getPassword().isBlank() || !pat.getPassword().startsWith("$2a$")) {
                    pat.setPassword(passwordEncoder.encode(pat.getPassword() != null && !pat.getPassword().isBlank() ? pat.getPassword() : "patient123"));
                    updated = true;
                }
                if (updated) {
                    patientRepository.save(pat);
                }
            }
        }

        log.info("Seed data check completed.");
    }
}
