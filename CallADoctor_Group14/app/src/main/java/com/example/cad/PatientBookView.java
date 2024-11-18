package com.example.cad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class PatientBookView extends AppCompatActivity {
    Patient_SessionManager patientSessionManager;
    String getId, patientname, clinicname;

    Button book;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private FloatingActionButton logout;




    private TextInputEditText description, date, time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_view);

        Intent intent = getIntent();

        clinicname = intent.getStringExtra("clinic_name");

        patientSessionManager = new Patient_SessionManager(getApplicationContext());
        patientSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = patientSessionManager.getUserDetail();
        getId = user.get(patientSessionManager.ID);
        patientname = user.get(patientSessionManager.PATIENT);
        logout = findViewById(R.id.idLogout6);




        description = findViewById(R.id.idEdtDescription);
        date = findViewById(R.id.idBtnPickDate);
        time = findViewById(R.id.idEdtPickTime);
        book = findViewById(R.id.idBtnBook);
        loadingPB = findViewById(R.id.idPBLoading);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // on below line creating our database reference.
        databaseReference = firebaseDatabase.getReference("Appointment");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientSessionManager.logout();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {

            final Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int day = currentDate.get(Calendar.DAY_OF_MONTH);
            @Override
            public void onClick(View view) {
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PatientBookView.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on below line we are getting the
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(PatientBookView.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Format hour and minute as two digits with leading zeros.
                                String formattedHour = String.format("%02d", hourOfDay);
                                String formattedMinute = String.format("%02d", minute);

                                // Set the selected time with two-digit formatting in the text view.
                                time.setText(formattedHour + ":" + formattedMinute);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingPB.setVisibility(View.VISIBLE);

                String Description = description.getText().toString();
                String Date = date.getText().toString();
                String Time = time.getText().toString();
                UUID uuid = UUID.randomUUID();
                String status = "";
                String preciscription = "";
                String doctorName = "";
                String uuidString = uuid.toString();

                AppModel appModel = new AppModel(uuidString,getId,patientname,Description,clinicname,Date,Time,status,preciscription,doctorName);

                DatabaseReference clinicsReference = databaseReference;
                clinicsReference.orderByChild("uniqueID").equalTo(uuidString).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                        {
                            databaseReference.child(uuidString).setValue(appModel);
                            Toast.makeText(PatientBookView.this, clinicname, Toast.LENGTH_SHORT).show();
                            // starting a main activity.

                            finish();
                        }
                        else {
                            loadingPB.setVisibility(view.INVISIBLE);
                            Toast.makeText(PatientBookView.this, "Appointment exist..", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PatientBookView.this, "Fail to add Appointment..", Toast.LENGTH_SHORT).show();

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



    }
}