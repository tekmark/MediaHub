package com.example.chao.mediahub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistTopControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistTopControllerFragment extends Fragment {
    private static final String TAG = "PlaylistTopCtrlFrag";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mBtnPlayAll;
    private ImageButton mBtnShuffle;
    private ImageButton mBtnRepeat;
    private ImageButton mBtnEditList;

    private EventListener mListener;

    public PlaylistTopControllerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistTopControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistTopControllerFragment newInstance(String param1, String param2) {
        PlaylistTopControllerFragment fragment = new PlaylistTopControllerFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_playlist_top_controller, container, false);

        mBtnPlayAll = rootView.findViewById(R.id.playlist_top_bar_play_all);
        mBtnShuffle = (ImageButton) rootView.findViewById(R.id.playlist_top_bar_button_shuffle);
        mBtnRepeat = (ImageButton) rootView.findViewById(R.id.playlist_top_bar_button_repeat);
        mBtnEditList = (ImageButton) rootView.findViewById(R.id.playlist_top_bar_button_edit);
        setListeners();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListener) {
            mListener = (EventListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EventListener");
        }
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
    public interface EventListener {
        // TODO: Update argument type and name
        void playAll();
    }

    private void setListeners() {
        mBtnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Shuffle Button onClick()");
            }
        });

        mBtnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Play All onClick()");
                mListener.playAll();
            }
        });
    }

}
