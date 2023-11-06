package com.example.m_hike;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    public MyDbHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create table Hike on the db
        sqLiteDatabase.execSQL(Constants.CREATE_TABLE_QUERY);
        //Create table Observation
        sqLiteDatabase.execSQL(ObservationConstants.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //upgrade db (if there is any structure change the change db version)
        //drop older table if exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        Log.w(this.getClass().getName(), Constants.TABLE_NAME + " database upgrade to version " + newVersion + " - old data lost");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ObservationConstants.TABLE_NAME);
        Log.w(this.getClass().getName(), ObservationConstants.TABLE_NAME + " database upgrade to version " + newVersion + " - old data lost");
        //create table again
        onCreate(sqLiteDatabase);
    }


    public long insertHike(String name, String location, String date, String length, String description,
                           String parkingAvailable, String level, String image, String createdAt, String lastUpdated){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();

        //insert data
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_DATE, date);
        values.put(Constants.C_LEVEL, level);
        values.put(Constants.C_DESCRIPTION, description);
        values.put(Constants.C_LENGTH, length);
        values.put(Constants.C_LOCATION, location);
        values.put(Constants.C_PARKING_AVAILABLE, parkingAvailable);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_CREATED_AT, createdAt);
        values.put(Constants.C_LAST_UPDATED, lastUpdated);

        return database.insertOrThrow(Constants.TABLE_NAME, null, values);
    }

    public void updateHike(String id,String name, String location, String date, String length, String description,
                           String parkingAvailable, String level, String image, String createdAt, String lastUpdated){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();

        //insert data
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_DATE, date);
        values.put(Constants.C_LEVEL, level);
        values.put(Constants.C_DESCRIPTION, description);
        values.put(Constants.C_LENGTH, length);
        values.put(Constants.C_LOCATION, location);
        values.put(Constants.C_PARKING_AVAILABLE, parkingAvailable);
        values.put(Constants.C_IMAGE, image);
        values.put(Constants.C_CREATED_AT, createdAt);
        values.put(Constants.C_LAST_UPDATED, lastUpdated);

        database.update(Constants.TABLE_NAME, values, Constants.C_ID +" = ?",new String[]{id});
        database.close();
    }

    public ArrayList<HikeModel> getAllHikes(String orderBy)
    {
        database = getReadableDatabase();
        ArrayList<HikeModel> hikeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + orderBy;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(Constants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(Constants.C_NAME);
                @SuppressLint("Range") int locationColumnIndex = cursor.getColumnIndex(Constants.C_LOCATION);
                @SuppressLint("Range") int parkingColumnIndex = cursor.getColumnIndex(Constants.C_PARKING_AVAILABLE);
                @SuppressLint("Range") int dateColumnIndex = cursor.getColumnIndex(Constants.C_DATE);
                @SuppressLint("Range") int lengthColumnIndex = cursor.getColumnIndex(Constants.C_LENGTH);
                @SuppressLint("Range") int levelColumnIndex = cursor.getColumnIndex(Constants.C_LEVEL);
                @SuppressLint("Range") int desColumnIndex = cursor.getColumnIndex(Constants.C_DESCRIPTION);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(Constants.C_IMAGE);
                @SuppressLint("Range") int createdAtColumnIndex = cursor.getColumnIndex(Constants.C_CREATED_AT);
                @SuppressLint("Range") int lastUpdatedColumnIndex = cursor.getColumnIndex(Constants.C_LAST_UPDATED);
                HikeModel hikeModel = new HikeModel(
                        ""+cursor.getInt(idColumnIndex),
                        ""+cursor.getString(nameColumnIndex),
                        ""+cursor.getString(locationColumnIndex),
                        ""+cursor.getString(parkingColumnIndex),
                        ""+cursor.getString(dateColumnIndex),
                        ""+cursor.getString(lengthColumnIndex),
                        ""+cursor.getString(levelColumnIndex),
                        ""+cursor.getString(desColumnIndex),
                        ""+cursor.getString(imageColumnIndex),
                        ""+cursor.getString(createdAtColumnIndex),
                        ""+cursor.getString(lastUpdatedColumnIndex));
                hikeList.add(hikeModel);
            } while (cursor.moveToNext());
        }
        database.close();
        return hikeList;
    }
    public ArrayList<HikeModel> searchHike(String query)
    {
        database = getReadableDatabase();
        ArrayList<HikeModel> hikeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " +
                Constants.C_NAME + " LIKE '%" + query +"%' OR " +
                Constants.C_DATE + " LIKE '%" + query +"%' OR " +
                Constants.C_LENGTH + " LIKE '%" + query +"%' OR " +
                Constants.C_LEVEL + " LIKE '%" + query +"%' OR " +
                Constants.C_LOCATION + " LIKE '%" + query +"%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(Constants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(Constants.C_NAME);
                @SuppressLint("Range") int locationColumnIndex = cursor.getColumnIndex(Constants.C_LOCATION);
                @SuppressLint("Range") int parkingColumnIndex = cursor.getColumnIndex(Constants.C_PARKING_AVAILABLE);
                @SuppressLint("Range") int dateColumnIndex = cursor.getColumnIndex(Constants.C_DATE);
                @SuppressLint("Range") int lengthColumnIndex = cursor.getColumnIndex(Constants.C_LENGTH);
                @SuppressLint("Range") int levelColumnIndex = cursor.getColumnIndex(Constants.C_LEVEL);
                @SuppressLint("Range") int desColumnIndex = cursor.getColumnIndex(Constants.C_DESCRIPTION);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(Constants.C_IMAGE);
                @SuppressLint("Range") int createdAtColumnIndex = cursor.getColumnIndex(Constants.C_CREATED_AT);
                @SuppressLint("Range") int lastUpdatedColumnIndex = cursor.getColumnIndex(Constants.C_LAST_UPDATED);
                HikeModel hikeModel = new HikeModel(
                        ""+cursor.getInt(idColumnIndex),
                        ""+cursor.getString(nameColumnIndex),
                        ""+cursor.getString(locationColumnIndex),
                        ""+cursor.getString(parkingColumnIndex),
                        ""+cursor.getString(dateColumnIndex),
                        ""+cursor.getString(lengthColumnIndex),
                        ""+cursor.getString(levelColumnIndex),
                        ""+cursor.getString(desColumnIndex),
                        ""+cursor.getString(imageColumnIndex),
                        ""+cursor.getString(createdAtColumnIndex),
                        ""+cursor.getString(lastUpdatedColumnIndex));
                hikeList.add(hikeModel);
            } while (cursor.moveToNext());
        }
        database.close();
        return hikeList;
    }

    public int getHikesCount(){
        database = getReadableDatabase();
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void deleteHike(String id){
        database = getWritableDatabase();
        //Delete all relate observation of the hike
        database.delete(ObservationConstants.TABLE_NAME, ObservationConstants.C_HIKE_ID + " = ?", new String[]{id});
        //Delete hike
        database.delete(Constants.TABLE_NAME, Constants.C_ID + " = ?", new String[]{id});
        database.close();
    }

    public void deleteAllHikes(){
        database = getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.TABLE_NAME);
        database.close();
    }

    //Observation
    public ArrayList<ObservationModel> getObservationByHikeID(String hikeID)
    {
        database = getReadableDatabase();
        ArrayList<ObservationModel> observationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ObservationConstants.TABLE_NAME + " WHERE " +
                ObservationConstants.C_HIKE_ID + " = '" + hikeID + "'"
                + " ORDER BY " + ObservationConstants.C_CREATED_AT + " DESC";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(ObservationConstants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(ObservationConstants.C_NAME);
                @SuppressLint("Range") int timeColumnIndex = cursor.getColumnIndex(ObservationConstants.C_TIME);
                @SuppressLint("Range") int commentColumnIndex = cursor.getColumnIndex(ObservationConstants.C_COMMENT);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(ObservationConstants.C_IMAGE);
                @SuppressLint("Range") int createdAtColumnIndex = cursor.getColumnIndex(ObservationConstants.C_CREATED_AT);
                @SuppressLint("Range") int lastUpdatedColumnIndex = cursor.getColumnIndex(ObservationConstants.C_LAST_UPDATED);
                @SuppressLint("Range") int hikeIDColumnIndex = cursor.getColumnIndex(ObservationConstants.C_HIKE_ID);
                ObservationModel obsModel = new ObservationModel(
                        ""+cursor.getInt(idColumnIndex),
                        ""+cursor.getString(nameColumnIndex),
                        ""+cursor.getString(timeColumnIndex),
                        ""+cursor.getString(commentColumnIndex),
                        ""+cursor.getString(imageColumnIndex),
                        ""+cursor.getString(createdAtColumnIndex),
                        ""+cursor.getString(lastUpdatedColumnIndex),
                        ""+cursor.getString(hikeIDColumnIndex));
                observationList.add(obsModel);
            } while (cursor.moveToNext());
        }
        database.close();
        return observationList;
    }
    public long insertObservation(String hikeID, String name, String time, String comment, String image, String createdAt, String lastUpdated){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ObservationConstants.C_HIKE_ID, hikeID);
        values.put(ObservationConstants.C_NAME, name);
        values.put(ObservationConstants.C_TIME, time);
        values.put(ObservationConstants.C_COMMENT, comment);
        values.put(ObservationConstants.C_IMAGE, image);
        values.put(ObservationConstants.C_CREATED_AT, createdAt);
        values.put(ObservationConstants.C_LAST_UPDATED, lastUpdated);

        return database.insertOrThrow(ObservationConstants.TABLE_NAME, null, values);
    }
    public void updateObservation(String id, String hikeID,String name, String time, String comment, String image, String createdAt, String lastUpdated){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ObservationConstants.C_HIKE_ID, hikeID);
        values.put(ObservationConstants.C_NAME, name);
        values.put(ObservationConstants.C_TIME, time);
        values.put(ObservationConstants.C_COMMENT, comment);
        values.put(ObservationConstants.C_IMAGE, image);
        values.put(ObservationConstants.C_CREATED_AT, createdAt);
        values.put(ObservationConstants.C_LAST_UPDATED, lastUpdated);

        database.update(ObservationConstants.TABLE_NAME, values, Constants.C_ID +" = ?",new String[]{id});
        database.close();
    }
    public void deleteObservation(String id){
        database = getWritableDatabase();
        database.delete(ObservationConstants.TABLE_NAME, ObservationConstants.C_ID + " = ?", new String[]{id});
        database.close();
    }
    public void deleteAllTables(){
        // Get a list of all tables in the database
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        // Iterate over the cursor to drop each table
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                // Exclude system tables like "android_metadata" and "sqlite_sequence"
                if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")) {
                    database.execSQL("DROP TABLE IF EXISTS " + tableName);
                }
                cursor.moveToNext();
            }
        }
        // Close the cursor
        cursor.close();
    }

}
