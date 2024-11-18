package com.example.cad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cad.utils.EncryptionUtils;

public class MainActivity extends AppCompatActivity {

    private Button administrator, clinic, doctor, patient, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the encryption utility (can be used for encrypted preferences or other data)
        EncryptionUtils.init(getApplicationContext());

        // Initialize buttons
        administrator = findViewById(R.id.idBtnSuper);
        clinic = findViewById(R.id.idBtnClinic);
        doctor = findViewById(R.id.idBtnDoctor);
        patient = findViewById(R.id.idBtnPatient);
        loginButton = findViewById(R.id.loginButton);

        // Set up listeners for roles
        administrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_admin = new Intent(MainActivity.this, AdministratorClinicView.class);
                startActivity(pro_admin);
            }
        });

        clinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_clinic = new Intent(MainActivity.this, ClinicMainView.class);
                startActivity(pro_clinic);
            }
        });

        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_doctor = new Intent(MainActivity.this, DoctorMainView.class);
                startActivity(pro_doctor);
            }
        });

        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_patient = new Intent(MainActivity.this, PatientMainView.class);
                startActivity(pro_patient);
            }
        });

        // Login button (no password required anymore)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    // Method to login user (No password verification needed anymore)
    private void loginUser() {
        // As there is no password validation anymore, the login is now based only on user roles
        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
        // Proceed with login (you may proceed directly or with other role-based navigation logic)
    }
}
