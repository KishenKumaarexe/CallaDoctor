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

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {

    private ArrayList<ClinicModel> clinicModalArrayList;
    private Context context;
    private ClinicClickInterface clinicClickInterface;
    int lastPos = -1;

    // creating a constructor.
    public ClinicAdapter(ArrayList<ClinicModel> clinicModalArrayList, Context context, ClinicClickInterface clinicClickInterface) {
        this.clinicModalArrayList = clinicModalArrayList;
        this.context = context;
        this.clinicClickInterface = clinicClickInterface;
    }
    @NonNull
    @Override
    public ClinicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ClinicAdapter.ViewHolder holder, int position) {
        // setting data to our recycler view item on below line.
        ClinicModel clinicModel = clinicModalArrayList.get(position);
        holder.clinicName.setText(clinicModel.getClinicName());
        if(clinicModel.getActive()==true)
        {
            holder.status.setText("Active");

        }
        else
        {
            holder.status.setText("Inactive");
        }
        // adding animation to recycler view item on below line.
        setAnimation(holder.itemView, position);
        holder.clinicName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clinicClickInterface.onClinicClick(holder.getAdapterPosition());
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
        return clinicModalArrayList.size();
    }






    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our image view and text view on below line.
        private TextView clinicName, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing all our variables on below line.
            clinicName = itemView.findViewById(R.id.idTVCClinicName);
            status = itemView.findViewById(R.id.idTVStatus);
        }
    }

    // creating a interface for on click
    public interface ClinicClickInterface {
        void onClinicClick(int position);
    }
}
