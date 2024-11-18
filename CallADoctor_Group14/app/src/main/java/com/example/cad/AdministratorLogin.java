package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdministratorLogin extends AppCompatActivity {

    Administrator_SessionManager administrator_sessionManager;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private Button Login;

    String getId;
    private TextInputEditText username, password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_login);
        administrator_sessionManager = new Administrator_SessionManager(getApplicationContext());


        Login = findViewById(R.id.idBtnLogin);
        username = findViewById(R.id.idEdtUserName);
        password = findViewById(R.id.idEdtPassword);
        loadingPB = findViewById(R.id.idPBLoading);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Administrator");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);

                String userName = username.getText().toString();
                String passWord = password.getText().toString();

                DatabaseReference adminRef = databaseReference;

                adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String storedUsername = snapshot.child("username").getValue(String.class);
                        String storedPassword = snapshot.child("password").getValue(String.class);

                        if (storedUsername != null && storedPassword != null) {
                            if (storedUsername.equals(userName) && storedPassword.equals(passWord)) {
                                administrator_sessionManager.createSession(userName);
                                Toast.makeText(AdministratorLogin.this, "Login Successful..", Toast.LENGTH_SHORT).show();
                                // starting a main activity.
                                startActivity(new Intent(AdministratorLogin.this, AdministratorClinicView.class));

                            } else {
                                loadingPB.setVisibility(view.GONE);
                                Toast.makeText(AdministratorLogin.this, "Invalid Credential..", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            loadingPB.setVisibility(view.GONE);
                            Toast.makeText(AdministratorLogin.this, "Invalid Credential..", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdministratorLogin.this, "User not found..", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });





    }
}