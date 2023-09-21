package com.example.m_hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDbHelper extends SQLiteOpenHelper {

    public MyDbHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create table Hike on the db
        sqLiteDatabase.execSQL(Constants.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //upgrade db (if there is any structure change the change db version)
        //drop older table if exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        //create table again
        onCreate(sqLiteDatabase);
    }

    //insert hike to db
    public long insertHike(String name, String location, String date, double length, String description,
                           int parkingAvailable, String level, String image){
        //get writeable database to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //insert data
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_LOCATION, location);
        values.put(Constants.C_DATE, date);
        values.put(Constants.C_PARKING_AVAILABLE, parkingAvailable);
        values.put(Constants.C_LENGTH, length);
        values.put(Constants.C_DESCRIPTION, description);
        values.put(Constants.C_LEVEL, level);
        values.put(Constants.C_IMAGE, image);

        //insert row, it will return hike id of saved hike
        long id = db.insert(Constants.TABLE_NAME, null, values);

        //close db connection
        db.close();

        //return id of inserted hike
        return id;
    }
}
