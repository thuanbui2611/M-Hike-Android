package com.example.m_hike;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.HolderObservation>{
    private Context context;
    private ArrayList<ObservationModel> observationList;
    MyDbHelper dbHelper;

    public ObservationAdapter(Context context, ArrayList<ObservationModel> observationList) {
        this.context = context;
        this.observationList = observationList;
        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public HolderObservation onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.observation_item, parent, false);
        return new HolderObservation(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderObservation holder, int position) {
        ObservationModel model = observationList.get(position);
        String id = model.getId();
        String name = model.getName();
        String time = model.getTime();
        String comment = model.getComment();
        String image = model.getImage();
        String createdAt = model.getCreatedAt();
        String lastUpdated = model.getLastUpdated();
        String hikeID = model.getHikeID();

        holder.name.setText(name);
        holder.time.setText(time);
        holder.comment.setText(comment);

        if(image.equals("null")){
            holder.image.setImageResource(R.drawable.ic_image_hike);
        } else {
            holder.image.setImageURI(Uri.parse(image));
        }
        //handle click to detail
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ObservationDetailActivity.class);
                intent.putExtra("OBSERVATION_ID", id);
                context.startActivity(intent);
            }
        });
        //handle moreBTN click
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreDialog(id, hikeID, name, comment, time, image, createdAt, lastUpdated);
            }
        });

    }

    private void showMoreDialog(final String id, final String hikeID,String name, String comment, String time, String image,String createdAt, String lastUpdated) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //option: edit
                    Intent intent = new Intent(context, AddUpdateObservationActivity.class);
                    intent.putExtra("ID",id);
                    intent.putExtra("NAME",name);
                    intent.putExtra("TIME",time);
                    intent.putExtra("IMAGE",image);
                    intent.putExtra("COMMENT",comment);
                    intent.putExtra("CREATEDAT",createdAt);
                    intent.putExtra("LASTUPDATED",lastUpdated);
                    intent.putExtra("HIKE_ID",hikeID);
                    intent.putExtra("isEditMode", true);
                    context.startActivity(intent);
                } else if (i==1) {
                    //option: delete
                    dbHelper.deleteObservation(id);
                    Toast.makeText(context, "Delete Observation: " + name + "Successfully!", Toast.LENGTH_SHORT).show();
                    //refresh observations
                    ((HikeDetailActivity)context).onResume();
                }
            }
        });
        builder.create().show();
    }
    @Override
    public int getItemCount() {
        return observationList.size();
    }


    class HolderObservation extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, time, comment;
        ImageButton moreBtn;
        public HolderObservation(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameObservation);
            time = itemView.findViewById(R.id.timeObservation);
            comment = itemView.findViewById(R.id.commentObservation);
            image = itemView.findViewById(R.id.imageObservation);
            moreBtn = itemView.findViewById(R.id.obs_moreBtn);
        }
    }
}
