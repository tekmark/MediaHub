package com.example.chao.mediahub;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClockMediaplayerControllerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClockMediaplayerControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClockMediaplayerControllerFragment extends Fragment {
    private static final String TAG = "ClockPlayerCtrlFrag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MediaplayerController mController;
    private OnFragmentInteractionListener mListener;

    private ImageButton mBtnChoosePlaylist;

    public ClockMediaplayerControllerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClockMediaplayerControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClockMediaplayerControllerFragment newInstance(String param1, String param2) {
        ClockMediaplayerControllerFragment fragment = new ClockMediaplayerControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.clock_mediaplayer_bar_controller, container, false);
        mController = MediaplayerController.newInstance(rootView);

        mBtnChoosePlaylist = (ImageButton)rootView.findViewById(R.id.meidaplayer_button_choose_playlist);

        mBtnChoosePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int playlistId = mController.getPlaybackServicePlaylistId();
                Log.d(TAG, "Choose Playlist onClick(), Playlist Id: " + playlistId);
                ClockChoosePlaylistDialog dialog = ClockChoosePlaylistDialog.newInstance(playlistId);
                dialog.show(getActivity().getSupportFragmentManager(), "Choose Playlist");
            }
        });
        return rootView;
    }

    public void bindService(MusicPlaybackService service) {
        if (mController.bindService(service)) {         //success
            MusicFile currentMusicFile = service.getCurrentMusicFile();      //get current file
            if (currentMusicFile != null) {
                Log.d(TAG, "Current File: " + currentMusicFile.toString());
            } else {
                Log.d(TAG, "No Current File");
            }
        } else {
            Log.d(TAG, "Failed to bind service");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnPlaylistChange) {
//            mListener = (OnPlaylistChange) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnPlaylistChange");
//        }
    }

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
        void onFragmentInteraction(Uri uri);
    }

    public void syncMusicServiceStart() {
        Log.d(TAG, "Start to sync Music Service");
        mController.sync();
    }
}
