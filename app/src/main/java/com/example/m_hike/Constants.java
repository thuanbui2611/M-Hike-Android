package com.example.m_hike;

public class Constants {
    public static final String DB_NAME = "M_HIKE_DB";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "HIKE_TABLE";
    public static final String C_ID = "ID";
    public static final String C_NAME = "NAME";
    public static final String C_DOB = "DOB";
    public static final String C_EMAIL = "EMAIL";
    public static final String C_IMAGE = "IMAGE";

    public static final String CREATE_TABLE_QUERY = String.format(
      "CREATE TABLE %s (" +
      "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT, " +
      "%s TEXT)",
        TABLE_NAME,C_ID,C_NAME, C_DOB, C_EMAIL, C_IMAGE
    );
}
