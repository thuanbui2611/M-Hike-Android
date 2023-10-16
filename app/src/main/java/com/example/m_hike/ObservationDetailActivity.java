package com.example.m_hike;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class ObservationDetailActivity extends AppCompatActivity {
    private ImageView image;
    private TextView name, time, comment, createdAt, lastUpdated;
    private ActionBar actionBar;
    private String observationID;
    private MyDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_detail);

        //setup action bar title and back button
        actionBar = getSupportActionBar();
        actionBar.setTitle("Observation Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //get observationID from adapter
        Intent intent = getIntent();
        observationID = intent.getStringExtra("OBSERVATION_ID");
        //init
        dbHelper = new MyDbHelper(this);
        image = findViewById(R.id.imageObservationDT);
        name = findViewById(R.id.nameObservationDT);
        time = findViewById(R.id.timeObservationDT);
        comment = findViewById(R.id.commentObservationDT);
        createdAt = findViewById(R.id.createdAtObsDT);
        lastUpdated = findViewById(R.id.lastUpdatedObsDT);
        showObservationDetails();
    }

    private void showObservationDetails() {
        String selectQuery = "SELECT * FROM " + ObservationConstants.TABLE_NAME + " WHERE " + Constants.C_ID +" =\"" + observationID+"\"";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(ObservationConstants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(ObservationConstants.C_NAME);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(ObservationConstants.C_IMAGE);
                @SuppressLint("Range") int commentColumnIndex = cursor.getColumnIndex(ObservationConstants.C_COMMENT);
                @SuppressLint("Range") int createdAtColumnIndex = cursor.getColumnIndex(ObservationConstants.C_CREATED_AT);
                @SuppressLint("Range") int lastUpdatedColumnIndex = cursor.getColumnIndex(ObservationConstants.C_LAST_UPDATED);
                String id = ""+cursor.getInt(idColumnIndex);
                String nameObs = ""+cursor.getString(nameColumnIndex);
                String commentObs = ""+cursor.getString(commentColumnIndex);
                String imageObs = ""+cursor.getString(imageColumnIndex);
                String createdAtObs = ""+cursor.getString(createdAtColumnIndex);
                String lastUpdatedObs = ""+cursor.getString(lastUpdatedColumnIndex);
                //Convert timestamp to datetime
                Calendar c1 = Calendar.getInstance(Locale.getDefault());
                c1.setTimeInMillis(Long.parseLong(createdAtObs));
                String createdAtTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", c1);

                Calendar c2 = Calendar.getInstance(Locale.getDefault());
                c1.setTimeInMillis(Long.parseLong(lastUpdatedObs));
                String lastUpdatedTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", c2);

                name.setText(nameObs);
                comment.setText(commentObs);
                createdAt.setText(createdAtTime);
                lastUpdated.setText(lastUpdatedTime);

                if(image.equals("null")){
                    image.setImageResource(R.drawable.ic_image_hike);
                } else {
                    image.setImageURI(Uri.parse(imageObs));
                }

            } while (cursor.moveToNext());
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); // go to prev activity
        return super.onSupportNavigateUp();
    }
}