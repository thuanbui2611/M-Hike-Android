package com.example.contactApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactDetailActivity extends AppCompatActivity {

    private ImageView image;
    private TextView contactName, dob, email;
    private String contactID;
    private ActionBar actionBar;
    private MyDbHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        //setup action bar with title and back button
        actionBar = getSupportActionBar();
        actionBar.setTitle("Contact Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //get contact id from adapter
        Intent intent = getIntent();
        contactID = intent.getStringExtra("CONTACT_ID");
        //init
        dbHelper = new MyDbHelper(this);

        image = findViewById(R.id.contactImageDT);
        email = findViewById(R.id.emailDT);
        dob = findViewById(R.id.dobDT);
        contactName = findViewById(R.id.nameContactDT);
        showContactDetails();
    }

    private void showContactDetails() {
        //query to select record by id
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.C_ID +" =\"" + contactID+"\"";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(Constants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(Constants.C_NAME);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(Constants.C_IMAGE);
                @SuppressLint("Range") int emailColumnIndex = cursor.getColumnIndex(Constants.C_EMAIL);
                @SuppressLint("Range") int dobColumnIndex = cursor.getColumnIndex(Constants.C_DOB);
                String id = ""+cursor.getInt(idColumnIndex);
                String name = ""+cursor.getString(nameColumnIndex);
                String dobGet = ""+cursor.getString(dobColumnIndex);
                String emailGet = ""+cursor.getString(emailColumnIndex);
                String imageContact = ""+cursor.getString(imageColumnIndex);

                contactName.setText(name);
                dob.setText(dobGet);
                email.setText(emailGet);

                if(image.equals("null")){
                    image.setImageResource(R.drawable.ic_image_contact);
                } else {
                    image.setImageURI(Uri.parse(imageContact));
                }
            } while (cursor.moveToNext());
        }
        db.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); // go to prev activity
        return super.onSupportNavigateUp();
    }
}