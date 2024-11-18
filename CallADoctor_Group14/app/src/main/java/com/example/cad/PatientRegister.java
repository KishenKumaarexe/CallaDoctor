package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class PatientRegister extends AppCompatActivity {

    private Button Register;
    private TextInputEditText p_name, u_name, pass, confirmPass, lat, longi, email, phone;
    private TextView login;
    private ProgressBar loadingPB;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);

        Register = findViewById(R.id.idBtnRegister);
        p_name = findViewById(R.id.idPatientName);
        u_name = findViewById(R.id.idEdtUserName);
        pass = findViewById(R.id.idEdtPassword);
        confirmPass = findViewById(R.id.idEdtConfirmPassword);
        lat = findViewById(R.id.idLatitude);
        longi = findViewById(R.id.idLongitude);
        email = findViewById(R.id.idEdtEmail);
        phone = findViewById(R.id.idEdtPhoneNumber);
        loadingPB = findViewById(R.id.idPBLoading);
        login = findViewById(R.id.idTVLoginUser);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Patient");

        Register.setOnClickListener(view -> {
            loadingPB.setVisibility(View.VISIBLE);

            // Retrieve inputs
            String patientName = p_name.getText().toString().trim();
            String userName = u_name.getText().toString().trim();
            String password = pass.getText().toString().trim(); // Get plain text password
            String confirmPassword = confirmPass.getText().toString().trim(); // Get plain text confirm password
            String latitude = lat.getText().toString().trim();
            String longitude = longi.getText().toString().trim();
            String emailInput = email.getText().toString().trim();
            String phoneInput = phone.getText().toString().trim();

            // Validate inputs
            if (!validateInputs(patientName, userName, password, confirmPassword, latitude, longitude, emailInput, phoneInput)) {
                loadingPB.setVisibility(View.INVISIBLE);
                return;
            }

            // Compare plain-text passwords BEFORE encryption
            if (!password.equals(confirmPassword)) {
                confirmPass.setError("Passwords do not match");
                loadingPB.setVisibility(View.INVISIBLE);
                return;
            }

            // Encrypt the password AFTER validation
            String encryptedPassword = EncryptionUtils.encrypt(password);

            if (encryptedPassword == null) {
                Toast.makeText(PatientRegister.this, "Error encrypting password", Toast.LENGTH_SHORT).show();
                loadingPB.setVisibility(View.INVISIBLE);
                return;
            }

            // Create patient model with the encrypted password
            PatientModel patientModel = new PatientModel(patientName, userName, encryptedPassword, latitude, longitude, emailInput, phoneInput);

            // Check if the username already exists in the database
            databaseReference.orderByChild("userName")
                    .equalTo(userName.replaceAll("[@.#$]", "-"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                databaseReference.child(userName.replaceAll("[@.#$]", "-")).setValue(patientModel);
                                Toast.makeText(PatientRegister.this, "Patient Registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                loadingPB.setVisibility(View.INVISIBLE);
                                Toast.makeText(PatientRegister.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingPB.setVisibility(View.INVISIBLE);
                            Toast.makeText(PatientRegister.this, "Failed to register patient", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        login.setOnClickListener(view -> startActivity(new Intent(PatientRegister.this, PatientLogin.class)));
    }

    // Function to validate user inputs
    private boolean validateInputs(String patientName, String userName, String password, String confirmPassword, String latitude, String longitude, String email, String phone) {
        if (confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (patientName.isEmpty()) {
            p_name.setError("Patient Name cannot be empty");
            return false;
        }

        if (userName.isEmpty()) {
            u_name.setError("Username cannot be empty");
            return false;
        }

        if (password.isEmpty()) {
            pass.setError("Password cannot be empty");
            return false;
        }

        if (password.length() < 6) {
            pass.setError("Password must be at least 6 characters");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\+?[0-9]{10,13}")) {
            Toast.makeText(this, "Invalid phone number. Include country code.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (latitude.isEmpty()) {
            lat.setError("Latitude cannot be empty");
            return false;
        }

        if (!isNumeric(latitude) || Math.abs(Double.parseDouble(latitude)) > 90) {
            lat.setError("Invalid latitude");
            return false;
        }

        if (longitude.isEmpty()) {
            longi.setError("Longitude cannot be empty");
            return false;
        }

        if (!isNumeric(longitude) || Math.abs(Double.parseDouble(longitude)) > 180) {
            longi.setError("Invalid longitude");
            return false;
        }

        return true;
    }

    // Utility function to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
