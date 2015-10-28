package com.example.chao.mediahub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistFragment.EventListener {
    final static String TAG = "PlaylistActivity";

    private MusicPlaybackService mPlaybackService;
    private boolean mBound = false;

    private int mPlaylistId;
    private PlaylistFragment mPlaylistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //get info from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(PlaylistsTabFragment.EXTRA_PLAYLIST_NAME);

        //update toolbar title to playlist name
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        mPlaylistId = intent.getIntExtra(PlaylistsTabFragment.EXTRA_PLAYLIST_ID, Playlist.INVALID_ID);
        if (mPlaylistId == Playlist.INVALID_ID) {
            Log.e(TAG, "Invalid Playlist Id");
        } else {
            Log.d(TAG, "Playlist Id : " + mPlaylistId);
        }

        FragmentManager fm = getSupportFragmentManager();
        mPlaylistFragment = (PlaylistFragment) fm.findFragmentById(R.id.fragment_playlist);
        if (mPlaylistFragment == null) {
            mPlaylistFragment = PlaylistFragment.newInstance(mPlaylistId);
            fm.beginTransaction().add(R.id.fragment_playlist, mPlaylistFragment).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent playIntent = new Intent(this, MusicPlaybackService.class);
        startService(playIntent);
        bindService(playIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            Log.d(TAG, "Service disconnected");
        }
        //clear item views;
        //clearItemViews();
        super.onStop();
    }


    //define local service connection for bound service.
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //cast the IBinder and get LocalService instance
            //MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) service;
            MusicPlaybackService.ServiceBinder binder = (MusicPlaybackService.ServiceBinder) service;
            mPlaybackService = binder.getService();
//            mMusicService.loadCurrPlaylist(playlist);
            Log.d(TAG, "MusicPlaybackService connected");
            syncPlaybackServicePlayingList();

//            mPlaybackService.loadPlaylist(mPlaylistId);

            //bind controller to service
//            mController.bindToMusicService(mMusicService);
            //mController.sync();

            //place items on screen;
//            createItemsViews(mMusicService.getMusicFiles());
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
            Log.d(TAG, "position " +position);
        } else {
            Log.w(TAG, "PlaybackService is not bound");
        }
    }

    private void syncPlaybackServicePlayingList() {
        //sync playlist
        List<MusicFile> list = mPlaylistFragment.getPlaylist();
        mPlaybackService.updatePlayingList(list);
    }

}
