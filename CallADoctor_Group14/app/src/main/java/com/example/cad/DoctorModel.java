package com.example.cad;

import android.os.Parcel;
import android.os.Parcelable;

public class DoctorModel {
    private String doctorName;
    private String userName;
    private String password;
    private String qualifications;
    private String specializations;
    private String clinicName;




    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }


    // creating an empty constructor.
    public DoctorModel() {

    }

    protected DoctorModel(Parcel in) {
        doctorName = in.readString();
        userName = in.readString();
        password = in.readString();
        qualifications = in.readString();
        specializations = in.readString();
        clinicName = in.readString();

    }

    public static final Parcelable.Creator<DoctorModel> CREATOR = new Parcelable.Creator<DoctorModel>() {
        @Override
        public DoctorModel createFromParcel(Parcel in) {
            return new DoctorModel(in);
        }

        @Override
        public DoctorModel[] newArray(int size) {
            return new DoctorModel[size];
        }
    };

    // creating getter and setter methods.
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public String getSpecializations() {
        return specializations;
    }

    public void setSpecializations(String specializations) {
        this.specializations = specializations;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public DoctorModel(String doctorName, String userName, String password, String qualifications, String specializations, String clinicName) {
        this.doctorName = doctorName;
        this.userName = userName;
        this.password = password;
        this.qualifications = qualifications;
        this.specializations = specializations;
        this.clinicName = clinicName;

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doctorName);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(qualifications);
        dest.writeString(specializations);
        dest.writeString(clinicName);

    }

}
