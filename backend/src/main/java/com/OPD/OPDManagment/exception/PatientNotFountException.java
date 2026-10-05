package com.OPD.OPDManagment.exception;

public class PatientNotFountException extends ResourceNotFoundException {
    public PatientNotFountException(String message) {
        super(message);
    }
}
