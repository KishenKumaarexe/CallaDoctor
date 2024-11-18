package com.example.cad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class PatientMainView extends AppCompatActivity {
    Patient_SessionManager patientSessionManager;
    String getId;
    Button n_req, app;
    private FloatingActionButton logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main_view);

        patientSessionManager = new Patient_SessionManager(getApplicationContext());
        patientSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = patientSessionManager.getUserDetail();
        getId = user.get(patientSessionManager.ID);


        n_req = findViewById(R.id.idBtnNewR);
        app = findViewById(R.id.idBtnDoctors);

        logout = findViewById(R.id.idLogout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientSessionManager.logout();
            }
        });

        n_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PatientMainView.this, PatientClinicView.class);
                startActivity(i);
            }
        });

        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PatientMainView.this, PatientPrescriptionView.class);
                startActivity(i);
            }
        });


    }
}