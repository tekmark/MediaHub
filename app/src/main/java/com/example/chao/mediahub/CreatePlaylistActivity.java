package com.example.chao.mediahub;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class CreatePlaylistActivity extends AppCompatActivity {

    static final String TAG = "CreatePlaylistActivity";

    static final int PICK_MUSIC_FILES_REQUEST = 1;

    static final String STATE_ADDED_MUSIC_FILES = "AddedMusicFiles";
    static final String STATE_MUSIC_FILES = "MusicFiles";

    private Button mCancel;
    private Button mDone;
    private Button mAddSongs;
    private EditText mEditText;

    private ArrayList<MusicFile> mAddedMusicFiles;
    private ArrayList<MusicFile> mMusicFiles;

    private PlaylistAddMusicFragment mAddMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        mCancel = (Button) findViewById(R.id.top_bar_button_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel onClick()");
                finish();
            }
        });
        mDone = (Button) findViewById(R.id.top_bar_button_done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Done onClick()");
                String name = mEditText.getText().toString();
                Log.d(TAG, "EditText content : " + name);
//                showEmptyPlaylistAlertDialog();
                MediaManager.writePlaylist(getApplicationContext(), name, getAddedMusicFileIds());
                finish();
            }
        });

        int playlistId = getIntent().getIntExtra("playlist_id", -1);
        Log.d(TAG, "Playlist ID : " + playlistId);

        //currentAdded
        if (savedInstanceState != null) {
            Log.d(TAG, "Restore Saved State");
            mMusicFiles = savedInstanceState.getParcelableArrayList(STATE_MUSIC_FILES);
            mAddedMusicFiles = savedInstanceState.getParcelableArrayList(STATE_ADDED_MUSIC_FILES);
        } else {
            Log.d(TAG, "No Saved Instance State");
            mMusicFiles = new ArrayList<>();
            mAddedMusicFiles = new ArrayList<>();
            if (playlistId != -1) {
                List<MusicFile> list = MediaManager.getPlaylistMusicFiles(getApplicationContext(), playlistId);
                Log.d(TAG, "playlist id : " + playlistId + " exists. list size : " + list.size());
                mMusicFiles.addAll(list);
                mAddedMusicFiles.addAll(list);
            }
        }

        mAddSongs = (Button) findViewById(R.id.create_playlist_button_add);
        mAddSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick() - 'Add Songs' Button. # of current added music files : "
                        + mMusicFiles.size());
                Intent intent = new Intent(getApplicationContext(), PlaylistAddMusicActivity.class);
                intent.putParcelableArrayListExtra("Current Added Music Files", mAddedMusicFiles);
                startActivityForResult(intent, PICK_MUSIC_FILES_REQUEST);
            }
        });
        mEditText = (EditText) findViewById(R.id.create_playlist_edittext_name);

        mAddMusicFragment = (PlaylistAddMusicFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_music_file_list);
        if (mAddMusicFragment == null) {
            Log.e(TAG, "cannot find fragment");
        } else {
            Log.d(TAG, "load current added songs");
            mAddMusicFragment.loadAddedMusicFiles(mAddedMusicFiles);
            mAddMusicFragment.reloadMusicFiles(mMusicFiles);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAddMusicFragment != null) {
            Log.d(TAG, "onResume(), # of added Music files : " + mMusicFiles.size());
            //mAddMusicFragment.reloadMusicFiles(mMusicFiles);
        } else {
            Log.d(TAG, "Fragment Error");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_MUSIC_FILES_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Get Result");
                List<MusicFile> result = data.getParcelableArrayListExtra("Added Files");

                mMusicFiles.clear();       //clear() is necessary.
                mMusicFiles.addAll(result);
                mAddedMusicFiles.clear();
                mAddedMusicFiles.addAll(result);

                mAddMusicFragment.loadAddedMusicFiles(mAddedMusicFiles);
                mAddMusicFragment.reloadMusicFiles(mMusicFiles);
                if (mMusicFiles != null) {
                    Log.d(TAG, " # of Current Added ID : " + mMusicFiles.size());
                } else {
                    Log.e(TAG, "No added music file");
                }
            } else {
                Log.e(TAG, "not RESULT_OK");
            }
        } else {
            Log.e(TAG, "Unsupported Request Code");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save current state
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putParcelableArrayList(STATE_MUSIC_FILES, mMusicFiles);
        savedInstanceState.putParcelableArrayList(STATE_ADDED_MUSIC_FILES, mAddedMusicFiles);
        super.onSaveInstanceState(savedInstanceState);
    }

    private List<String> getAddedMusicFileIds() {
        List<String> ids = new ArrayList<>();
        for (MusicFile file : mAddedMusicFiles) {
            String id = Integer.toString(file.getId());
            ids.add(id);
        }
        return ids;
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
//                finish();
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
