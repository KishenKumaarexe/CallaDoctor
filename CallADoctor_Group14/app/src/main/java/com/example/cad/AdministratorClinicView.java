package com.example.cad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ValueEventListener;

public class AdministratorClinicView extends AppCompatActivity implements ClinicAdapter.ClinicClickInterface{
    Administrator_SessionManager administrator_sessionManager;
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
        setContentView(R.layout.activity_administrator_clinic_view);

        administrator_sessionManager = new Administrator_SessionManager(getApplicationContext());
        administrator_sessionManager.checkLogin();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HashMap<String, String> user = administrator_sessionManager.getUserDetail();
        getId = user.get(administrator_sessionManager.ID);

        clinicRV = findViewById(R.id.idRVClinic);
        homeRL = findViewById(R.id.idRLBSheet);
        firebaseDatabase = FirebaseDatabase.getInstance();
        clinicRVModalArrayList = new ArrayList<>();
        loadingPB = findViewById(R.id.idPBLoading);
        logout = findViewById(R.id.idLogout3);


        databaseReference = firebaseDatabase.getReference("Clinic");

        clinicAdapter = new ClinicAdapter(clinicRVModalArrayList, this, this::onClinicClick);

        clinicRV.setLayoutManager(new LinearLayoutManager(this));

        clinicRV.setAdapter(clinicAdapter);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                administrator_sessionManager.logout();
            }
        });

        getClinic();

    }

    private void getClinic() {
        clinicRVModalArrayList.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
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
                Toast.makeText(AdministratorClinicView.this, "Network Issue..", Toast.LENGTH_SHORT).show();

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
        Button delBtn = layout.findViewById(R.id.idBtnDelete);

        delBtn.setVisibility(View.VISIBLE);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                // on below line we are opening our EditCourseActivity on below line.
                // Reference the specific clinic to delete by its name
                databaseReference.child(clinicModel.getClinicName()).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Hide the progress bar once the deletion is complete
                                loadingPB.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Display success message and refresh the clinic list
                                    Toast.makeText(AdministratorClinicView.this, "Clinic deleted successfully.", Toast.LENGTH_SHORT).show();
                                    bottomSheetTeachersDialog.dismiss();
                                    // Call the method to refresh the clinic list after deletion
                                    getClinic();
                                } else {
                                    // Display failure message if the deletion fails
                                    Toast.makeText(AdministratorClinicView.this, "Failed to delete clinic.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // adding on click listener for our edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                // on below line we are opening our EditCourseActivity on below line.
                Boolean isActive = false;

                if(clinicModel.getActive()==true)
                {
                    isActive = false;
                }
                else
                {
                    isActive = true;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("active", isActive);

                databaseReference.child(clinicModel.getClinicName()).updateChildren(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingPB.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdministratorClinicView.this, "Clinic updated..", Toast.LENGTH_SHORT).show();
                                    bottomSheetTeachersDialog.dismiss();
                                    // Refresh the clinic list after updating the data
                                    getClinic();
                                } else {
                                    Toast.makeText(AdministratorClinicView.this, "Update failed..", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are navigating to browser
                // for displaying course details from its url
                Intent i = new Intent(AdministratorClinicView.this, AdministratorMap.class);
                // on below line we are passing our course modal
                i.putExtra("latitude", clinicModel.getLatitude());
                i.putExtra("longitude", clinicModel.getLongitude());
                i.putExtra("clinic", clinicModel.getClinicName());
                startActivity(i);
            }
        });
    }
}
