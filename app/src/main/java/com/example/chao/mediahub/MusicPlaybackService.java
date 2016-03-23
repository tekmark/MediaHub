package com.example.chao.mediahub;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MusicPlaybackService";

    //private static final String ACTION_PLAY = "com.example.action.PLAY";
    //final static private int REACH_THE_END_OF_PLAYLIST = -1;
    private final ServiceBinder mBinder = new ServiceBinder();

    private MediaPlayer mMediaPlayer = null;

    private PlaybackServicePlayingList mPlayingList;

    private static final int PLAYING_LIST_READY_STATE = 1;
    private static final int PLAYING_LIST_INITIALIZED_STATE = 0;
    private static final int PLAYING_LIST_INVALID_STATE = -1;

    private int mPlayingListState;
    private int mCurrentPosition;

    private boolean isRepeat = false;
    private boolean isRepeatAll = false;

    private int mLoopingState;

    private int mCurrentPlaylistId;

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();

        //construct a playlist
        mPlayingList = PlaybackServicePlayingList.newInstance();

        //set currentPositison to invalid position;
        mCurrentPosition = PlaybackServicePlayingList.PLAYLIST_INVALID_POS;
        mPlayingListState  = PLAYING_LIST_INVALID_STATE;

        mLoopingState = Tags.States.STATE_NOT_LOOPING;

        mCurrentPlaylistId = Playlist.INVALID_PLAYLIST_ID;
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();                            // initialize MediaPlayer
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  // set stream type to music
        //set wake lock
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //set listeners
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
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
        prepareNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "OnPrepared() is called");
        mMediaPlayer.start();
        if (mListener != null) {
            mListener.musicFileOnStart(mCurrentPosition);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MediaPlayer", "onError() is called, ERROR TYPE is " + what +
                " ERROR CODE is " + extra);
        mPlayingList.resetCur();
        return true;
    }

    public int getCurrentPlaylistId() {
        return mCurrentPlaylistId;
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


    public int getPlaylistLoopingState() {
        return mLoopingState;
    }

    public void setPlaylistLoopingState(int state) {
        mLoopingState = state;
        if (state == Tags.States.STATE_LOOPING) {
            mPlayingList.setLooping(true);
        } else if (state == Tags.States.STATE_NOT_LOOPING){
            mPlayingList.setLooping(false);
        } else {    //TODO: single song loop.
            Log.d(TAG, "Unsupported Playlist State");
            return;
        }
        mLoopingState = state;
    }

    public void updatePlayingList(List<MusicFile> list) {
        mPlayingList.update(list);
        mPlayingListState = PLAYING_LIST_INITIALIZED_STATE;
        Log.d(TAG, "Playing List updated. size is : " + list.size());
    }

    public void updatePlayingList(List<MusicFile> list, int playlistId) {
        updatePlayingList(list);
        mCurrentPlaylistId = playlistId;
    }

    public void reorderPlaylist(List<MusicFile> list) {
        int currentAudioId = mPlayingList.getCurrentMusicFile().getAudioId();
        mPlayingList.reorder(list, getCurrentMusicFile());
    }

    public List<MusicFile> getMusicFiles() {
        return mPlayingList.getMusicFiles();
    }
    public MusicFile getMusicFile(int pos) {
        //TODO: handle shuffling
        return mPlayingList.getMusicFile(pos);
    }

    public void play() {
        Log.d(TAG, "play() is called");
//        if (mPlayingListState == PLAYING_LIST_INITIALIZED_STATE) {
//            mPlayingListState = PLAYING_LIST_READY_STATE;
//            mMediaPlayer.prepareAsync();
//            return;
//        }
        mMediaPlayer.start();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void playPlaylistFrom(int pos) {
        mMediaPlayer.reset();
        mPlayingList.move(pos);
        prepareNext();
    }

    public void prepareFirstMusicFile() {
        mMediaPlayer.reset();
        mPlayingList.resetCur();
        if (mPlayingList.hasNext()) {
            mCurrentPosition = mPlayingList.nextIndex();
            mCurrentFile = mPlayingList.next();
            Log.d(TAG, "Current position : " + mCurrentPosition + ", Current File: " + mCurrentFile.toString());
            String path = mCurrentFile.getPath();
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                //TODO: error handling
                Log.e(TAG, "Cannot find audio file: " + path);
            }
        }
    }

//    private void informActivity(String action, String msg) {
//        Intent intent = new Intent("my-event");
//        intent.putExtra("message", mCurrentPosition);
//        Log.d(TAG, "INTENT SEND");
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }

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

    public void initPlayingList() {
        Log.d(TAG, "initialize playing list");
        mMediaPlayer.reset();
        mPlayingList.resetCur();
        if (mPlayingList.hasNext()) {
            mCurrentPosition = mPlayingList.nextIndex();
            mCurrentFile = mPlayingList.next();
            String songPath = mCurrentFile.getPath();
            try {
                mMediaPlayer.setDataSource(songPath);
                mPlayingListState = PLAYING_LIST_INITIALIZED_STATE;
            } catch (IOException e) {
                Log.e(TAG, "Fail to set data source: " + songPath);
            }
        } else {
            Log.w(TAG, "Playing list is empty");
        }
    }

    //for on complete
    public void prepareNext() {
        Log.d(TAG, "prepareNext() is called. Current position : " + mCurrentPosition);
        if (mPlayingList.hasNext()) {
            mCurrentPosition = mPlayingList.nextIndex();
            mCurrentFile = mPlayingList.next();
            Log.d(TAG, "Current position : " + mCurrentPosition + ", Current File: " + mCurrentFile.toString());
            String path = mCurrentFile.getPath();
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                //TODO: error handling
                Log.e(TAG, "Cannot find audio file: " + path);
            }
        } else {
            Log.d(TAG, "Reach the end of current playlist.");
            mMediaPlayer.reset();
            mPlayingList.resetCur();
            if (mListener != null) {
                mListener.playlistOnCompletion();
            } else {
                Log.d(TAG, "No Event Listener");
            }
        }
    }

    public void next() {
        Log.d(TAG, "next() is called");
        boolean playing = isPlaying();
        mMediaPlayer.reset();
        if (mPlayingList.hasNext()) {
            mCurrentPosition = mPlayingList.nextIndex();
            mCurrentFile = mPlayingList.next();
            String songPath = mCurrentFile.getPath();
            try {
                mMediaPlayer.setDataSource(songPath);
                Log.d(TAG, "Set data source: " + songPath);
                if (playing) {
                    mMediaPlayer.prepareAsync();
                }
            } catch (IOException e) {
                //TODO: exception handling.
                Log.e(TAG, "Fail to set data source: " + songPath);
            }
        } else {
            Log.d(TAG, "No Next In Playlist");
            initPlayingList();
            //resetPlaylist();
        }
    }

    public void previous() {
        Log.d(TAG, "previous() is called");
        boolean playing = isPlaying();
        mMediaPlayer.reset();
        if (mPlayingList.hasPrevious()) {
            mCurrentPosition = mPlayingList.previousIndex();
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

    private boolean prepareNextSong() {
        Log.d(TAG, "prepareNextSong() is called");
        if (mPlayingList.hasNext()) {
            mCurrentPosition = mPlayingList.nextIndex();
            mCurrentFile = mPlayingList.next();
            String songPath = mCurrentFile.getPath();

            Log.d(TAG, "current position is " + mCurrentPosition);
            return prepare(songPath);
        } else {
            Log.d(TAG, "Reach the end of playlist");
            initPlayingList();
            return false;
        }
    }

    private boolean preparePrevSong() {
        Log.d(TAG, "preparePrevSong() is called");
        if (mPlayingList.hasPrevious()) {
            String songPath = mPlayingList.previous().getPath();
            mCurrentPosition = mPlayingList.previousIndex();
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
            //long currDuration = mMediaPlayer.getmCurrentPosition();
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
        mCurrentPosition = -1;

//        if (prepareNextSong()) {
//            Log.d(LOG_TAG, "move to next song in Playlist");
//        } else {
//            Log.e(LOG_TAG, "cannot find next song after reset");
//        }
    }
    public String getCurrentArtist() {
        //return currentArtist;
        return mPlayingList.getMusicFile(mCurrentPosition).getArtist();
    }

    public String getCurrentTitle() {
        //return currentTitle;
        return mPlayingList.getMusicFile(mCurrentPosition).getTitle();
    }

    //private String currentArtist;
    //private String currentTitle;

    private MusicFile mCurrentFile;
    //    void updateCurrentFileInfo() {
//        mCurrentFile = new MusicFile(mPlayingList.getMusicFile(mCurrentPosition));
//    }

//    public int getmCurrentPosition() {
//        return mCurrentPosition;
//    }
    public int getCurrentPosition() {
        return mPlayingList.getCurrentPosition();
    }

    public MusicFile getCurrentMusicFile() {
        if (mCurrentPosition != PlaybackServicePlayingList.PLAYLIST_INVALID_POS){
            return mPlayingList.getMusicFile(mCurrentPosition);
        } else {
            return null;
        }
    }

    public long getCurrentDuration(){
        return mMediaPlayer.getCurrentPosition();
    }
    public long getTotalDuration() {
        if (mMediaPlayer.isPlaying()) {
            return mMediaPlayer.getDuration();
        } else {
            MusicFile file = getCurrentMusicFile();
            if (file != null) {
                return file.getDuration();
            } else {
                return 0;
            }
        }
    }

    private EventListener mListener;

    public interface EventListener {
        void playlistOnCompletion();
        void musicFileOnStart(int position);
        void notifyChange();
    }

    public void setEventListener(Context context) {
        Log.d(TAG, "Set New Event Listener. " + context.getClass().getName());
        mListener = (EventListener)context;
    }

    public boolean isShuffling() {
        return mPlayingList.isShuffling();
    }

    public void shufflePlaylist(boolean shuffle) {
        mPlayingList.shuffle(shuffle);
        Log.d(TAG, mPlayingList.orderListToString());
    }

}
