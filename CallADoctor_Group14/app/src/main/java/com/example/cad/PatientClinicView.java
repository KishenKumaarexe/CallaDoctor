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

public class PatientClinicView extends AppCompatActivity implements ClinicAdapter.ClinicClickInterface{
    Patient_SessionManager patientSessionManager;
    String getId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private RecyclerView clinicRV;
    private ProgressBar loadingPB;
    private ArrayList<ClinicModel> clinicRVModalArrayList;
    private ClinicAdapter clinicAdapter;
    private RelativeLayout homeRL;
    private FloatingActionButton logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_clinic_view);
        patientSessionManager = new Patient_SessionManager(getApplicationContext());
        patientSessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = patientSessionManager.getUserDetail();
        getId = user.get(patientSessionManager.ID);
        logout = findViewById(R.id.idLogout8);
        clinicRV = findViewById(R.id.idRVClinic);
        homeRL = findViewById(R.id.idRLBSheet);
        firebaseDatabase = FirebaseDatabase.getInstance();
        clinicRVModalArrayList = new ArrayList<>();
        loadingPB = findViewById(R.id.idPBLoading);

        databaseReference = firebaseDatabase.getReference("Clinic");

        clinicAdapter = new ClinicAdapter(clinicRVModalArrayList, this, this::onClinicClick);

        clinicRV.setLayoutManager(new LinearLayoutManager(this));

        clinicRV.setAdapter(clinicAdapter);

        getClinic();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientSessionManager.logout();
            }
        });

    }

    private void getClinic() {
        clinicRVModalArrayList.clear();
        databaseReference.orderByChild("active").equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);

                clinicRVModalArrayList.add(snapshot.getValue(ClinicModel.class));

                clinicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadingPB.setVisibility(View.GONE);
                clinicAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                clinicAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                clinicAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PatientClinicView.this, "Network Issue..", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onClinicClick(int position) {
        displayBottomSheet(clinicRVModalArrayList.get(position));

    }



    private void displayBottomSheet(ClinicModel clinicModel) {

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
        clinicNameTV.setText("Clinic: "+clinicModel.getClinicName());
        userNameTV.setText("Username: "+clinicModel.getUserName());
        if(clinicModel.getActive()==true)
        {
            statusTV.setText("Status: Active");
        }
        else
        {
            statusTV.setText("Status: Inactive");
        }


        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnActivate);

        editBtn.setText("Appointment");

        // adding on click listener for our edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appointment = new Intent(PatientClinicView.this, PatientBookView.class);
                appointment.putExtra("clinic_name",clinicModel.getClinicName());
                startActivity(appointment);

            }
        });
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are navigating to browser
                // for displaying course details from its url
                Intent i = new Intent(PatientClinicView.this, AdministratorMap.class);
                // on below line we are passing our course modal
                i.putExtra("latitude", clinicModel.getLatitude());
                i.putExtra("longitude", clinicModel.getLongitude());
                i.putExtra("clinic", clinicModel.getClinicName());
                startActivity(i);
            }
        });
    }
}
