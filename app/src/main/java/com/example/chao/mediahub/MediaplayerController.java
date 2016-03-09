package com.example.chao.mediahub;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chaohan on 10/28/15.
 */
public class MediaplayerController {
    final static public int PLAY_PREVIOUS_THRESHOLD_SEC = 10;
    final static public int DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC = 200;
    final static public int DEFAULT_PROGRESS_BAR_REFRESH_TIMES = 500;

    final static private String TAG = "MediaPlayerController";

    //elements
    private ImageButton mBtnPlayPause;
    private ImageButton mBtnEnd;
    private ImageButton mBtnSkipToStart;
    private ImageButton mBtnShuffle;
    private ImageButton mBtnLoop;
    private SeekBar mProgressBar;
    private TextView mCurrDuration;
    private TextView mTotalDuration;
    private TextView mTitle;
    private TextView mArtist;

    //music service bound to
    private MusicPlaybackService mService;
    //music service bound status.
    private boolean mBoundStatus;

    //task handler for sync with service.
    private Handler mHandler;

    private MediaplayerController() {
        mService = null;
        mBoundStatus = false;
        mHandler = new Handler();
    }

    public static MediaplayerController newInstance() {
        return new MediaplayerController();
    }

    public static MediaplayerController newInstance(View view) {
        MediaplayerController controller = new MediaplayerController();
        controller.bindLayout(view);
        return controller;
    }

    //bind controller's layout to service, and set listeners
    public boolean bindService(MusicPlaybackService musicService) {
        if (musicService == null) {
            mBoundStatus = false;
            Log.w(TAG, "Unable to bind to music service. Check if music service exists.");
            return false;
        } else {
            mService = musicService;
            mBoundStatus = true;
            Log.d(TAG, "Music service is bound");
            setListeners();
            syncStatus();
            return true;
        }
    }

    public boolean isBound() {
        return mBoundStatus;
    }

    public void enableProgressBar(boolean enable) {
        if (mProgressBar != null) {
            mProgressBar.setEnabled(enable);
            if (enable) {
                Log.d(TAG, "Progress bar is enabled");
            } else {
                Log.d(TAG, "Progress bar is disabled");
            }
        } else {
            Log.w(TAG, "Progress bar is missing in layout");
        }
    }

    public void enableSeek(boolean enable) {
        if (mProgressBar != null) {
            enableProgressBar(true);
            if (enable) {
                mProgressBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                Log.d(TAG, "Progress bar is seekable");
            } else {
                mProgressBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                Log.d(TAG, "Progress bar is not seekable");
            }
        } else {
            Log.w(TAG, "Progress bar is not mBoundStatus or missing in layout");
        }
    }

    public void setInfo(String title, String artist, String duration){

    }

    private void setListeners() {
        if (mBtnPlayPause != null) setPlayPauseButtonListener();
        if (mBtnEnd != null) setEndButtonListener();
        if (mBtnSkipToStart != null) setSkipToStartButtonListener();
        if (mBtnShuffle != null) setShuffleButtonListener();
        if (mBtnLoop != null) setRepeatButtonListener();
        if (mProgressBar != null) setSeekBarListener();
    }

    private void syncStatus() {
//        if (mBtnPlayPause != null) {
//            if (mService.isPlaying()) {
//                Log.d(TAG, "Music is playing, Button image: Pause(Ready_to_Pause)");
//                mBtnPlayPause.setSelected(true);
//            } else {
//                Log.d(TAG, "Music is not playing, Button image: Play(Ready_to_Play)");
//                mBtnPlayPause.setSelected(false);
//            }
//        }
        if (mBtnPlayPause != null) {
            mBtnPlayPause.setSelected(mService.isPlaying());
        }
        if (mBtnShuffle != null) {
            mBtnShuffle.setSelected(mService.isShuffling());
        }
    }

    private void setPlayPauseButtonListener() {
        mBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "PlayPause Button is clicked");
                if (mBtnPlayPause.isSelected() && mService.isPlaying()) { //if image is pause (playing)
                    mBtnPlayPause.setSelected(false);      //change image from pause to play
                    mService.pause();                   //pause
                    Log.d(TAG, "Button image: Pause(Ready_To_Pause) -> Play(Ready_to_Play)");
                } else if (!mBtnPlayPause.isSelected() && !mService.isPlaying()) { //if paused
                    //NOTE: isPlaying() == false doesn't mean that MusicService is paused. MediaPlayer
                    //maybe not prepared. One situation is that next() is called, since currently is
                    //not playing, but asynPrepare() is not called.
                    mBtnPlayPause.setSelected(true);
                    mService.play();
                    //mService.resume();
                    Log.d(TAG, "Button image: Play(Ready_to_Play) -> Pause(Ready_to_Pause)");
                } else {    //handle error here
                    mBtnPlayPause.setSelected(mService.isPlaying());
                    Log.e(TAG, "PlayPause ButtonImageERROR, image has been hanged");
                }
            }
        });
    }

    private void setEndButtonListener() {
        mBtnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "End Button is clicked");
                mService.next();
                sync();
            }
        });
    }

    private void setSkipToStartButtonListener() {
        mBtnSkipToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SkipToStart Button is clicked");
                int played_sec = mService.getCurrentPosition() / 1000;
                if (played_sec < PLAY_PREVIOUS_THRESHOLD_SEC) {
                    mService.previous();
                } else {
                    Log.d(TAG, "Played Sec : " + played_sec + " Skip to Start of Current");
                    mService.skipToStart();
                }
                sync();
            }
        });
    }

    private void setShuffleButtonListener() {
        mBtnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnShuffle.isSelected()) {  //playing,
                    Log.d(TAG, "shuffle -> not shuffle");
                    mBtnShuffle.setSelected(false);
                    mService.shufflePlaylist(false);
                } else {
                    Log.d(TAG, "not shuffle -> shuffle");
                    mBtnShuffle.setSelected(true);
                    mService.shufflePlaylist(true);

                }
            }
        });
    }

    private void setRepeatButtonListener() {
        //TODO: add repeat current one;
        mBtnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Repeat button is clicked");
//                if (mBtnLoop.isSelected() && isRepeatAll && currPlaylist.isRepeat()) {
//                    mBtnLoop.setSelected(false);
//                    isRepeatAll = false;
//                    currPlaylist.resetRepeat();
//                    Log.d("MediaPlayer", "disable repeat playlist when finished");
//                } else if (!mBtnLoop.isSelected() && !isRepeatAll && !currPlaylist.isRepeat()){
//                    mBtnLoop.setSelected(true);
//                    isRepeatAll = true;
//                    currPlaylist.setRepeat();
//                    Log.d("MediaPlayer", "enable repeat playlist when finished");
//                } else {
//                    Log.e("MediaPlayer", "Repeat statues conflicts, reset to false");
//                    mBtnLoop.setSelected(false);
//                    isRepeatAll = false;
//                    currPlaylist.resetRepeat();
//                }
            }
        });
    }

    private void setSeekBarListener() {
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                //long totalDuration = mService.getTotalDuration();
                mService.seekTo(seekBar.getProgress());
                updateProgressBarStart();
            }
        });
    }

    private void setTitle(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    private void setArtist(String artist) {
        if (mArtist != null) {
            mArtist.setText(artist);
        }
    }

    public void sync() {
        if (mBoundStatus && mService != null) {
            MusicFile file = mService.getCurrentMusicFile();
            Log.d(TAG, "sync()");
            if (file != null) {
                //sync title and artist
                setTitle(file.getTitle());
                setArtist(file.getArtist());
                //sync play_pause button
                syncStatus();
                //sync progress bar
                if (mTotalDuration != null && mCurrDuration != null && mProgressBar != null) {
                    updateProgressBarStart();
                }
            }
        } else {
             Log.e(TAG, "Sync failed. Cannot sync unbound controller");
        }
    }

    public void updateProgressBarStart() {
        long total = mService.getTotalDuration();
        mProgressBar.setMax((int) total);
        mTotalDuration.setText(Utils.millSecondsToTime(total));
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC);
    }

    public void stopSync() {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mService.getTotalDuration();
            long currDuration = mService.getCurrentDuration();
            mCurrDuration.setText(Utils.millSecondsToTime(currDuration));
            mProgressBar.setProgress((int) currDuration);

            //frequency of refreshing progress bar depends on total duration of the audio.
            //therefore, if audio is extremely long, refreshing rate will be low.
            int updateFreq = Math.max(DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC,
                        (int) totalDuration / DEFAULT_PROGRESS_BAR_REFRESH_TIMES);
            mHandler.postDelayed(this, updateFreq);
        }
    };

    private void bindLayout(View view) {
        mProgressBar = (SeekBar) view.findViewById(R.id.mediaplayer_controller_seek_bar);
        mCurrDuration = (TextView) view.findViewById(R.id.mediaplayer_controller_label_current_time);
        mTotalDuration = (TextView) view.findViewById(R.id.mediaplayer_controller_label_total_time);
        mTitle = (TextView) view.findViewById(R.id.mediaplayer_controller_label_title);
        mArtist = (TextView) view.findViewById(R.id.mediaplayer_controller_label_artist);
        mBtnPlayPause = (ImageButton) view.findViewById(R.id.mediaplayer_controller_button_play_pause);
        mBtnEnd = (ImageButton) view.findViewById(R.id.mediaplayer_controller_button_end);
        mBtnSkipToStart = (ImageButton) view.findViewById(R.id.mediaplayer_controller_button_skip_to_start);
        mBtnShuffle = (ImageButton) view.findViewById(R.id.mediaplayer_controller_button_shuffle);
        //mBtnLoop = (ImageButton) activity.findViewById(R.id.media_control_repeat);
        printElements();
    }

    /**
     * for debugging. print found elements and missing elements.
     */
    private void printElements() {
        List<String> found = new LinkedList<>();
        List<String> missing = new LinkedList<>();

        String element = "Progress Bar";
        if (mProgressBar != null) found.add(element);
        else missing.add(element);

        element = "Label Current_Duration";
        if (mCurrDuration != null) found.add(element);
        else missing.add(element);

        element = "Label Total_Duration";
        if (mCurrDuration != null) found.add(element);
        else missing.add(element);

        element = "Label Title";
        if (mTitle != null) found.add(element);
        else missing.add(element);

        element = "Label Artist";
        if (mArtist != null) found.add(element);
        else missing.add(element);

        element = "Button Play/Pause";
        if (mBtnPlayPause != null) found.add(element);
        else missing.add(element);

        element = "Button End";
        if (mBtnEnd != null) found.add(element);
        else missing.add(element);

        element = "Button Skip";
        if (mBtnSkipToStart != null) found.add(element);
        else missing.add(element);

        String str = "";
        for (String s : found) {
            str += s;
            str += ", ";
        }
        Log.d(TAG, "Found: " + str);
        str = "";
        for(String s : missing) {
            str += s;
            str += ", ";
        }
        Log.w(TAG, "Missing: " + str);
    }
}