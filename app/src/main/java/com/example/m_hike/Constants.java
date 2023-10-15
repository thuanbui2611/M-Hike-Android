package com.example.m_hike;

public class Constants {
    public static final String DB_NAME = "M_HIKE_DB";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "HIKE_TABLE";
    public static final String C_ID = "ID";
    public static final String C_NAME = "NAME";
    public static final String C_LOCATION = "LOCATION";
    public static final String C_DATE = "DATE";
    public static final String C_LENGTH = "LENGTH";
    public static final String C_DESCRIPTION = "DESCRIPTION";
    public static final String C_PARKING_AVAILABLE = "PARKING_AVAILABLE";
    public static final String C_LEVEL = "LEVEL";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_CREATED_AT = "CREATED_AT";
    public static final String C_LAST_UPDATED = "LAST_UPDATED";
    public static final String CREATE_TABLE_QUERY = String.format(
      "CREATE TABLE %s (" +
      "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT)",
        TABLE_NAME,C_ID,C_NAME,C_DATE,C_LEVEL,C_DESCRIPTION,C_LENGTH,C_LOCATION,
            C_PARKING_AVAILABLE,C_IMAGE,C_CREATED_AT,C_LAST_UPDATED
    );
}
