package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.cad.utils.EncryptionUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

public class ClinicDoctorRegister extends AppCompatActivity {
    Clinic_SessionManager clinicSessionManager;
    String getId, c_name;

    private Button Register;

    private ProgressBar loadingPB;

    private TextInputEditText d_name, u_name, pass, qual, special;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_doctor_register);

        Register = findViewById(R.id.idBtnRegister);
        d_name = findViewById(R.id.idDoctorName);
        u_name = findViewById(R.id.idEdtUserName);
        pass = findViewById(R.id.idEdtPassword);
        qual = findViewById(R.id.idQualification);
        special = findViewById(R.id.idSpecialization);
        loadingPB = findViewById(R.id.idPBLoading);

        clinicSessionManager = new Clinic_SessionManager(getApplicationContext());
        clinicSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = clinicSessionManager.getUserDetail();
        getId = user.get(clinicSessionManager.ID);
        c_name = user.get(clinicSessionManager.c_name);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Doctor");

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);

                String DoctorName = d_name.getText().toString();
                String UserName = u_name.getText().toString();
                String PassWord = pass.getText().toString();
                PassWord = EncryptionUtils.encrypt(PassWord);
                String Qualification = qual.getText().toString();
                String Specialization = special.getText().toString();

                DoctorModel doctorModel = new DoctorModel(DoctorName,UserName,PassWord,Qualification,Specialization,c_name);

                DatabaseReference clinicsReference = databaseReference;
                clinicsReference.orderByChild("doctorName").equalTo(DoctorName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            clinicsReference.orderByChild("userName").equalTo(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists())
                                    {
                                        databaseReference.child(DoctorName).setValue(doctorModel);
                                        Toast.makeText(ClinicDoctorRegister.this, "Doctor Added..", Toast.LENGTH_SHORT).show();
                                        // starting a main activity.
                                        finish();
                                    }
                                    else {
                                        loadingPB.setVisibility(view.INVISIBLE);
                                        Toast.makeText(ClinicDoctorRegister.this, "Username exist..", Toast.LENGTH_SHORT).show();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ClinicDoctorRegister.this, "Fail to add doctor..", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        else {
                            loadingPB.setVisibility(view.INVISIBLE);

                            Toast.makeText(ClinicDoctorRegister.this, "Doctor name exist..", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClinicDoctorRegister.this, "Fail to add doctor..", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });




    }
}