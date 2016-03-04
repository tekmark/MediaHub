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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String AGR_AUDIO_ID = Tags.Arguments.AGR_AUDIO_ID;
    private int mAudioId;

    private Button mBtnAddToPlaylist;
    private Button mBtnCancel;

    private OnInteractionListener mListener;

    public MusicFileOptionsDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param audioId audio id of the selected item.
     * @return A new instance of fragment MusicFileOptionsDialog.
     */
    public static MusicFileOptionsDialog newInstance(int audioId) {
        MusicFileOptionsDialog fragment = new MusicFileOptionsDialog();
        Bundle args = new Bundle();
        args.putInt(AGR_AUDIO_ID, audioId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        Log.d(TAG, "Update Audio Id: " + mAudioId + " -> " + audioId);
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
