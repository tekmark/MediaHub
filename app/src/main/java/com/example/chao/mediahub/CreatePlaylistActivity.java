package com.example.chao.mediahub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CreatePlaylistActivity extends AppCompatActivity {

    final static String TAG = "CreatePlaylistActivity";
    private Button mCancel;
    private Button mDone;
    private Button mAddSongs;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        mCancel = (Button) findViewById(R.id.create_playlist_button_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel onClick()");
                finish();
            }
        });
        mDone = (Button) findViewById(R.id.create_playlist_button_done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Done onClick()");
                String name = mEditText.getText().toString();
                Log.d(TAG, "EditText content : " + name);
                showEmptyPlaylistAlertDialog();
                MediaManager.writePlaylist(getApplicationContext(), name, new ArrayList<String>());
            }
        });
        mAddSongs = (Button) findViewById(R.id.create_playlist_button_add);
        mAddSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add songs");
                Intent intent = new Intent(getApplicationContext(), PlaylistAddMusicActivity.class);
                startActivity(intent);
            }
        });
        mEditText = (EditText) findViewById(R.id.create_playlist_edittext_name);
    }


    private void showEmptyPlaylistAlertDialog() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("hello!")
                .setTitle("empty");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d(TAG, "OK");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Log.d(TAG, "Cancel");
            }
        });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
