package com.example.dental_channelling.Constructors;

public class Doctors {

    String doctorName, doctorId, doctorPosition, imageUrl;

    public Doctors(String doctorName, String doctorId, String doctorPosition, String imageUrl) {
        this.doctorName = doctorName;
        this.doctorId = doctorId;
        this.doctorPosition = doctorPosition;
        this.imageUrl = imageUrl;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorPosition() {
        return doctorPosition;
    }

    public void setDoctorPosition(String doctorPosition) {
        this.doctorPosition = doctorPosition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
