package com.example.chao.mediahub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistFragment.EventListener,
        PlaylistTopControllerFragment.EventListener, PlaylistEditingDialog.OnEditingDoneListener,
        MusicPlaybackService.EventListener {
    private static final String TAG = "PlaylistActivity";
    //parameters name
    private static final String AGR_PLAYLIST_NAME = Tags.Arguments.AGR_PLAYLIST_NAME;
    private static final String AGR_PLAYLIST_ID = Tags.Arguments.AGR_PLAYLIST_ID;

    private int mPlaylistId;
    private String mPlaylistName;

    private MusicPlaybackService mPlaybackService;
    private boolean mBound = false;

    private PlaylistFragment mPlaylistFragment;
    private PlaylistTopControllerFragment mTopController;
    private PlaylistBotInfoFragment mBotInfoBar;

    private boolean isInfoBarVisible = false;
    //private List<MusicFile> mPlaylist;

    //private MediaplayerController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //get info from intent
        Bundle b = getIntent().getExtras();

        //update toolbar title to playlist name
        mPlaylistName = b.getString(AGR_PLAYLIST_NAME);
        toolbar.setTitle(mPlaylistName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPlaylistId = b.getInt(AGR_PLAYLIST_ID, Playlist.INVALID_PLAYLIST_ID);
        if (mPlaylistId == Playlist.INVALID_PLAYLIST_ID) {
            Log.e(TAG, "Invalid Playlist Id");
        } else {
            Log.d(TAG, "Playlist Id : " + mPlaylistId);
        }

        FragmentManager fm = getSupportFragmentManager();

        mTopController = (PlaylistTopControllerFragment) fm.findFragmentByTag(Tags.Fragments.PLAYLIST_TOP_CONTROLLER);
        mPlaylistFragment = (PlaylistFragment) fm.findFragmentById(R.id.fragment_playlist);
        mBotInfoBar = (PlaylistBotInfoFragment) fm.findFragmentByTag(Tags.Fragments.PLAYLIST_BOT_INFO_BAR);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        FragmentManager fm = getSupportFragmentManager();
        if (mTopController == null) {
            Log.d(TAG, "No mTopController");
            mTopController = PlaylistTopControllerFragment.newInstance("", "");
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.playlist_top_controller_container, mTopController, Tags.Fragments.PLAYLIST_TOP_CONTROLLER);
            ft.commit();
        }
        if (mPlaylistFragment == null) {
            mPlaylistFragment = PlaylistFragment.newInstance(mPlaylistId);
            fm.beginTransaction().add(R.id.fragment_playlist, mPlaylistFragment).commit();
        }
        if (mBotInfoBar == null) {
            Log.d(TAG, "Create Bottom Info Bar.");
            mBotInfoBar = PlaylistBotInfoFragment.newInstance("", "");
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mBotInfoBar);
            ft.add(R.id.playlist_info_bar_bottom, mBotInfoBar, Tags.Fragments.PLAYLIST_BOT_INFO_BAR);
            ft.commit();
        }
        super.onResume();
    }
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        if (mBotInfoBar != null) {
//            ft.remove(mBotInfoBar);
//        }
//        if (mPlaylistFragment != null) {
//            ft.remove(mPlaylistFragment);
//        }
//        if (mTopController != null) {
//            ft.remove(mTopController);
//        }
//        ft.commit();
        super.onPause();
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
            //sync background music playback service's playlist
            syncPlaybackServicePlayingList();
            mPlaybackService.setEventListener(PlaylistActivity.this);
            //bind controller to service
            //mController.bindService(mPlaybackService);
            mBotInfoBar.bindService(mPlaybackService);
            //set status
            mTopController.setLoopBtnState(mPlaybackService.getPlaylistLoopingState());
            mTopController.setShuffleBtnState(mPlaybackService.isShuffling());
            mBound = true;
            Log.d(TAG, "MusicPlaybackService connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d(TAG, "MusicPlaybackService disconnected");
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

    @Override
    public void musicFileOnClick(int position) {
        Log.d(TAG, "MusicFileOnClick(), Position: " + position);
        MusicFile file = mPlaylistFragment.getMusicFile(position);
        Log.d(TAG, file.toString());

        if(mBound && mPlaybackService != null) {
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

    @Override
    public void playAll() {
        mPlaybackService.playPlaylistFrom(0);
        showBottomInfoBar();
        mBotInfoBar.startSyncMusicService();
    }

    @Override
    public void onLoop(int state) {
        Log.d(TAG, "OnLoop");
        if (state == Tags.States.STATE_LOOPING) {
            mPlaybackService.setPlaylistLoopingState(state);
        } else if (state == Tags.States.STATE_NOT_LOOPING) {
            //mPlaybackService.setPlaylistNonLooping();
            mPlaybackService.setPlaylistLoopingState(state);
        } else {//TODO: single song loop.
            Log.d(TAG, "unsupported state");
        }
    }

    @Override
    public void onShuffle(boolean state) {
        Log.d(TAG, "onShuffle");
        mPlaybackService.shufflePlaylist(state);
    }

    @Override
    public void onEdit() {
        Log.d(TAG, "onEdit");
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(Tags.Fragments.PLAYLIST_EDITING) == null && mBound) {
            PlaylistEditingDialog fragment = PlaylistEditingDialog.newInstance(
                    new ArrayList<>(mPlaybackService.getMusicFiles()));
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(fragment, Tags.Fragments.PLAYLIST_EDITING);
            ft.commit();
        }
    }

    public void showBottomInfoBar() {
        FragmentManager fm = getSupportFragmentManager();
        if (mBotInfoBar.isHidden()) {        //if added but hidden.
            FragmentTransaction ft = fm.beginTransaction();
            ft.show(mBotInfoBar);
            ft.commit();
            Log.d(TAG, "show top info bar");
        } else {
            Log.d(TAG, "not hidden");
        }
    }

    public void hideBottomInfoBar() {
        FragmentManager fm = getSupportFragmentManager();
        if (mBotInfoBar != null && mBotInfoBar.isAdded() && !mBotInfoBar.isHidden()) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mBotInfoBar);
            ft.commit();
            Log.d(TAG, "hide bot info bar");
        }
    }

    @Override
    public void playlistOnCompletion() {
        hideBottomInfoBar();
        mPlaylistFragment.clearHighlighted();
    }

    @Override
    public void musicFileOnStart(int position) {
        Log.d(TAG, "POSITION: " + position);
        if (mBotInfoBar.isHidden()) {
            showBottomInfoBar();
        }
        mBotInfoBar.startSyncMusicService();
        mPlaylistFragment.setPositionHighlighted(position);
    }

    @Override
    public void notifyChange() {

    }

    @Override
    public void onEditingDone(List<String> audioIds) {
        //update MediaStore
        MediaManager.writePlaylist(this, mPlaylistId, mPlaylistName, audioIds);
        //remove dialog
        FragmentManager fm = getSupportFragmentManager();
        Fragment dialog = fm.findFragmentByTag(Tags.Fragments.PLAYLIST_EDITING);
        if (dialog != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(dialog);
            ft.commit();
        }

        List<MusicFile> files = MediaManager.getPlaylistMusicFiles(this, mPlaylistId);
        //update playlist in PlaybackService if bound.
        if (mPlaybackService != null && mBound) {
            mPlaybackService.reorderPlaylist(files);
            int pos = mPlaybackService.getCurrentPosition();
            mPlaylistFragment.setPositionHighlighted(pos);
            Log.d(TAG, "Current Pos: " + pos);
            mPlaylistFragment.updateUI();
        }
    }
}
