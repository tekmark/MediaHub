package com.example.chao.mediahub;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaplayerControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MediaplayerControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaplayerControllerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final static private String TAG = "MediaPlayerController";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    private ImageButton mPlayPause;
    private ImageButton mEnd;
    private ImageButton mSkipToStart;
    private ImageButton mShuffle;
    private ImageButton mRepeat;

    private SeekBar mProgressBar;
    private TextView mCurrDuration;
    private TextView mTotalDuration;

    private TextView mTitle;
    private TextView mArtist;

    private MusicPlaybackService mService;
    private boolean bound;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MediaplayerControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MediaplayerControllerFragment newInstance(String param1, String param2) {
        MediaplayerControllerFragment fragment = new MediaplayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MediaplayerControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mService = null;
        bound = false;

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreate() is called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView() is called");
        View rootView = inflater.inflate(R.layout.mediaplayer_controller, container, false);
        //bind layout.
        bindLayout(rootView);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

//    bind controller's layout to service, and set listeners
//    public boolean bindService(MusicPlaybackService musicService) {
//        if (musicService == null) {
//            mService = null;
//            bound = false;
//            Log.w(TAG, "Try to bind to a invalid music service");
//            return false;
//        } else {
//            mService = musicService;
//            bound = true;
//            setListeners();
//            Log.d(TAG, "Music service is bound");
//            return true;
//        }
//    }

    public boolean isBound() {
        return bound;
    }

    private void bindLayout(View rootView) {
        mPlayPause = (ImageButton) rootView.findViewById(R.id.mediaplayer_controller_button_play_pause);
        if (mPlayPause != null) {
            Log.d(TAG, "Button Play/Pause is found");
        } else {
            Log.w(TAG, "Button Play/Pause isn't found");
        }
        mEnd = (ImageButton) rootView.findViewById(R.id.mediaplayer_controller_button_end);
        if (mEnd != null) {
            Log.d(TAG, "Button End is found");
        } else {
            Log.w(TAG, "Button End isn't found");
        }
        mSkipToStart = (ImageButton) rootView.findViewById(R.id.mediaplayer_controller_button_skip_to_start);
        if (mSkipToStart != null) {
            Log.d(TAG, "Button Skip_to_Start is found");
        } else {
            Log.w(TAG, "Button Skip_to_Start isn't found");
        }
        //mShuffle = (ImageButton) activity.findViewById(R.id.button_shuffle);
        //mRepeat = (ImageButton) activity.findViewById(R.id.media_control_repeat);
        mProgressBar = (SeekBar) rootView.findViewById(R.id.mediaplayer_controller_seek_bar);
        if (mProgressBar != null) {
            Log.d(TAG, "Progress Bar is found");
        } else {
            Log.w(TAG, "Progress Bar isn't found");
        }
        mCurrDuration = (TextView) rootView.findViewById(R.id.mediaplayer_controller_label_current_time);
        if (mCurrDuration != null) {
            Log.d(TAG, "Label Current_Duration is found");
        } else {
            Log.w(TAG, "Label Current_Duration End isn't found");
        }
        mTotalDuration = (TextView) rootView.findViewById(R.id.mediaplayer_controller_label_total_time);
        if (mCurrDuration != null) {
            Log.d(TAG, "Label Total_Duration is found");
        } else {
            Log.w(TAG, "Label Total_Duration isn't found");
        }
        mTitle = (TextView) rootView.findViewById(R.id.mediaplayer_controller_label_title);
        if (mTitle != null) {
            Log.d(TAG, "Label Title is found");
        } else {
            Log.w(TAG, "Label Title isn't found");
        }
        mArtist = (TextView) rootView.findViewById(R.id.mediaplayer_controller_label_artist);
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
    };

    private void setPlayPauseButtonListner() {

    }

    private void setEndButtonListener() {

    }

    private void setSkipToStartButtonListener() {

    }

    private void setShuffleButtonListener() {

    }

    private void setRepeatButtonListener() {

    }

    private void setSeekBarListener() {

    }
}
