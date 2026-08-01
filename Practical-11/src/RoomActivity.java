package com.gpb.dataapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoomActivity extends AppCompatActivity {
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        db = AppDatabase.getDatabase(this);

        EditText etTitle = findViewById(R.id.etTitle);
        EditText etContent = findViewById(R.id.etContent);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnLoad = findViewById(R.id.btnLoad);

        btnAdd.setOnClickListener(v -> executor.execute(() -> {
            db.noteDao().insert(new Note(etTitle.getText().toString(), etContent.getText().toString()));
            runOnUiThread(() -> {
                etTitle.getText().clear();
                etContent.getText().clear();
            });
        }));

        btnLoad.setOnClickListener(v -> executor.execute(() -> {
            List<Note> notes = db.noteDao().getAllNotes();
            for (Note note : notes) {
                System.out.println(note.id + ": " + note.title + " - " + note.content);
            }
            // Bind `notes` to a RecyclerView (see Practical 09) to display in UI
        }));
    }
}
