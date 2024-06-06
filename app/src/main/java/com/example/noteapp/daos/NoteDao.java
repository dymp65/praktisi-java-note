package com.example.noteapp.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import com.example.noteapp.helpers.DatabaseHelper;
import com.example.noteapp.models.Note;

public class NoteDao {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public NoteDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void insert(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        database.insert("notes", null, values);
    }

    public void update(Note note) {
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("content", note.getContent());
        database.update("notes", values, "id = ?", new String[]{String.valueOf(note.getId())});
    }

    public void delete(Note note) {
        database.delete("notes", "id = ?", new String[]{String.valueOf(note.getId())});
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM notes", null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(0));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return notes;
    }
}

