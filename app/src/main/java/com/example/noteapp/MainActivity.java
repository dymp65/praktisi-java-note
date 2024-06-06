package com.example.noteapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import java.util.List;
import android.widget.LinearLayout;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.example.noteapp.models.Note;
import com.example.noteapp.viewmodel.NoteViewModel;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private EditText titleEditText, contentEditText, idEditText;
    private Button createButton, readButton, updateButton, deleteButton;
    private LinearLayout notesContainer;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        idEditText = findViewById(R.id.idEditText);
        createButton = findViewById(R.id.createButton);
        notesContainer = findViewById(R.id.notesContainer);

        idEditText.setVisibility(View.GONE);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                Note note = new Note();
                note.setTitle(title);
                note.setContent(content);
                noteViewModel.insert(note);
                Toast.makeText(MainActivity.this, "Note created", Toast.LENGTH_SHORT).show();
                loadNotes();
            }
        });

        // Initial load of notes
        loadNotes();

    }

    private void loadNotes() {
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesContainer.removeAllViews();
                for (Note note : notes) {

                    View noteView = LayoutInflater.from(MainActivity.this).inflate(R.layout.note_item, notesContainer, false);
                    TextView noteTitleTextView = noteView.findViewById(R.id.titleTextView);
                    TextView noteContentTextView = noteView.findViewById(R.id.contentTextView);
                    noteTitleTextView.setText(note.getTitle());
                    noteContentTextView.setText(note.getContent());

                    noteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showOptionsDialog(note);
                        }
                    });

                    notesContainer.addView(noteView);
                }
            }
        });
    }

    private void showOptionsDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showEditDialog(note);
                        } else if (which == 1) {
                            showDeleteDialog(note);
                        }
                    }
                })
                .create()
                .show();
    }

    private void showEditDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_note, null);
        builder.setView(dialogView);

        final EditText editTitleEditText = dialogView.findViewById(R.id.editTitleEditText);
        final EditText editContentEditText = dialogView.findViewById(R.id.editContentEditText);

        editTitleEditText.setText(note.getTitle());
        editContentEditText.setText(note.getContent());

        builder.setTitle("Edit Note")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note.setTitle(editTitleEditText.getText().toString());
                        note.setContent(editContentEditText.getText().toString());
                        noteViewModel.update(note);
                        Toast.makeText(MainActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                        loadNotes(); // Refresh the notes after updating
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showDeleteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteViewModel.delete(note);
                        Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        loadNotes(); // Refresh the notes after deletion
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}