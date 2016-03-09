package com.example.chao.mediahub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MediaplayerControllerFragment extends Fragment {

    private static final String TAG = "PlayerControllerFrag";

    private static final String ARG_ATTACHED_ACTIVITY_TAG = "ArgAttachedActivityTag";

    private String mAttachedActivityTag;

    private EnventListener mListener;

    private MediaplayerController mController;

    private MusicPlaybackService mService;
    private boolean bound;

    public MediaplayerControllerFragment() {
        mService = null;
        bound = false;
    }

    //TODO: delete this factory method.
    public static MediaplayerControllerFragment newInstance(String param1, String param2) {
        MediaplayerControllerFragment fragment = new MediaplayerControllerFragment();
        Bundle args = new Bundle();
        //args.putString(TAG_ATTACHED_ACTIVITY, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static MediaplayerControllerFragment newInstance(String tag) {
        MediaplayerControllerFragment fragment = new MediaplayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ATTACHED_ACTIVITY_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAttachedActivityTag = getArguments().getString(ARG_ATTACHED_ACTIVITY_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView() is called");
        View rootView = inflater.inflate(R.layout.fragment_playing_mediaplayer_controller, container, false);

        //create controller and bind layout.
        mController = MediaplayerController.newInstance(rootView);

        return rootView;
    }

    //TODO: define interfaces.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() to " + mAttachedActivityTag);
//        if (context instanceof EnventListener) {
//            mListener = (EnventListener) context;
//        } else {
//            throw new ClassCastException(context.toString()
//                    + " must implement OnInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach() from " + mAttachedActivityTag);
    }

    public boolean bindService(MusicPlaybackService service) {
        return mController.bindService(service);
    }

    public void syncMusicServiceStart() {
        Log.d(TAG, "Start to sync Music Service");
        mController.sync();
    }

    public void syncMusicServicePause() {

    }

    public void syncMusicServiceStop() {

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
    public interface EnventListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}