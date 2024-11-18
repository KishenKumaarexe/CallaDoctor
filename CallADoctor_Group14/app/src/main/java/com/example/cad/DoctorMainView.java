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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorMainView extends AppCompatActivity implements AppAdapter.appClickInterface{
    Doctor_SessionManager doctorSessionManager;
    String getId, d_name;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FloatingActionButton addDoctor;
    private RecyclerView clinicRV;
    private ProgressBar loadingPB;
    private ArrayList<AppModel> appRVModalArrayList;
    private AppAdapter appAdapter;
    private RelativeLayout homeRL;

    BottomSheetDialog bottomSheetTeachersDialog;

    private FloatingActionButton logout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main_view);

        doctorSessionManager = new Doctor_SessionManager(getApplicationContext());
        doctorSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = doctorSessionManager.getUserDetail();
        getId = user.get(doctorSessionManager.ID);
        d_name = user.get(doctorSessionManager.DOCTOR);

        clinicRV = findViewById(R.id.idRVClinic);
        homeRL = findViewById(R.id.idRLBSheet);
        firebaseDatabase = FirebaseDatabase.getInstance();
        appRVModalArrayList = new ArrayList<>();
        loadingPB = findViewById(R.id.idPBLoading);

        databaseReference = firebaseDatabase.getReference("Appointment");

        appAdapter = new AppAdapter(appRVModalArrayList, this, this::onAppClick);

        clinicRV.setLayoutManager(new LinearLayoutManager(this));

        clinicRV.setAdapter(appAdapter);
        logout = findViewById(R.id.idLogout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doctorSessionManager.logout();
            }
        });


    }

    private void getAppointment() {
        appRVModalArrayList.clear();
        databaseReference.orderByChild("doctorName").equalTo(d_name).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);

                appRVModalArrayList.add(snapshot.getValue(AppModel.class));

                appAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                appAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                appAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorMainView.this, "Network Issue..", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onAppClick(int position) {
        displayBottomSheet(appRVModalArrayList.get(position));

    }



    private void displayBottomSheet(AppModel appModel) {

        bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
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
        clinicNameTV.setText("Patient: "+appModel.getPatientName());
        userNameTV.setText("Prescribe: "+appModel.getPrescription());
        statusTV.setText("Desc: "+appModel.getDescription());
        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnActivate);
        if(appModel.getPrescription().equals(""))
        {
            editBtn.setText("Prescription");
        }
        else
        {
            editBtn.setVisibility(View.GONE);
        }




        //editBtn.setText("M");

        // adding on click listener for our edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appointment = new Intent(DoctorMainView.this, DoctorPatientPrescription.class);
                appointment.putExtra("uniqueID",appModel.getUniqueID());
                startActivity(appointment);

            }
        });
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference2 = firebaseDatabase.getReference("Patient");
                Query query = databaseReference2.orderByChild("patientName").equalTo(appModel.getPatientName());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if(snapshot2.exists())
                        {
                            for (DataSnapshot userSnapshot : snapshot2.getChildren()) {

                                String latitude = userSnapshot.child("latitude").getValue(String.class);
                                String longitude = userSnapshot.child("longitude").getValue(String.class);
                                String p_nm = userSnapshot.child("patientName").getValue(String.class);

                        Intent i = new Intent(DoctorMainView.this, AdministratorMap.class);
                        // on below line we are passing our course modal
                        i.putExtra("latitude", latitude);
                        i.putExtra("longitude", longitude);
                        i.putExtra("clinic", p_nm);
                        startActivity(i);
                            }

                        }
                        else{
                            Toast.makeText(DoctorMainView.this, "Failed", Toast.LENGTH_SHORT).show();

                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DoctorMainView.this, "Failed", Toast.LENGTH_SHORT).show();

                    }
                });



                // on below line we are navigating to browser
                // for displaying course details from its url

            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (bottomSheetTeachersDialog != null && bottomSheetTeachersDialog.isShowing()) {
            bottomSheetTeachersDialog.dismiss();
        }
        getAppointment();
        //getDoctor();
        // Perform actions you want when this activity resumes

        // For example, you can update UI elements or refresh data here
    }
}

