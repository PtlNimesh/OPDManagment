package com.OPD.OPDManagment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long apointmentId;
    private String bloodPressure;
    private String heartRate;
    private String notes;

    public Consultation() {
    }

    public Consultation(Long id, Long apointmentId, String bloodPressure, String heartRate, String notes) {
        this.id = id;
        this.apointmentId = apointmentId;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApointmentId() {
        return apointmentId;
    }

    public void setApointmentId(Long apointmentId) {
        this.apointmentId = apointmentId;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
