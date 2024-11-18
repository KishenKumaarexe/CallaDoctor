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
import java.util.Objects;

public class ClinicPatientView extends AppCompatActivity implements AppAdapter.appClickInterface {

    Clinic_SessionManager clinicSessionManager;
    String getId, c_name;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView clinicRV;
    private ProgressBar loadingPB;
    private ArrayList<AppModel> appRVModalArrayList;
    private AppAdapter appAdapter;
    private RelativeLayout homeRL;
    private BottomSheetDialog bottomSheetTeachersDialog;

    private FloatingActionButton logout;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_patient_view);

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
        appRVModalArrayList = new ArrayList<>();
        loadingPB = findViewById(R.id.idPBLoading);
        databaseReference = firebaseDatabase.getReference("Appointment");
        logout = findViewById(R.id.idLogout5);

        appAdapter = new AppAdapter(appRVModalArrayList, this, this::onAppClick);

        clinicRV.setLayoutManager(new LinearLayoutManager(this));

        clinicRV.setAdapter(appAdapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clinicSessionManager.logout();
            }
        });

        //getAppointment();


    }
    private void getAppointment() {
        appRVModalArrayList.clear();
        databaseReference.orderByChild("clinicName").equalTo(c_name).addChildEventListener(new ChildEventListener() {
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
                Toast.makeText(ClinicPatientView.this, "Network Issue..", Toast.LENGTH_SHORT).show();

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
        userNameTV.setText("Desc: "+appModel.getDescription());
        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnActivate);

        if(Objects.equals(appModel.getStatus(), "approve"))
        {
            statusTV.setText("Status: Approved");
            viewBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);

        }
        else if(Objects.equals(appModel.getStatus(), "decline"))
        {
            statusTV.setText("Status: Declined");
            viewBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
        }
        else
        {
            statusTV.setText("Status: Pending");
            viewBtn.setText("Decline");
            editBtn.setText("Assign Doctor");
        }





        viewBtn.setText("Decline");

        // adding on click listener for our edit button.
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);


                // on below line we are navigating to browser
                // for displaying course details from its url
                Map<String, Object> map = new HashMap<>();
                map.put("status", "decline");

                databaseReference.child(appModel.getUniqueID()).updateChildren(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingPB.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(ClinicPatientView.this, "Declined..", Toast.LENGTH_SHORT).show();
                                    bottomSheetTeachersDialog.dismiss();
                                    // Refresh the clinic list after updating the data
                                    getAppointment();
                                } else {
                                    Toast.makeText(ClinicPatientView.this, "Decline failed..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClinicPatientView.this, ClinicPatientAccept.class);
                i.putExtra("date", appModel.getDate());
                i.putExtra("time", appModel.getTime());
                i.putExtra("uniqueid", appModel.getUniqueID());
                startActivity(i);

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