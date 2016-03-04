package com.example.chao.mediahub;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistOptionsDialog.OnInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistOptionsDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistOptionsDialog extends Fragment {

    final static private String TAG = "PlaylistOptionsDialog";

    // the fragment initialization parameters
    private static final String ARG_PLAYLIST_ID = Tags.Arguments.AGR_PLAYLIST_ID;
    //parameters
    private int mPlaylistId;

    private OnInteractionListener mListener;

    private Button mBtnModify;
    private Button mBtnDelete;
    private Button mBtnCancel;
    private Button mBtnSticky;

    public PlaylistOptionsDialog() {
        // Required empty public constructor
    }

    /**
     * Create a new instance of fragment PlaylistOptionsDialog.
     * @param playlistId playlistId of the selected item.
     * @return A new instance of fragment PlaylistOptionsDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistOptionsDialog newInstance(int playlistId) {
        PlaylistOptionsDialog fragment = new PlaylistOptionsDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlaylistId = getArguments().getInt(ARG_PLAYLIST_ID);
//            Log.d(TAG,  "Parameter " + ARG_PLAYLIST_ID + " = " + mPlaylistId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playlist_mgmt, container, false);
        mBtnModify = (Button) rootView.findViewById(R.id.playlist_mgmt_button_modify);
        mBtnDelete = (Button) rootView.findViewById(R.id.playlist_mgmt_button_delete);
        mBtnCancel = (Button) rootView.findViewById(R.id.button_cancel);
        mBtnSticky = (Button) rootView.findViewById(R.id.playlist_mgmt_button_sticky);

        //set button's listeners.
        if (mBtnCancel != null && mBtnDelete != null && mBtnCancel != null && mBtnSticky != null) {
            setListeners();
        }
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach()");
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
        Log.d(TAG, "onDetach()");
        super.onDetach();
        mListener = null;
    }

    public void updatePlaylistId(int playlistId) {
        mPlaylistId = playlistId;
    }
    public int getCurrentPlaylistId() {
        return mPlaylistId;
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
        void onPlaylistDelete(int playlistId);
        void onDialogClose(String tag);
    }

    private void setListeners() {
        mBtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Modify Button onClick()");
                //close this dialog.
                String myTag = PlaylistOptionsDialog.this.getTag();
                mListener.onDialogClose(myTag);
                //start CreatePlaylistActivity, and pass mPlaylistId to it.
                Intent intent = new Intent(getContext(), CreatePlaylistActivity.class);
                intent.putExtra(Tags.AGR_PLAYLIST_ID, mPlaylistId);
                startActivity(intent);
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel Button onClick()");
                String myTag = PlaylistOptionsDialog.this.getTag();
                mListener.onDialogClose(myTag);
            }
        });

        mBtnSticky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sticky Button onClick()");
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Delete Button onClick(), mPlaylistId : " + mPlaylistId);
                mListener.onPlaylistDelete(mPlaylistId);
            }
        });
    }
/*
    public void showDialog(FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.main_content, this, TAG);
        ft.commit();
    }

    public void removeDialog(FragmentManager fragmentManager) {
        Log.d(TAG, "remove dialog input tag: ");
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public void deletePlaylist() {
        MediaManager.deletePlaylist(getContext(), mPlaylistId);
    }*/
}
