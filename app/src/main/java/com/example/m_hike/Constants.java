package com.example.m_hike;

public class Constants {
    public static final String DB_NAME = "M_HIKE_DB";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "HIKE_TABLE";
    //Columns/fields of table
    public static final String C_ID = "ID";
    public static final String C_NAME = "NAME";
    public static final String C_LOCATION = "LOCATION";
    public static final String C_DATE = "DATE";
    public static final String C_LENGTH = "LENGTH";
    public static final String C_DESCRIPTION = "DESCRIPTION";
    public static final String C_PARKING_AVAILABLE = "PARKING_AVAILABLE";
    public static final String C_LEVEL = "LEVEL";
    public static final String C_IMAGE = "IMAGE";

    //Create table query
    public static final String CREATE_TABLE = "CREATE TABLE" + TABLE_NAME + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_NAME + "TEXT,"
            + C_DATE + "TEXT,"
            + C_LEVEL + "TEXT,"
            + C_DESCRIPTION + "TEXT,"
            + C_LENGTH + "NUMERIC,"
            + C_LOCATION + "TEXT,"
            + C_PARKING_AVAILABLE + "INTEGER,"
            + C_IMAGE + "TEXT,"
            + ")";

}
