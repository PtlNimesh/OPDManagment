package com.OPD.OPDManagment.dto;

public class ConsultationResponseDto {

    private Long id;
    private Long apointmentId;
    private String bloodPressure;
    private String heartRate;
    private String notes;

    public ConsultationResponseDto() {
    }

    public ConsultationResponseDto(Long id, Long apointmentId, String bloodPressure, String heartRate, String notes) {
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
