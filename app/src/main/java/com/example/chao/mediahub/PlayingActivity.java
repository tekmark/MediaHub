package com.example.chao.mediahub;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

public class PlayingActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        MusicPlaybackService.EventListener {
    private static final String TAG = "PlayingActivity";

    private MusicPlaybackService mPlaybackService;
    private boolean mBound;

    private MediaplayerControllerFragment mControllerFragment;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //self-defined navigation up button.
        toolbar.setNavigationIcon(R.drawable.collapse_bottom_arrow_32);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(this);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Tags.Fragments.PLAYING_CONTROLLER);
        if (fragment != null) {
            mControllerFragment = (MediaplayerControllerFragment)fragment;
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mControllerFragment = MediaplayerControllerFragment.newInstance("", "");
            ft.add(R.id.mediaplayer_controller_container, mControllerFragment, Tags.Fragments.PLAYING_CONTROLLER);
            ft.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent playInent = new Intent(this, MusicPlaybackService.class);
        bindService(playInent, mConnection, Context.BIND_AUTO_CREATE);
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

    @Override
    public void finish() {
        super.finish();
        //set animation.
        overridePendingTransition(R.anim.slide_out_no_anim, R.anim.slide_out_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        Log.d(TAG, "onOptionsItemSelected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
//            Log.d(TAG, "HOME");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            //cast the IBinder and get LocalService instance
            MusicPlaybackService.ServiceBinder binder = (MusicPlaybackService.ServiceBinder) service;
            mPlaybackService = binder.getService();
            mBound = true;

            mControllerFragment.bindService(mPlaybackService);
            mControllerFragment.syncMusicServiceStart();

            int size = mPlaybackService.getMusicFiles().size();
            PlayingPagerAdapter mPageAdapter = new PlayingPagerAdapter(getSupportFragmentManager(), size);
            mViewPager.setAdapter(mPageAdapter);

            mPlaybackService.setEventListener(PlayingActivity.this);
            int pos = mPlaybackService.getCurrentPosition();
            mViewPager.setCurrentItem(pos);

            Log.d(TAG, "MusicPlaybackService connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d(TAG, "MusicPlaybackService disconnected");
        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int currentPos = mPlaybackService.getCurrentPosition();
        Toast.makeText(this,
                "Selected page position: " + position + " Current: " + currentPos, Toast.LENGTH_SHORT).show();
        mPlaybackService.playPlaylistFrom(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void playlistOnCompletion() {
        Log.d(TAG, "PlaylistOnCompletion()");
        mControllerFragment.syncMusicServiceStop();
        finish();
    }

    @Override
    public void musicFileOnStart(int position) {
        Log.d(TAG, "on Start: position : " + position);
        mViewPager.setCurrentItem(position);
        mControllerFragment.syncMusicServiceStart();
    }

    @Override
    public void notifyChange() {

    }

    public class PlayingPagerAdapter extends FragmentStatePagerAdapter {
        private int mNumOfPages;

        private PlayingPagerAdapter(FragmentManager fm, int numOfPages) {
            super(fm);
            mNumOfPages = numOfPages;
        }

        @Override
        public int getCount() {
            return mNumOfPages;
        }

        @Override
        public Fragment getItem(int position) {
            MusicFile file = mPlaybackService.getMusicFile(position);
            return PlayingFragment.newInstance(file);
        }
    }
}
