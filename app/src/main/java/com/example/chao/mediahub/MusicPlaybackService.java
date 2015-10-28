package com.example.chao.mediahub;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    final static private String TAG = "MusicPlaybackService";

    //private static final String ACTION_PLAY = "com.example.action.PLAY";
    private final ServiceBinder mBinder = new ServiceBinder();

    private MediaPlayer mMediaPlayer = null;

    private PlaybackServicePlayingList mPlayingList;

    private int currentPosition;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isRepeatAll = false;

    public void initMediaPlayer() {
        //mPlaylistManger = new PlaylistManager();

        mMediaPlayer = new MediaPlayer();                            // initialize MediaPlayer
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  // set stream type to music
        //set wake lock
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //set listeners
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        //set currentPositison to -1;
        currentPosition = -1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        mPlayingList = new PlaybackServicePlayingList("default playlist");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent.getAction().equals(ACTION_PLAY)) {
//            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestory() is called");
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "OnCompleteListener is called");
        reset();
        if (prepareNextSong()) {
            //informActivity("", "");
        } else {
            //resetPlaylist();
            mPlayingList.resetCur();
            currentPosition = -1;
            //next();
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "OnPrepared() is called");
        play();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MediaPlayer", "onError() is called, ERROR TYPE is " + what +
                " ERROR CODE is " + extra);
        mPlayingList.resetCur();
        return true;
    }


    //TODO: link to it.
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }


    //binder for service
    public class ServiceBinder extends Binder {
        MusicPlaybackService getService () {
            return MusicPlaybackService.this;
        }
    }

    public void loadPlaylist(int playlistId) {
        //mPlayingList = PlaylistManager.getPlaylistFromMediaStore(this.getApplicationContext(), playlistId);
        /*if (isRepeatAll) {
            mPlayingList.setRepeat();
        } else {
            mPlayingList.resetRepeat();
        }*/
    }


//    public Playlist getmPlayingList() {
//        return mPlayingList;
//    }

    public void updatePlayingList(List<MusicFile> list) {
        mPlayingList.update(list);
        Log.d(TAG, "Playing List updated");
    }


    public List<MusicFile> getMusicFiles() {
        return mPlayingList.getMusicFiles();
    }

    public void play() {
        Log.d(TAG, "play() is called");
        if (currentPosition < 0) {
            mMediaPlayer.prepareAsync();
        }
        mMediaPlayer.start();
        informActivity("", "");
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void playPlaylistFrom(int pos) {
        mMediaPlayer.reset();
        mPlayingList.move(pos);
        //currentPosition = pos;
        prepareNextSong();
    }

    private void informActivity(String action, String msg) {
        Intent intent = new Intent("my-event");
        intent.putExtra("message", currentPosition);
        Log.d(TAG, "INTENT SEND");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void pause() {
        Log.d(TAG, "pause() is called");
        mMediaPlayer.pause();
    }

    public void stop() {
        Log.d(TAG, "stop() is called");
        mMediaPlayer.stop();
    }

    public void reset() {
        Log.d(TAG, "reset() is called");
        mMediaPlayer.reset();
    }

    public void next() {
        Log.d(TAG, "next() is called");
        boolean playing = isPlaying();
        mMediaPlayer.reset();
        if (mPlayingList.hasNext()) {
            currentPosition = mPlayingList.nextIndex();
            currentFile = mPlayingList.next();
            String songPath = currentFile.getPath();
            try {
                mMediaPlayer.setDataSource(songPath);
                Log.d(TAG, "Set data source: " + songPath);
                if (playing) {
                    mMediaPlayer.prepareAsync();
                }
            } catch (IOException e) {
                Log.e(TAG, "Fail to set data source: " + songPath);
            }
        } else {
            Log.d(TAG, "No Next In Playlist");
            resetPlaylist();
        }
    }

    public void previous() {
        Log.d(TAG, "previous() is called");
        boolean playing = isPlaying();
        mMediaPlayer.reset();
        if (mPlayingList.hasPrevious()) {
            currentPosition = mPlayingList.previousIndex();
            String songPath = mPlayingList.previous().getPath();
            try {
                mMediaPlayer.setDataSource(songPath);
                Log.d(TAG, "Set data source: " + songPath);
                if (playing) {
                    mMediaPlayer.prepareAsync();
                }
            } catch (IOException e) {
                Log.e(TAG, "Fail to set data source: " + songPath);
            }
        } else {
            Log.d(TAG, "No Previous In Playlist");
            resetPlaylist();
        }
    }

    public void skipToStart() {
        Log.d(TAG, "skipTOStart() is called");
        mMediaPlayer.seekTo(0);
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }


    public int getProgress() {
        return 0;
    }

//    public void loadCurrPlaylist(Playlist list) {
//        mPlayingList = new Playlist(list);
//        mPlayingList.resetCur();
//        prepareNextSong();
//    }

    private boolean prepareNextSong() {
        Log.d(TAG, "prepareNextSong() is called");
        if (mPlayingList.hasNext()) {
            currentPosition = mPlayingList.nextIndex();
            currentFile = mPlayingList.next();
            String songPath = currentFile.getPath();

            Log.d(TAG, "current position is " + currentPosition);
            return prepare(songPath);
        } else {
            Log.d(TAG, "Reach the end of playlist");
            return false;
        }
    }

    private boolean preparePrevSong() {
        Log.d(TAG, "preparePrevSong() is called");
        if (mPlayingList.hasPrevious()) {
            String songPath = mPlayingList.previous().getPath();
            currentPosition = mPlayingList.previousIndex();
            return prepare(songPath);
        } else {
            Log.d(TAG, "Reach the end of playlist");
            return false;
        }
    }

    private boolean prepare(String songPath) {
        try {
            Log.d(TAG, "Media file path : " + songPath);
            mMediaPlayer.setDataSource(songPath);
            mMediaPlayer.prepareAsync();
            //mProgressBar.setProgress(0);
            //mProgressBar.setMax(mMediaPlayer.getDuration());
            //long totalDuration = mMediaPlayer.getDuration();
            //long currDuration = mMediaPlayer.getCurrentPosition();
            //mCurrDuration.setText(MediaPlayerUtils.millSecondsToTime(currDuration));
            //mTotalDuration.setText(MediaPlayerUtils.millSecondsToTime(totalDuration));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Cannot find audio file: " + songPath);
            return false;
        }
    }

    private void resetPlaylist() {
        Log.d(TAG, "resetPlaylist() is called");
        mPlayingList.resetCur();
        currentPosition = -1;

//        if (prepareNextSong()) {
//            Log.d(LOG_TAG, "move to next song in Playlist");
//        } else {
//            Log.e(LOG_TAG, "cannot find next song after reset");
//        }
    }
    public String getCurrentArtist() {
        //return currentArtist;
        return mPlayingList.getMusicFile(currentPosition).getArtist();
    }

    public String getCurrentTitle() {
        //return currentTitle;
        return mPlayingList.getMusicFile(currentPosition).getTitle();
    }

    //private String currentArtist;
    //private String currentTitle;

    private MusicFile currentFile;
    //    void updateCurrentFileInfo() {
//        currentFile = new MusicFile(mPlayingList.getMusicFile(currentPosition));
//    }
    private String mostRecentTotalDuration;
    private String mostRecentCurrentDuration;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public long getDuration() {
        return mMediaPlayer.getDuration();
    }
    public long getCurrentDuration() {
        return mMediaPlayer.getCurrentPosition();
    }

}

