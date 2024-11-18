package com.example.cad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClinicDoctorView extends AppCompatActivity implements DoctorAdapter.doctorClickInterface {
    Clinic_SessionManager clinicSessionManager;
    String getId, c_name;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FloatingActionButton addDoctor;
    private RecyclerView clinicRV;
    private ProgressBar loadingPB;
    private ArrayList<DoctorModel> doctorRVModalArrayList;
    private DoctorAdapter doctorAdapter;
    private RelativeLayout homeRL;

    private FloatingActionButton logout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_doctor_view);

        clinicSessionManager = new Clinic_SessionManager(getApplicationContext());
        clinicSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = clinicSessionManager.getUserDetail();
        getId = user.get(clinicSessionManager.ID);
        c_name = user.get(clinicSessionManager.c_name);

        clinicRV = findViewById(R.id.idRVClinic);
        homeRL = findViewById(R.id.idRLBSheet);
        firebaseDatabase = FirebaseDatabase.getInstance();
        doctorRVModalArrayList = new ArrayList<>();
        loadingPB = findViewById(R.id.idPBLoading);
        addDoctor = findViewById(R.id.idFABAddCourse);
        logout = findViewById(R.id.idLogout2);


        databaseReference = firebaseDatabase.getReference("Doctor");

        addDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity for adding a course.
                Intent i = new Intent(ClinicDoctorView.this, ClinicDoctorRegister.class);
                //i.putExtra("clinicName", )
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clinicSessionManager.logout();
            }
        });

        doctorAdapter = new DoctorAdapter(doctorRVModalArrayList, this, this::onDoctorClick);

        clinicRV.setLayoutManager(new LinearLayoutManager(this));

        clinicRV.setAdapter(doctorAdapter);

        getDoctor();
    }



    private void getDoctor() {
        doctorRVModalArrayList.clear();
        databaseReference.orderByChild("clinicName").equalTo(c_name).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                loadingPB.setVisibility(View.GONE);

                doctorRVModalArrayList.add(snapshot.getValue(DoctorModel.class));

                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                doctorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                doctorAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                doctorAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClinicDoctorView.this, "Network Issue..", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onDoctorClick(int position) {
        displayBottomSheet(doctorRVModalArrayList.get(position));

    }

    private void displayBottomSheet(DoctorModel doctorModel) {

        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // on below line we are inflating our layout file for our bottom sheet.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRL);
        // setting content view for bottom sheet on below line.
        bottomSheetTeachersDialog.setContentView(layout);
        // on below line we are setting a cancelable
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        // calling a method to display our bottom sheet.
        bottomSheetTeachersDialog.show();
        // on below line we are creating variables for
        // our text view and image view inside bottom sheet
        // and initializing them with their ids.
        TextView clinicNameTV = layout.findViewById(R.id.idTVClinicName);
        TextView userNameTV = layout.findViewById(R.id.idTVUserName);
        TextView statusTV = layout.findViewById(R.id.idTVStatus);
        // on below line we are setting data to different views on below line.
        clinicNameTV.setText("Doctor: "+doctorModel.getDoctorName());
        userNameTV.setText("Spec: "+doctorModel.getSpecializations());
        statusTV.setText("Status: "+doctorModel.getQualifications());



        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnActivate);

        editBtn.setVisibility(View.GONE);
        viewBtn.setText("Delete");

        // adding on click listener for our edit button.
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);


                // on below line we are navigating to browser
                // for displaying course details from its url

                databaseReference.child(doctorModel.getDoctorName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        loadingPB.setVisibility(View.GONE);

                        Toast.makeText(ClinicDoctorView.this, "Doctor Deleted..", Toast.LENGTH_SHORT).show();
                        bottomSheetTeachersDialog.dismiss();

                        getDoctor();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingPB.setVisibility(View.GONE);

                    }
                });

            }
        });
    }

    protected void onResume() {
        super.onResume();

        //getDoctor();
        // Perform actions you want when this activity resumes

        // For example, you can update UI elements or refresh data here
    }

}