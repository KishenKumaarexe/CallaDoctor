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

import com.example.cad.utils.EncryptionUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

public class PatientLogin extends AppCompatActivity {

    Patient_SessionManager patientSessionManager;

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
        setContentView(R.layout.activity_patient_login);
        patientSessionManager = new Patient_SessionManager(getApplicationContext());

        NUser = findViewById(R.id.idTVNewUser);
        Login = findViewById(R.id.idBtnLogin);
        username = findViewById(R.id.idEdtUserName);
        password = findViewById(R.id.idEdtPassword);
        loadingPB = findViewById(R.id.idPBLoading);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Patient");


        // adding click listener for our add course button.

        NUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pro_admin = new Intent(PatientLogin.this, PatientRegister.class);
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

                                String patientName = userSnapshot.child("patientName").getValue(String.class);

                                // Hash the entered password, call the method.
                                String hashedInputPassword = hashPassword(passWord);
                                if (storedPassword != null && storedPassword.equals(passWord)) {

                                    patientSessionManager.createSession(userName,patientName);
                                    Toast.makeText(PatientLogin.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    // starting a main activity.
                                    startActivity(new Intent(PatientLogin.this, PatientMainView.class));


                                } else {
                                    loadingPB.setVisibility(view.INVISIBLE);
                                    Toast.makeText(PatientLogin.this, "Invalid Credential..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else {
                            loadingPB.setVisibility(view.INVISIBLE);
                            Toast.makeText(PatientLogin.this, "User not found..", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PatientLogin.this, "User not found..", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }

    // method to convert the password entered, to make it into a hash (SHA-256),
    // and then compare it with the stored hash, to Success Login.
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}