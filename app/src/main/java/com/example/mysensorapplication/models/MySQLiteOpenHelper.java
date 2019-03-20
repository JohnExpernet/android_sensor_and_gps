package com.example.mysensorapplication.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "human";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "mysensorapplication";
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long write(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        return db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<String> read(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = { "id", "name" };
        String selection = "name = ?";
        String[] selectionArgs = { name };
        String sortOrder = "name DESC";

        Cursor cursor = db.query(
            TABLE_NAME, // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection, // The columns for the WHERE clause
            selectionArgs, // The values for the WHERE clause
            null, // don't group the rows
            null, // don't filter by row groups
            sortOrder // The sort order
        );

        ArrayList<String> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            items.add(itemName);
        }
        cursor.close();

        return items;
    }
}
