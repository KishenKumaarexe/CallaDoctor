package com.example.cad;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class ClinicMainView extends AppCompatActivity {

    Clinic_SessionManager clinicSessionManager;
    String getId;

    Button n_request, doctors;

    private FloatingActionButton logout;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_main_view);

        clinicSessionManager = new Clinic_SessionManager(getApplicationContext());
        clinicSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = clinicSessionManager.getUserDetail();
        getId = user.get(clinicSessionManager.ID);

        n_request = findViewById(R.id.idBtnNew);
        doctors = findViewById(R.id.idBtnDoctors);
        logout = findViewById(R.id.idLogout);


        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clinic_doctor = new Intent(ClinicMainView.this, ClinicDoctorView.class);
                startActivity(clinic_doctor);
            }
        });
        n_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clinic_patient = new Intent(ClinicMainView.this, ClinicPatientView.class);
                startActivity(clinic_patient);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clinicSessionManager.logout();
            }
        });

    }
}