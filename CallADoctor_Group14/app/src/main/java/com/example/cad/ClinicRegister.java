package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.cad.utils.EncryptionUtils;

public class ClinicRegister extends AppCompatActivity {
    private Button Register;
    private TextInputEditText c_name, u_name, pass, lat, longi;

    private TextView login;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_register);
        Register = findViewById(R.id.idBtnRegister);
        c_name = findViewById(R.id.idClinicName);
        u_name = findViewById(R.id.idEdtUserName);
        pass = findViewById(R.id.idEdtPassword);
        lat = findViewById(R.id.idLatitude);
        longi = findViewById(R.id.idLongitude);
        loadingPB = findViewById(R.id.idPBLoading);
        login = findViewById(R.id.idTVLoginUser);


        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Clinic");
        // adding click listener for our add course button.


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);

                String ClinicName = c_name.getText().toString();
                String UserName = u_name.getText().toString();
                String PassWord = pass.getText().toString();
                PassWord = EncryptionUtils.encrypt(PassWord);
                String Latitude = lat.getText().toString();
                String Longitude = longi.getText().toString();
                Boolean active = false;

                ClinicModel clinicModel = new ClinicModel(ClinicName,UserName,PassWord,Latitude,Longitude,active);

                DatabaseReference clinicsReference = databaseReference;
                clinicsReference.orderByChild("clinicName").equalTo(ClinicName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            clinicsReference.orderByChild("userName").equalTo(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists())
                                    {
                                        databaseReference.child(ClinicName).setValue(clinicModel);
                                        Toast.makeText(ClinicRegister.this, "Clinic Added..", Toast.LENGTH_SHORT).show();
                                        // starting a main activity.
                                        startActivity(new Intent(ClinicRegister.this, ClinicLogin.class));
                                    }
                                    else {
                                        loadingPB.setVisibility(view.INVISIBLE);
                                        Toast.makeText(ClinicRegister.this, "Username exist..", Toast.LENGTH_SHORT).show();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ClinicRegister.this, "Fail to add clinic..", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        else {
                            loadingPB.setVisibility(view.INVISIBLE);

                            Toast.makeText(ClinicRegister.this, "Clinic name exist..", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClinicRegister.this, "Fail to add clinic..", Toast.LENGTH_SHORT).show();

                    }
                });

//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(ClinicRegister.this, "Fail to add clinic..", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClinicRegister.this, ClinicLogin.class));
            }
        });

    }
}