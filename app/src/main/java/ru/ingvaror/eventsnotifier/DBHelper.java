package ru.ingvaror.eventsnotifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    // Database name and version
    private static final String DATABASE_NAME = "EventsNotifier";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names
    private static final String TABLE_NAME = "watchers";
    private static final String COLUMN_URL = "URL";

    // Create table query
    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_URL + " TEXT PRIMARY KEY);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to insert data into the database
    public long insertData(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_URL, url);

        // Insert the data
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Method to retrieve all data from the database
    public List<String> getAllURLs() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> URLs = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                URLs.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return URLs;
    }
}
