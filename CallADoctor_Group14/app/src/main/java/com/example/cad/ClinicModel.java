package com.example.cad;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class ClinicModel implements Parcelable{
    private String clinicName;
    private String userName;
    private String password;
    private String latitude;
    private String longitude;
    private Boolean active;



    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }


    // creating an empty constructor.
    public ClinicModel() {

    }

    protected ClinicModel(Parcel in) {
        clinicName = in.readString();
        userName = in.readString();
        password = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        active = in.readBoolean();

    }

    public static final Creator<ClinicModel> CREATOR = new Creator<ClinicModel>() {
        @Override
        public ClinicModel createFromParcel(Parcel in) {
            return new ClinicModel(in);
        }

        @Override
        public ClinicModel[] newArray(int size) {
            return new ClinicModel[size];
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public Boolean getActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }



    public ClinicModel(String clinicName, String userName, String password, String latitude, String longitude, Boolean active) {
        this.clinicName = clinicName;
        this.userName = userName;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clinicName);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeBoolean(active);

    }

}
