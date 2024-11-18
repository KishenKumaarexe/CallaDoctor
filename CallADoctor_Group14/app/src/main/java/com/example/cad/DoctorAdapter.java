package com.example.cad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private ArrayList<DoctorModel> doctorModalArrayList;
    private Context context;
    private DoctorAdapter.doctorClickInterface doctorClickInterface;
    int lastPos = -1;

    // creating a constructor.
    public DoctorAdapter(ArrayList<DoctorModel> doctorModalArrayList, Context context, DoctorAdapter.doctorClickInterface doctorClickInterface) {
        this.doctorModalArrayList = doctorModalArrayList;
        this.context = context;
        this.doctorClickInterface = doctorClickInterface;
    }
    @NonNull
    @Override
    public DoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new DoctorAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DoctorAdapter.ViewHolder holder, int position) {
        // setting data to our recycler view item on below line.
        DoctorModel doctorModel = doctorModalArrayList.get(position);
        holder.doctorName.setText(doctorModel.getDoctorName());
        holder.qualification.setText(doctorModel.getQualifications());

        // adding animation to recycler view item on below line.
        setAnimation(holder.itemView, position);
        holder.doctorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorClickInterface.onDoctorClick(holder.getAdapterPosition());
            }
        });
    }
    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            // on below line we are setting animation.
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return doctorModalArrayList.size();
    }






    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our image view and text view on below line.
        private TextView doctorName, qualification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing all our variables on below line.
            doctorName = itemView.findViewById(R.id.idTVCClinicName);
            qualification = itemView.findViewById(R.id.idTVStatus);
        }
    }

    // creating a interface for on click
    public interface doctorClickInterface {
        void onDoctorClick(int position);
    }
}
