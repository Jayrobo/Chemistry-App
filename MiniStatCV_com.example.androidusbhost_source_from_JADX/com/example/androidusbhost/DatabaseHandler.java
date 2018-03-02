package com.example.androidusbhost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Sensor_Data_Table";
    private static final int DATABASE_VERSION = 1;
    private static final String KEY_DATETIME = "Date_time";
    private static final String KEY_ID = "ID";
    private static final String KEY_VALDAC = "Data_value";
    private static final String KEY_VOLT = "Voltage";
    private static final String TABLE_NAME = "Data_Log";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Data_Log(ID INTEGER PRIMARY KEY,Date_time INTEGER,Voltage SMALLINT,Data_value REAL)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Data_Log");
        onCreate(db);
    }

    void addEntry(DataTable entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATETIME, entry.dateTime);
        values.put(KEY_VOLT, entry.volt);
        values.put(KEY_VALDAC, entry.valADC);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public DataTable getEntry(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_DATETIME, KEY_VOLT, KEY_VALDAC}, "ID=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        DataTable entry = new DataTable(Integer.parseInt(cursor.getString(0)), Long.valueOf(Long.parseLong(cursor.getString(1))), Short.valueOf(Short.parseShort(cursor.getString(2))), Double.valueOf(Double.parseDouble(cursor.getString(3))));
        cursor.close();
        db.close();
        return entry;
    }

    public List<DataTable> getAllEntry() {
        List<DataTable> entryList = new ArrayList();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM Data_Log", null);
        if (cursor.moveToFirst()) {
            do {
                DataTable entry = new DataTable();
                entry.id = Integer.parseInt(cursor.getString(0));
                entry.dateTime = Long.valueOf(Long.parseLong(cursor.getString(1)));
                entry.volt = Short.valueOf(Short.parseShort(cursor.getString(2)));
                entry.valADC = Double.valueOf(Double.parseDouble(cursor.getString(3)));
                entryList.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entryList;
    }

    public int updateEntry(DataTable entry) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DATETIME, entry.dateTime);
        values.put(KEY_VOLT, entry.volt);
        values.put(KEY_VALDAC, entry.valADC);
        return db.update(TABLE_NAME, values, "ID = ?", new String[]{String.valueOf(entry.id)});
    }

    public void deleteEntry(DataTable entry) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(entry.id)});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public int getEntryCount() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT  * FROM Data_Log", null);
        cursor.close();
        return cursor.getCount();
    }
}
