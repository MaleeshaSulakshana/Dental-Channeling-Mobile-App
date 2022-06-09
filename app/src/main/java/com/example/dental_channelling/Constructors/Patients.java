package com.example.dental_channelling.Constructors;

public class Patients {

    String patientName, patientId, patientImage;

    public Patients(String patientName, String patientId, String patientImage) {
        this.patientName = patientName;
        this.patientId = patientId;
        this.patientImage = patientImage;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientImage() {
        return patientImage;
    }

    public void setPatientImage(String patientImage) {
        this.patientImage = patientImage;
    }
}
