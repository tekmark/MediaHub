package com.example.chao.mediahub;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by chaohan on 10/28/15.
 */
public class MediaPlayerController {
    final int PLAY_PREVIOUS_THRESHOLD_SEC = 10;
    final int DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC = 200;
    final int DEFAULT_PROGRESS_BAR_REFRESH_TIMES = 500;


    final static private String TAG = "MediaPlayerController";

    private ImageButton mPlayPause;
    private ImageButton mEnd;
    private ImageButton mSkipToStart;
    private ImageButton mShuffle;
    private ImageButton mRepeat;

    private SeekBar mProgressBar;
    private TextView mCurrDuration;
    private TextView mTotalDuration;

    private boolean bound;

    private TextView mTitle;
    private TextView mArtist;
    //private int currSongIndex;
    //private int defaultSongIndex;

    //private boolean isShuffle = false;
    //private boolean isRepeat = false;
    //private boolean isRepeatAll = false;

    private MusicPlaybackService mService;

    private Handler mHandler = new Handler();

    private MediaPlayerController(Activity activity) {
        bound = false;
        bindLayout(activity);
    }

    public static MediaPlayerController newInstance(Activity activity) {
        MediaPlayerController controller = new MediaPlayerController(activity);
        return controller;
    }


    //bind controller's layout to service, and set listeners
    public boolean bindService(MusicPlaybackService musicService) {
        if (musicService == null) {
            bound = false;
            Log.w(TAG, "Try to bind to a invalid music service");
            return false;
        } else {
            mService = musicService;
            bound = true;
            setListeners();
            Log.d(TAG, "Music service is bound");
            //syncStatus();
            return true;
        }
    }

    public boolean isBound() {
        return bound;
    }

    public void sync() {
        Log.d(TAG, "sync() is called");
        if (!bound) {
            Log.w(TAG, "Sync failed. Cannot sync unbound controller");
            return;
        }
        if (mPlayPause != null) {
            if (mService.isPlaying()) {
                mPlayPause.setSelected(true);
            } else {
                mPlayPause.setSelected(false);
            }
        }
        if (mProgressBar != null) {
            refreshProgressBar();
        }
        if (mArtist != null) {
            String artist = mService.getCurrentArtist();
            mArtist.setText(artist);
        }
        if (mTitle != null) {
            String title = mService.getCurrentTitle();
            mTitle.setText(title);
        }
        if (mTotalDuration != null) {
            long total = mService.getDuration();
            mTotalDuration.setText(Utilities.millSecondsToTime(total));
        }
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
            Log.w(TAG, "Progress bar may be missing in layout");
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
            Log.w(TAG, "Progress bar is not bound or missing in layout");
        }
    }

    private void bindLayout(Activity activity) {
        mPlayPause = (ImageButton) activity.findViewById(R.id.mediaplayer_controller_button_play_pause);
        if (mPlayPause != null) {
            Log.d(TAG, "Button Play/Pause is found");
        } else {
            Log.w(TAG, "Button Play/Pause isn't found");
        }
        mEnd = (ImageButton) activity.findViewById(R.id.mediaplayer_controller_button_end);
        if (mEnd != null) {
            Log.d(TAG, "Button End is found");
        } else {
            Log.w(TAG, "Button End isn't found");
        }
        mSkipToStart = (ImageButton) activity.findViewById(R.id.mediaplayer_controller_button_skip_to_start);
        if (mSkipToStart != null) {
            Log.d(TAG, "Button Skip_to_Start is found");
        } else {
            Log.w(TAG, "Button Skip_to_Start isn't found");
        }
        //mShuffle = (ImageButton) activity.findViewById(R.id.button_shuffle);
        //mRepeat = (ImageButton) activity.findViewById(R.id.media_control_repeat);
        mProgressBar = (SeekBar) activity.findViewById(R.id.mediaplayer_controller_seek_bar);
        if (mProgressBar != null) {
            Log.d(TAG, "Progress Bar is found");
        } else {
            Log.w(TAG, "Progress Bar isn't found");
        }
        mCurrDuration = (TextView) activity.findViewById(R.id.mediaplayer_controller_label_current_time);
        if (mCurrDuration != null) {
            Log.d(TAG, "Label Current_Duration is found");
        } else {
            Log.w(TAG, "Label Current_Duration End isn't found");
        }
        mTotalDuration = (TextView) activity.findViewById(R.id.mediaplayer_controller_label_total_time);
        if (mCurrDuration != null) {
            Log.d(TAG, "Label Total_Duration is found");
        } else {
            Log.w(TAG, "Label Total_Duration isn't found");
        }
        mTitle = (TextView) activity.findViewById(R.id.mediaplayer_controller_label_title);
        if (mTitle != null) {
            Log.d(TAG, "Label Title is found");
        } else {
            Log.w(TAG, "Label Title isn't found");
        }
        mArtist = (TextView) activity.findViewById(R.id.mediaplayer_controller_label_artist);
        if (mArtist != null) {
            Log.d(TAG, "Label Artist is found");
        } else {
            Log.w(TAG, "Label Artist isn't found");
        }
    }

    private void setListeners() {
        if (mPlayPause != null) setPlayPauseButtonListner();
        if (mEnd != null) setEndButtonListener();
        if (mSkipToStart != null) setSkipToStartButtonListener();
        if (mShuffle != null) setShuffleButtonListener();
        if (mRepeat != null) setRepeatButtonListener();
        if (mProgressBar != null) setSeekBarListener();
    }

    private void setPlayPauseButtonListner() {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "PlayPause Button is clicked");
                if (mPlayPause.isSelected() && mService.isPlaying()) { //if image is pause (playing)
                    mPlayPause.setSelected(false);      //change image from pause to play
                    mService.pause();                   //pause
                    Log.d(TAG, "Button image: Pause(Ready_To_Pause) -> Play(Ready_to_Play)");
                } else if (!mPlayPause.isSelected() && !mService.isPlaying()) { //if paused
                    //NOTE: isPlaying() == false doesn't mean that MusicService is paused. MediaPlayer
                    //maybe not prepared. One situation is that next() is called, since currently is
                    //not playing, but asynPrepare() is not called.
                    mPlayPause.setSelected(true);
                    mService.play();
                    Log.d(TAG, "Button image: Play(Ready_to_Play) -> Pause(Ready_to_Pause)");
                } else {    //handle error here
                    mPlayPause.setSelected(mService.isPlaying());
                    Log.e(TAG, "PlayPause ButtonImageERROR, image has been hanged");
                }
            }
        });
    }

    private void setEndButtonListener() {
        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "End Button is clicked");
                mService.next();
            }
        });
    }

    private void setSkipToStartButtonListener() {
        mSkipToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SkipToStart Button is clicked");
                int played_sec = mService.getCurrentPosition() / 1000;
                if (played_sec < PLAY_PREVIOUS_THRESHOLD_SEC) {
//                    mMediaPlayer.reset();
//                    if (preparePrevSong()) {
//                        Log.d("MediaPlayer", "move to previous song in Playlist");
//                        if (mPlayPause.isSelected()) {
//                            play();
//                        }
//                    } else {
//                        Log.d("MediaPlayer", "Fail to find previous song in Playlist");
//                    }
                    mService.previous();
                } else {
                    Log.d(TAG, "Played Sec : " + played_sec + " Skip to Start of Current");
                    mService.skipToStart();
                }
            }
        });
    }

    private void setShuffleButtonListener() {
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Shuffle button is clicked");
                if (mShuffle.isSelected()) {  //playing,
                    mShuffle.setSelected(false);
                } else {
                    mShuffle.setSelected(true);

                }
            }
        });
    }

    private void setRepeatButtonListener() {
        //TODO: add repeat current one;
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Repeat button is clicked");
//                if (mRepeat.isSelected() && isRepeatAll && currPlaylist.isRepeat()) {
//                    mRepeat.setSelected(false);
//                    isRepeatAll = false;
//                    currPlaylist.resetRepeat();
//                    Log.d("MediaPlayer", "disable repeat playlist when finished");
//                } else if (!mRepeat.isSelected() && !isRepeatAll && !currPlaylist.isRepeat()){
//                    mRepeat.setSelected(true);
//                    isRepeatAll = true;
//                    currPlaylist.setRepeat();
//                    Log.d("MediaPlayer", "enable repeat playlist when finished");
//                } else {
//                    Log.e("MediaPlayer", "Repeat statues conflicts, reset to false");
//                    mRepeat.setSelected(false);
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
                long totalDuration = mService.getDuration();
                //int currPosition = (int) (seekBar.getProgress() * totalDuration / 100);
                mService.seekTo(seekBar.getProgress());
                //play();
                refreshProgressBar();
            }
        });
    }

    public void refreshProgressBar() {
        //TODO: update getProgress() in MusicService
        long total = mService.getDuration();
        mProgressBar.setMax((int) total);

        //mTotalDuration.setText(MediaPlayerUtils.millSecondsToTime(total));
        if (mTotalDuration != null) {
            mTotalDuration.setText(Utilities.millSecondsToTime(total));
        }
        mHandler.postDelayed(mUpdateTimeTask, DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC);
    }

    public void stopRefreshProgressBar() {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (mService.isPlaying()) {
                long totalDuration = mService.getDuration();
                long currDuration = mService.getCurrentDuration();

                mCurrDuration.setText(Utilities.millSecondsToTime(currDuration));
                mTotalDuration.setText(Utilities.millSecondsToTime(totalDuration));

                mProgressBar.setProgress((int) currDuration);

                //frequency of refreshing progress bar depends on total duration of the audio.
                //therefore, if audio is extremely long, refreshing rate will be low.
                int updateFreq = Math.max(DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC,
                        (int) totalDuration / DEFAULT_PROGRESS_BAR_REFRESH_TIMES);
                mHandler.postDelayed(this, updateFreq);
            } else {
                if (mPlayPause != null && !mPlayPause.isSelected()) {
                    mPlayPause.setSelected(false);
                }
                mHandler.postDelayed(this, DEFAULT_PROGRESS_BAR_REFRESH_PERIOD_MSEC);
            }
        }
    };
}