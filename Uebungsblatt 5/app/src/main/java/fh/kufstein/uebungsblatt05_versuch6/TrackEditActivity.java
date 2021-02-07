package fh.kufstein.uebungsblatt05_versuch6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TrackEditActivity extends AppCompatActivity {
        TrackDao dao;
        Track track;
        EditText title;
        EditText  description;
        FloatingActionButton save;
        long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_edit);

        Intent intent = getIntent();
        if (intent.hasExtra("track_id")) {
            id = intent.getLongExtra("track_id", 0);
            dao = AppDatabase.getInstance(getApplicationContext()).getTrackDao();

            defineParameters(id);

            setDefaultValuesInEditTextFields();

            save.setOnClickListener((View v) -> {
                updateCurrentTrack(dao, track, title, description);
                goBack();
            });
        }
    }

    private void setDefaultValuesInEditTextFields() {
        title.setText(track.title);
        description.setText(track.description);
    }

    private void defineParameters(long id) {
        track = dao.getTrack(id);
        title = findViewById(R.id.edit_track_title);
        description = findViewById(R.id.edit_track_description);
        save = findViewById(R.id.check);
    }

    private void updateCurrentTrack(TrackDao dao, Track track, EditText title, EditText description) {
        track.title = title.getText().toString();
        track.description = description.getText().toString();
        dao.updateTrack(track);
    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}