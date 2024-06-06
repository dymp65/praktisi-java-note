package com.example.noteapp.repositoies;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import android.os.AsyncTask;
import com.example.noteapp.daos.NoteDao;
import com.example.noteapp.models.Note;

public class NoteRepository {
    private NoteDao noteDao;
    private MutableLiveData<List<Note>> allNotes;

    public NoteRepository(Context context) {
        noteDao = new NoteDao(context);
        allNotes = new MutableLiveData<>();
        loadAllNotes();
    }

    public void insert(Note note) {
        new InsertNoteAsyncTask(noteDao, this).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(noteDao, this).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(noteDao, this).execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private void loadAllNotes() {
        new LoadAllNotesAsyncTask(noteDao, allNotes).execute();
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private NoteRepository repository;

        private InsertNoteAsyncTask(NoteDao noteDao, NoteRepository repository) {
            this.noteDao = noteDao;
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            repository.loadAllNotes();
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private NoteRepository repository;

        private UpdateNoteAsyncTask(NoteDao noteDao, NoteRepository repository) {
            this.noteDao = noteDao;
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            repository.loadAllNotes();
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private NoteRepository repository;

        private DeleteNoteAsyncTask(NoteDao noteDao, NoteRepository repository) {
            this.noteDao = noteDao;
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            repository.loadAllNotes();
            return null;
        }
    }

    private static class LoadAllNotesAsyncTask extends AsyncTask<Void, Void, List<Note>> {
        private NoteDao noteDao;
        private MutableLiveData<List<Note>> liveData;

        private LoadAllNotesAsyncTask(NoteDao noteDao, MutableLiveData<List<Note>> liveData) {
            this.noteDao = noteDao;
            this.liveData = liveData;
        }

        @Override
        protected List<Note> doInBackground(Void... voids) {
            return noteDao.getAllNotes();
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            liveData.setValue(notes);
        }
    }
}

