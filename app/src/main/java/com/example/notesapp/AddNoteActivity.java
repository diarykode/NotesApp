package com.example.notesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etContent;
    private Button btnDelete;
    private Button btnSave;

    private Database db;
    private Intent dataIntent;
    private boolean isEdit = false;
    private int noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Note");

        db = new Database(this);
        dataIntent = getIntent();
        isEdit = dataIntent.getBooleanExtra("isEdit", false);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnDelete = findViewById(R.id.btn_delete);
        btnSave = findViewById(R.id.btn_save);

        if (isEdit) {
            int id = dataIntent.getIntExtra("noteID", 0);
            if (id != 0) {
                NoteModel noteModel = db.getNote(id);
                noteID = id;
                etTitle.setText(noteModel.getNoteTitle());
                etContent.setText(noteModel.getNoteContent());
            }

            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String note = etContent.getText().toString();
                if (isInputValid()) {
                    if (isEdit) {
                        updateNote(noteID, title, note);
                    } else {
                        saveNote(title, note);
                    }
                }
            }
        });
    }

    private boolean isInputValid() {
        if (TextUtils.isEmpty(etTitle.getText()) || TextUtils.isEmpty(etContent.getText())) {
            if (TextUtils.isEmpty(etTitle.getText())) {
                etTitle.setError("Judul tidak boleh kosong!");
            }

            if (TextUtils.isEmpty(etContent.getText())) {
                etContent.setError("Note tidak boleh kosong!");
            }

            return false;
        }

        return true;
    }

    private void saveNote(String title, String content) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm");

        String date = sdf.format(calendar.getTime());

        NoteModel noteModel = new NoteModel(title, content, date);
        int success = db.addNote(noteModel);

        String message = "Note gagal disimpan";

        if (success != 0) {
            message = "Note berhasil disimpan";
            finish();
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateNote(int noteID, String title, String note) {
        NoteModel noteModel = new NoteModel(noteID, title, note);
        int success = db.updateNote(noteModel);

        String message = "Note gagal di update";

        if (success != 0) {
            message = "Note berhasil di update";
            finish();
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteNote() {
        int success = db.deleteNote(noteID);
        String message = "Note gagal di hapus";
        if (success != 0) {
            message = "Note berhasil di hapus";
            finish();
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
