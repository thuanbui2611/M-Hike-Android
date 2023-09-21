package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //views
    private FloatingActionButton addHikeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init views
        addHikeBtn = findViewById(R.id.addHikeBtn);

        //click to start add hike activity
        addHikeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, AddUpdateHikesActivity.class));
            }
        });
    }
}