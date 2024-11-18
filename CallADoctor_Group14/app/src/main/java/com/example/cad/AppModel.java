package com.example.cad;

import android.os.Parcel;
import android.os.Parcelable;

public class AppModel {
    private String uniqueID;
    private String patientUserName;
    private String patientName;
    private String description;
    private String prescription;
    private String clinicName;
    private String date;
    private String time;
    private String status;
    private String doctorName;


    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    // creating an empty constructor.
    public AppModel() {

    }

    protected AppModel(Parcel in) {
        uniqueID=in.readString();
        patientUserName = in.readString();
        patientName = in.readString();
        description = in.readString();
        clinicName = in.readString();
        date = in.readString();
        time = in.readString();
        status = in.readString();
        prescription = in.readString();
        doctorName = in.readString();

    }

    public static final Parcelable.Creator<AppModel> CREATOR = new Parcelable.Creator<AppModel>() {
        @Override
        public AppModel createFromParcel(Parcel in) {
            return new AppModel(in);
        }

        @Override
        public AppModel[] newArray(int size) {
            return new AppModel[size];
        }
    };


    public String getPatientUserName() {
        return patientUserName;
    }

    public void setPatientUserName(String patientUserName) {
        this.patientUserName = patientUserName;
    }
    // creating getter and setter methods.
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setSpecializations(String specializations) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public AppModel(String uniqueID, String patientUserName, String patientName, String description, String clinicName, String date, String time, String status, String prescription, String doctorName) {
        this.uniqueID = uniqueID;
        this.patientUserName = patientUserName;
        this.description = description;
        this.clinicName = clinicName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.patientName = patientName;
        this.prescription = prescription;
        this.doctorName = doctorName;

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueID);
        dest.writeString(patientUserName);
        dest.writeString(description);
        dest.writeString(clinicName);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(status);
        dest.writeString(patientName);
        dest.writeString(prescription);
        dest.writeString(doctorName);

    }

}
