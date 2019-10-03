package com.example.smsscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATBASE_NAME = "scheduledSMS.db";
    public static final String TABLE_NAME = "scheduledSMS";
    public static final String COL0 = "ID";
    public static final String COL1 = "PHONE_NUMBER";
    public static final String COL2 = "TIME";
    public static final String COL3 = "MESSAGE";
    public static final String COL4 = "STATE";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATBASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");

        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " TEXT PRIMARY KEY , " +
                COL1 + " TEXT NOT NULL, " +
                COL2 + " INTEGER NOT NULL, " +
                COL3 + " TEXT NOT NULL, " +
                COL4 + " INTEGER NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addRow(String id, String phone_number, long time, String message, int state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL0, id);
        contentValues.put(COL1, phone_number);
        contentValues.put(COL2, time);
        contentValues.put(COL3, message);
        contentValues.put(COL4, state);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return (result != -1);
    }

    public boolean addSMS(String id, String phone_number, long time, String message, ScheduledSMSState state) {
        return addRow(id, phone_number, time, message, state.ordinal());
    }

    public boolean updateState(String id, ScheduledSMSState state) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL4, state.ordinal());

        long result = db.update(TABLE_NAME, cv, COL0 + "=\"" + id + "\"", null);

        return (result != -1);
    }

    public Cursor getScheduledSMS() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL2 + " DESC", null);

        return data;
    }
}
