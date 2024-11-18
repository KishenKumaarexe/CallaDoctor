package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.cad.utils.EncryptionUtils;


public class ClinicLogin extends AppCompatActivity {

    Clinic_SessionManager clinicSessionManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private TextView NUser;
    private Button Login;

    String getId;


    private TextInputEditText username, password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_login);
        clinicSessionManager = new Clinic_SessionManager(getApplicationContext());


        NUser = findViewById(R.id.idTVNewUser);
        Login = findViewById(R.id.idBtnLogin);
        username = findViewById(R.id.idEdtUserName);
        password = findViewById(R.id.idEdtPassword);
        loadingPB = findViewById(R.id.idPBLoading);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Clinic");


        // adding click listener for our add course button.

        NUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_admin = new Intent(ClinicLogin.this, ClinicRegister.class);
                startActivity(pro_admin);

            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);

                String userName = username.getText().toString();
                String passWord = password.getText().toString();


                DatabaseReference clinicsReference = databaseReference;
                clinicsReference.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String storedPassword = userSnapshot.child("password").getValue(String.class);
                                storedPassword = EncryptionUtils.decrypt(storedPassword);
                                boolean isActive = userSnapshot.child("active").getValue(Boolean.class);
                                String clinicName = userSnapshot.child("clinicName").getValue(String.class);

                                if (storedPassword != null && storedPassword.equals(passWord) && isActive) {

                                    clinicSessionManager.createSession(userName,clinicName);
                                    Toast.makeText(ClinicLogin.this, clinicName, Toast.LENGTH_SHORT).show();
                                    // starting a main activity.
                                    startActivity(new Intent(ClinicLogin.this, ClinicMainView.class));


                                } else {
                                    loadingPB.setVisibility(view.INVISIBLE);
                                    Toast.makeText(ClinicLogin.this, "Invalid Credential..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            loadingPB.setVisibility(view.INVISIBLE);
                            Toast.makeText(ClinicLogin.this, "User not found..", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClinicLogin.this, "User not found..", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }
}