package com.example.chao.mediahub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistFragment.EventListener {
    final static String TAG = "PlaylistActivity";

    private MusicPlaybackService mPlaybackService;
    private boolean mBound = false;

    private int mPlaylistId;
    private PlaylistFragment mPlaylistFragment;

    private boolean isInfoBarVisible = false;
    //private List<MusicFile> mPlaylist;

    private MediaplayerController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //get info from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(PlaylistsTabFragment.EXTRA_AGR_PLAYLIST_NAME);

        //update toolbar title to playlist name
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mController = MediaplayerController.newInstance(this);

        mPlaylistId = intent.getIntExtra(PlaylistsTabFragment.EXTRA_AGR_PLAYLIST_ID, Playlist.INVALID_PLAYLIST_ID);
        if (mPlaylistId == Playlist.INVALID_PLAYLIST_ID) {
            Log.e(TAG, "Invalid Playlist Id");
        } else {
            Log.d(TAG, "Playlist Id : " + mPlaylistId);
        }

        //mController = MediaPlayerController.newInstance(this);

        FragmentManager fm = getSupportFragmentManager();
        mPlaylistFragment = (PlaylistFragment) fm.findFragmentById(R.id.fragment_playlist);
        if (mPlaylistFragment == null) {
            mPlaylistFragment = PlaylistFragment.newInstance(mPlaylistId);
            fm.beginTransaction().add(R.id.fragment_playlist, mPlaylistFragment).commit();
        }
        showInfoBar();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected(), Id: " + id + " home id: " + android.R.id.home);
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            Log.d(TAG, "navigate up");
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        Log.d(TAG, "onOptionsItemSelected(), Id: " + id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlaybackService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "Service disconnected");
        }
        super.onStop();
    }

    public void showController(View view) {
        Intent intent = new Intent(this, PlayingActivity.class);
        startActivity(intent);
        Log.d(TAG, "Show Playing");
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_no_anim);
    }

    //define local service connection for bound service.
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //cast the IBinder and get LocalService instance
            MusicPlaybackService.ServiceBinder binder = (MusicPlaybackService.ServiceBinder) service;
            mPlaybackService = binder.getService();
            Log.d(TAG, "MusicPlaybackService connected");
            //sync background music playback service's playlist
            syncPlaybackServicePlayingList();

            //bind controller to service
            mController.bindService(mPlaybackService);

            mBound = true;
    }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "MusicPlaybackService disconnected");
            mBound = false;
        }
    };

    @Override
    public void itemPositionOnClick(int position) {
        if(mBound && mPlaybackService != null) {
            Log.d(TAG, "position " + position);
            if (mPlaybackService.getCurrentPosition() == position && mPlaybackService.isPlaying()) {
                mPlaybackService.pause();
            } else {
                mPlaybackService.playPlaylistFrom(position);
                //mController.sync();
            }
            mPlaylistFragment.setPositionHighlighted(position);
        } else {
            Log.w(TAG, "PlaybackService is not bound");
        }
    }

    private void syncPlaybackServicePlayingList() {
        //sync playlist
        List<MusicFile> list = mPlaylistFragment.getPlaylist();
        mPlaybackService.updatePlayingList(list);
    }

    private void hideInfoBar() {
        if (isInfoBarVisible) {
            View view = findViewById(R.id.playlist_info_bar_bottom);
            view.setVisibility(View.INVISIBLE);
            isInfoBarVisible = false;
        }
    }
    private void showInfoBar() {
        if (!isInfoBarVisible) {
            View view = findViewById(R.id.playlist_info_bar_bottom);
            view.setVisibility(View.VISIBLE);
            isInfoBarVisible = true;
        }
    }
}
