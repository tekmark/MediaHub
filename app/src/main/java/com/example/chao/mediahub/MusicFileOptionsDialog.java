package com.example.chao.mediahub;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicFileOptionsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFileOptionsDialog extends Fragment {

    private static final String TAG = "MusicFileOptionsFrag";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mBtnAddToPlaylist;
    private Button mBtnCancel;

    private int mAudioId;

    private OnInteractionListener mListener;

    public MusicFileOptionsDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFileOptionsDialog.
     */
    // TODO: Rename and change types and number of parameters
//    public static MusicFileOptionsDialog newInstance(String param1, String param2) {
//        MusicFileOptionsDialog fragment = new MusicFileOptionsDialog();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static MusicFileOptionsDialog newInstance(int audioId) {
        MusicFileOptionsDialog fragment = new MusicFileOptionsDialog();
        Bundle args = new Bundle();
        args.putInt(Tags.Arguments.AGR_AUDIO_ID, audioId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            if (mParam1 != null) {
//                mAudioId = Integer.parseInt(mParam1);
//            }
//            mParam2 = getArguments().getString(ARG_PARAM2);
            mAudioId = getArguments().getInt(Tags.Arguments.AGR_AUDIO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_music_file_options, container, false);
        mBtnAddToPlaylist = (Button) rootView.findViewById(R.id.more_options_button_add_to_playlist);
        mBtnCancel = (Button) rootView.findViewById(R.id.more_options_button_cancel);
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
        if (context instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public int getCurrentAudioId() {
        return mAudioId;
    }

    public void updateAudioId(int audioId) {
        mAudioId = audioId;
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
    public interface OnInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
        void onDialogClose(String Tag);
        void onMusicFileOptionsAddToPlaylist(int audioId);
    }



    private void setListeners() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = MusicFileOptionsDialog.this.getTag();
                mListener.onDialogClose(tag);
            }
        });

        mBtnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button AddToPlaylist onClick(), mAudioId : " + mAudioId);
                mListener.onMusicFileOptionsAddToPlaylist(mAudioId);
            }
        });
    }
}
