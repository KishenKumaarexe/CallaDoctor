package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClinicPatientAccept extends AppCompatActivity {
    Clinic_SessionManager clinicSessionManager;
    String getId, c_name, dates, times, uniqueID;
    Spinner spinner;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private ProgressBar loadingPB;

    private FloatingActionButton logout;


    Button assign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_patient_accept);

        Intent i = getIntent();

        dates = i.getStringExtra("date");
        times = i.getStringExtra("time");
        uniqueID = i.getStringExtra("uniqueid");
        loadingPB = findViewById(R.id.idPBLoading);
        logout = findViewById(R.id.idLogout4);


        assign = findViewById(R.id.idBtnRegister);




        clinicSessionManager = new Clinic_SessionManager(getApplicationContext());
        clinicSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = clinicSessionManager.getUserDetail();
        getId = user.get(clinicSessionManager.ID);
        c_name = user.get(clinicSessionManager.c_name);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clinicSessionManager.logout();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Doctor");


        spinner = findViewById(R.id.idDoctorName);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> items = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = snapshot.child("doctorName").getValue(String.class);

                    DatabaseReference availableRef = snapshot.child("Schedule").getRef();
                    availableRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String date = snapshot.child("date").getValue(String.class);

                            if(!dates.equals(date))
                            {
                                items.add(item);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ClinicPatientAccept.this, android.R.layout.simple_spinner_item, items);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingPB.setVisibility(View.VISIBLE);
                String doctorName = spinner.getSelectedItem().toString();

                DatabaseReference databaseReference2 = firebaseDatabase.getReference("Schedule");
                Query query = databaseReference2.orderByChild("doctorName").equalTo(doctorName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(!snapshot.exists()){
                                databaseReference2.child(uniqueID).child("date").setValue(dates);
                                databaseReference2.child(uniqueID).child("time").setValue(times);
                                databaseReference2.child(uniqueID).child("doctorName").setValue(doctorName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            // Data inserted successfully, show a toast message with the current time

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("status", "approve");
                                            map.put("doctorName", doctorName);
                                            DatabaseReference databaseReference3 = firebaseDatabase.getReference("Appointment");

                                            databaseReference3.child(uniqueID).updateChildren(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            loadingPB.setVisibility(View.GONE);
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                finish();

                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            // Handle the error case
                                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }
                                });


                        }
                        else{

                            for (DataSnapshot scheduleItem : snapshot.getChildren()) {
                                String date = scheduleItem.child("date").getValue(String.class);
                                if (!Objects.equals(date, dates)) {

                                    databaseReference2.child(uniqueID).child("date").setValue(dates);
                                    databaseReference2.child(uniqueID).child("time").setValue(times);
                                    databaseReference2.child(uniqueID).child("doctorName").setValue(doctorName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                // Data inserted successfully, show a toast message with the current time

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("status", "approve");
                                                map.put("doctorName", doctorName);
                                                DatabaseReference databaseReference3 = firebaseDatabase.getReference("Appointment");

                                                databaseReference3.child(uniqueID).updateChildren(map)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                loadingPB.setVisibility(View.GONE);
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                                    finish();

                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                // Handle the error case
                                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Doctor Unavailable", Toast.LENGTH_SHORT).show();
                                    loadingPB.setVisibility(View.GONE);

                                }
                            }

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });

    }


}