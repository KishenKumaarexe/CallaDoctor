package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DoctorPatientPrescription extends AppCompatActivity {
    Doctor_SessionManager doctorSessionManager;
    String getId, d_name, uniqueID;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Button Submit;
    private ProgressBar loadingPB;

    private FloatingActionButton logout;



    TextInputEditText prescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_prescription);
        Intent i = getIntent();

        uniqueID = i.getStringExtra("uniqueID");

        doctorSessionManager = new Doctor_SessionManager(getApplicationContext());
        doctorSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = doctorSessionManager.getUserDetail();
        getId = user.get(doctorSessionManager.ID);
        d_name = user.get(doctorSessionManager.DOCTOR);

        Submit = findViewById(R.id.idBtnRegister);
        logout = findViewById(R.id.idLogout7);

        prescription = findViewById(R.id.idEdtPrescription);
        loadingPB = findViewById(R.id.idPBLoading);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Appointment");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorSessionManager.logout();
            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);
                String Prescription = prescription.getText().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("prescription", Prescription);

                databaseReference.child(uniqueID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingPB.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(DoctorPatientPrescription.this, "Prescription added..", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DoctorPatientPrescription.this, "Update failed..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
}