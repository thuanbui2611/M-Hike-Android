package com.example.m_hike;

public class ObservationConstants {
    public static final String DB_NAME = "M_HIKE_DB";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "OBSERVATION_TABLE";
    public static final String C_ID = "ID";
    public static final String C_NAME = "NAME";
    public static final String C_TIME = "TIME";
    public static final String C_COMMENT = "COMMENT";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_CREATED_AT = "CREATED_AT";
    public static final String C_LAST_UPDATED = "LAST_UPDATED";
    public static final String C_HIKE_ID = "HIKE_ID";
    public static final String CREATE_TABLE_QUERY = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT)",
            TABLE_NAME,C_ID,C_NAME,C_TIME,C_COMMENT,C_IMAGE,C_CREATED_AT,C_LAST_UPDATED, C_HIKE_ID
    );
}
