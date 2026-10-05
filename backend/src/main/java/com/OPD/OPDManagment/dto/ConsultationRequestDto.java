package com.OPD.OPDManagment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConsultationRequestDto {

    @NotNull(message = "Appointment ID is required")
    private Long apointmentId;

    @NotBlank(message = "Blood pressure is required")
    private String bloodPressure;

    @NotBlank(message = "Heart rate is required")
    private String heartRate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public ConsultationRequestDto() {
    }

    public ConsultationRequestDto(Long apointmentId, String bloodPressure, String heartRate, String notes) {
        this.apointmentId = apointmentId;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.notes = notes;
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
