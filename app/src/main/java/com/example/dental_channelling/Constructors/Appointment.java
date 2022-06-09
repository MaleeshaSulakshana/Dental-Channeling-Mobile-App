package com.example.dental_channelling.Constructors;

public class Appointment {

    String appointmentPatientName, appointmentId, appointmentDate;

    public Appointment(String appointmentPatientName, String appointmentId, String appointmentDate) {
        this.appointmentPatientName = appointmentPatientName;
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentPatientName() {
        return appointmentPatientName;
    }

    public void setAppointmentPatientName(String appointmentPatientName) {
        this.appointmentPatientName = appointmentPatientName;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
