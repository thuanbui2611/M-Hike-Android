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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HolderHike>{
    private Context context;
    private ArrayList<HikeModel> hikeList;

    MyDbHelper dbHelper;

    public HikeAdapter(Context context, ArrayList<HikeModel> hikeList) {
        this.context = context;
        this.hikeList = hikeList;

        dbHelper = new MyDbHelper(context);
    }

    @NonNull
    @Override
    public HolderHike onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.hike_item, parent, false);

        return new HolderHike(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderHike holder, int position) {
        //get data
        HikeModel model = hikeList.get(position);
        String id = model.getId();
        String hikeName = model.getName();
        String image = model.getImage();
        String location = model.getLocation();
        String date = model.getDate();
        String length = model.getLength();
        String level = model.getLevel();
        String description = model.getDescription();
        String parking = model.getParking();
        String createdAt = model.getCreatedAt();
        String lastUpdated = model.getLastUpdated();
        //set data
        holder.hikeName.setText(hikeName);
        holder.location.setText(location);
        holder.date.setText(date);
        holder.level.setText(level);


        if(level.trim().toLowerCase().equals("easy"))
        {
            holder.level.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (level.trim().toLowerCase().equals("medium")) {
            holder.level.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        } else if (level.trim().toLowerCase().equals("hard")) {
            holder.level.setTextColor(ContextCompat.getColor(context, R.color.red));
        };
        holder.level.setText(level);

        if(image.equals("null")){
            holder.image.setImageResource(R.drawable.ic_image_hike);
        } else {
            holder.image.setImageURI(Uri.parse(image));
        }
        //handle item clicks (go to detail activity)
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Pass hike id to next activity to show detail
                Intent intent = new Intent(context, HikeDetailActivity.class);
                intent.putExtra("HIKE_ID", id);
                context.startActivity(intent);
            }
        });
        //handle more_button click
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreDiaglog(id, hikeName, image, location, date, length, level, description, parking, createdAt, lastUpdated);
            }
        });
    }

    private void showMoreDiaglog(final String id, String hikeName, String image, String location, String date, String length, String level, String description, String parking, String createdAt, String lastUpdated) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //option: edit
                    Intent intent = new Intent(context, AddUpdateHikesActivity.class);
                    intent.putExtra("ID",id);
                    intent.putExtra("HIKENAME",hikeName);
                    intent.putExtra("IMAGE",image);
                    intent.putExtra("LOCATION",location);
                    intent.putExtra("DATE",date);
                    intent.putExtra("LENGTH",length);
                    intent.putExtra("LEVEL",level);
                    intent.putExtra("DESCRIPTION",description);
                    intent.putExtra("PARKING",parking);
                    intent.putExtra("CREATEDAT",createdAt);
                    intent.putExtra("LASTUPDATED",lastUpdated);
                    intent.putExtra("isEditMode",true);
                    context.startActivity(intent);
                }
                else if(i==1){
                    //option: delete
                    dbHelper.deleteHike(id);
                    Toast.makeText(context, "Delete hike: " + hikeName + "Successfully!", Toast.LENGTH_SHORT).show();
                    //refresh hikes
                    //((MainActivity)context).onResume();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    class HolderHike extends RecyclerView.ViewHolder{
        ImageView image;
        TextView hikeName, location, date, level;
        ImageButton moreBtn;

        public HolderHike(@NonNull View itemView) {
            super(itemView);

            hikeName = itemView.findViewById(R.id.nameHike);
            location = itemView.findViewById(R.id.locationHike);
            date = itemView.findViewById(R.id.dateHike);
            level = itemView.findViewById(R.id.levelHike);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            image = itemView.findViewById(R.id.imageHike);
        }
    }
}
