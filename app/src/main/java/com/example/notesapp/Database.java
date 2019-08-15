package com.example.notesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "NotesAppDB";
    private static final String TABLE_NOTES = "tb_notes";

    private static final String KEY_NOTE_ID = "id";
    private static final String KEY_NOTE_TITLE = "title";
    private static final String KEY_NOTE_CONTENT = "content";
    private static final String KEY_NOTE_DATE = "date";

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NOTES + " (" +
                KEY_NOTE_ID + " INTEGER PRIMARY KEY, " +
                KEY_NOTE_TITLE + " TEXT, " +
                KEY_NOTE_CONTENT + " TEXT, " +
                KEY_NOTE_DATE + " TEXT" + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);

        onCreate(db);
    }

    public int addNote(NoteModel noteModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NOTE_TITLE, noteModel.getNoteTitle());
        values.put(KEY_NOTE_CONTENT, noteModel.getNoteContent());
        values.put(KEY_NOTE_DATE, noteModel.getNoteDate());

        long ID = db.insert(TABLE_NOTES, null, values);
        db.close();

        return (int) ID;
    }

    public List<NoteModel> getNotes() {
        List<NoteModel> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                String date = cursor.getString(3);

                NoteModel noteModel = new NoteModel(id, title, desc, date);

                noteList.add(noteModel);
            } while (cursor.moveToNext());
        }

        return noteList;
    }

    public NoteModel getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[] {
                KEY_NOTE_ID,
                KEY_NOTE_TITLE,
                KEY_NOTE_CONTENT,
                KEY_NOTE_DATE
        }, KEY_NOTE_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        int noteID = cursor.getInt(0);
        String title = cursor.getString(1);
        String desc = cursor.getString(2);
        String date = cursor.getString(3);

        NoteModel noteModel = new NoteModel(noteID, title, desc, date);

        return noteModel;
    }

    public int updateNote(NoteModel noteModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NOTE_TITLE, noteModel.getNoteTitle());
        values.put(KEY_NOTE_CONTENT, noteModel.getNoteContent());

        return db.update(TABLE_NOTES, values, KEY_NOTE_ID + "=?",
                new String[]{String.valueOf(noteModel.getNoteID())});
    }

    public int deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ID = db.delete(TABLE_NOTES, KEY_NOTE_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();

        return ID;
    }
}
