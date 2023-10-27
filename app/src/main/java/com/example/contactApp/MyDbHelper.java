package com.example.contactApp;

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
        sqLiteDatabase.execSQL(Constants.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        Log.w(this.getClass().getName(), Constants.TABLE_NAME + " database upgrade to version " + newVersion + " - old data lost");
        onCreate(sqLiteDatabase);
    }

    public ArrayList<ContactModel> searchContact(String query)
    {
        database = getReadableDatabase();
        ArrayList<ContactModel> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " +
                Constants.C_NAME + " LIKE '%" + query +"%' OR " +
                Constants.C_DOB + " LIKE '%" + query +"%' OR " +
                Constants.C_EMAIL + " LIKE '%" + query +"%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(Constants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(Constants.C_NAME);
                @SuppressLint("Range") int emailColumnIndex = cursor.getColumnIndex(Constants.C_EMAIL);
                @SuppressLint("Range") int dobColumnIndex = cursor.getColumnIndex(Constants.C_DOB);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(Constants.C_IMAGE);
                ContactModel contactModel = new ContactModel(
                        ""+cursor.getInt(idColumnIndex),
                        ""+cursor.getString(nameColumnIndex),
                        ""+cursor.getString(imageColumnIndex),
                        ""+cursor.getString(dobColumnIndex),
                        ""+cursor.getString(emailColumnIndex));
                contactList.add(contactModel);
            } while (cursor.moveToNext());
        }
        database.close();
        return contactList;
    }

    public long addContact(String name, String image, String dob, String email){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_DOB, dob);
        values.put(Constants.C_EMAIL, email);
        values.put(Constants.C_IMAGE, image);

        return database.insertOrThrow(Constants.TABLE_NAME, null, values);
    }

    public void updateContact(String id,String name, String image, String dob, String email){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.C_NAME, name);
        values.put(Constants.C_DOB, dob);
        values.put(Constants.C_EMAIL, email);
        values.put(Constants.C_IMAGE, image);

        database.update(Constants.TABLE_NAME, values, Constants.C_ID +" = ?",new String[]{id});
        database.close();
    }

    public ArrayList<ContactModel> getAllContacts()
    {
        database = getReadableDatabase();
        ArrayList<ContactModel> contactList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + Constants.C_NAME + " ASC";;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") int idColumnIndex = cursor.getColumnIndex(Constants.C_ID);
                @SuppressLint("Range") int nameColumnIndex = cursor.getColumnIndex(Constants.C_NAME);
                @SuppressLint("Range") int emailColumnIndex = cursor.getColumnIndex(Constants.C_EMAIL);
                @SuppressLint("Range") int dobColumnIndex = cursor.getColumnIndex(Constants.C_DOB);
                @SuppressLint("Range") int imageColumnIndex = cursor.getColumnIndex(Constants.C_IMAGE);
                ContactModel contactModel = new ContactModel(
                        ""+cursor.getInt(idColumnIndex),
                        ""+cursor.getString(nameColumnIndex),
                        ""+cursor.getString(imageColumnIndex),
                        ""+cursor.getString(dobColumnIndex),
                        ""+cursor.getString(emailColumnIndex));
                contactList.add(contactModel);
            } while (cursor.moveToNext());
        }
        database.close();
        return contactList;
    }

    public void deleteContact(String id){
        database = getWritableDatabase();
        database.delete(Constants.TABLE_NAME, Constants.C_ID + " = ?", new String[]{id});
        database.close();
    }

    public void deleteAllContacts(){
        database = getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.TABLE_NAME);
        database.close();
    }
}
