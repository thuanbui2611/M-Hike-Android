package com.example.m_hike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btn_start = findViewById(R.id.btn_start);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        welcomeTextView.startAnimation(fadeInAnimation);
        btn_start.startAnimation(fadeInAnimation);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}