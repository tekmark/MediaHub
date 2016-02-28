package com.example.chao.mediahub;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAddMusicActivity extends AppCompatActivity {

    final private static String TAG = "PlaylistAddMusicAct";

    private Button mCancel;
    private Button mDone;

    private PlaylistAddMusicFragment mMusicFilesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_add_music);

        mCancel = (Button) findViewById(R.id.top_bar_button_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel on Click");
                finish();
            }
        });

        mDone = (Button) findViewById(R.id.top_bar_button_done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Done on Click");
                ArrayList<MusicFile> list = new ArrayList<>(mMusicFilesFragment.getAddedMusicList());
                String s = "";
                for (MusicFile f : list) {
                    s += f.getId() + ",";
                }
                Log.d(TAG, "current added list " + s);
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("Added Files", list);
                setResult(RESULT_OK, resultIntent);

                finish();
            }
        });

        Intent intent = getIntent();
        ArrayList<MusicFile> addedMusicFiles = intent.getParcelableArrayListExtra("Current Added Music Files");
        Log.d(TAG, "# of added music files : " + addedMusicFiles.size());

        mMusicFilesFragment = (PlaylistAddMusicFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_music_file_list);
        if (mMusicFilesFragment != null) {
            mMusicFilesFragment.loadAddedMusicFiles(addedMusicFiles);
            mMusicFilesFragment.reloadMusicFiles(MediaManager.getAllMusicFiles(this));
            List<MusicFile> list = mMusicFilesFragment.getAddedMusicList();
            String s = "";
            for (MusicFile f : list) {
                s += f.getId() + ",";
            }
            Log.d(TAG, "current added list " + s);
        } else {
            Log.d(TAG, "fragment is null");
        }
    }

}
