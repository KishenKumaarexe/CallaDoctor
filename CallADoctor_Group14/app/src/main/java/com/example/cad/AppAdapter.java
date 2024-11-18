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
import java.util.Objects;

public class AppAdapter  extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private ArrayList<AppModel> AppModalArrayList;
    private Context context;
    private AppAdapter.appClickInterface appClickInterface;
    int lastPos = -1;

    // creating a constructor.
    public AppAdapter(ArrayList<AppModel> AppModalArrayList, Context context, AppAdapter.appClickInterface appClickInterface) {
        this.AppModalArrayList = AppModalArrayList;
        this.context = context;
        this.appClickInterface = appClickInterface;
    }

    @NonNull
    @Override
    public AppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new AppAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppAdapter.ViewHolder holder, int position) {
        // setting data to our recycler view item on below line.
        AppModel appModel = AppModalArrayList.get(position);
        holder.patientName.setText(appModel.getPatientName());
        if(Objects.equals(appModel.getStatus(), "approve"))
        {
            holder.status.setText("Approved");
        }
        else if (Objects.equals(appModel.getStatus(), "decline"))
        {
            holder.status.setText("Declined");
        }
        else
        {
            holder.status.setText("Pending");
        }


        // adding animation to recycler view item on below line.
        setAnimation(holder.itemView, position);
        holder.patientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appClickInterface.onAppClick(holder.getAdapterPosition());
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
        return AppModalArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variable for our image view and text view on below line.
        private TextView patientName, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing all our variables on below line.
            patientName = itemView.findViewById(R.id.idTVCClinicName);
            status = itemView.findViewById(R.id.idTVStatus);
        }
    }

    // creating a interface for on click
    public interface appClickInterface {
        void onAppClick(int position);
    }
}