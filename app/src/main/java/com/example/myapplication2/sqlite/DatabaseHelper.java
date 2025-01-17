package com.example.myapplication2.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim
 * https://github.com/ravi8x/AndroidSQLite/blob/master/app/src/main/java/info/androidhive/sqlite/database/DatabaseHelper.java
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "mytable";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Note.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertNote(Note note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // no need to add them
        values.put(Note.COLUMN_ZAK, note.getZak());
        values.put(Note.COLUMN_DATE, note.getData());
        values.put(Note.COLUMN_CHAS, note.getChas());

        // insert row
        db.insert(Note.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
    }

    public ArrayList<String> distinct(String colName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> notes = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + colName  + " FROM " + Note.TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                notes.add(cursor.getString(cursor.getColumnIndex(colName)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;

    }

    public ArrayList<Note> where(String colName, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Note> notes = new ArrayList<>();
        String selectQuery = "SELECT * FROM "  + Note.TABLE_NAME +
                " WHERE " + colName + " = '" + value + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setZak(cursor.getString(cursor.getColumnIndex(Note.COLUMN_ZAK)));
                note.setData(cursor.getString(cursor.getColumnIndex(Note.COLUMN_DATE)));
                note.setChas(cursor.getDouble(cursor.getColumnIndex(Note.COLUMN_CHAS)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;

    }

    public Note getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_ZAK, Note.COLUMN_DATE, Note.COLUMN_CHAS},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // prepare note object
        assert cursor != null;
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_ZAK)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_DATE)),
                cursor.getDouble(cursor.getColumnIndex(Note.COLUMN_CHAS))
        );

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setZak(cursor.getString(cursor.getColumnIndex(Note.COLUMN_ZAK)));
                note.setData(cursor.getString(cursor.getColumnIndex(Note.COLUMN_DATE)));
                note.setChas(cursor.getDouble(cursor.getColumnIndex(Note.COLUMN_CHAS)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public void updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_ZAK, note.getZak());
        values.put(Note.COLUMN_DATE, note.getData());
        values.put(Note.COLUMN_CHAS, note.getChas());
        Log.d("DatabaseHelper",
                "id = " + note.getId() +
                        ", Дата = " + note.getData() +
                        ", Заказ_наряд = " + note.getZak() +
                        ", Часы = " + note.getChas()
        );
        // updating row
         db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
//    return db.update(Note.TABLE_NAME, values, null, null);
        db.close();

    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

    public void deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + Note.TABLE_NAME + " ORDER BY " +
                Note.COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Note note = new Note();
//                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));

                db.delete(Note.TABLE_NAME, Note.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)))});
            } while (cursor.moveToNext());
        }
        db.close();
    }
}