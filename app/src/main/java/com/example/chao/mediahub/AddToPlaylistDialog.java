package com.example.chao.mediahub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnInteractionListener}
 * interface.
 */

//TODO: disable back ground interactions.
public class AddToPlaylistDialog extends Fragment {
    private static final String TAG = "AddToPlaylistDialog";

    //parameter arguments
    private static final String ARG_AUDIO_ID = Tags.Arguments.AGR_AUDIO_ID;

    private int mAudioId = MusicFile.INVALID_ID;

    private Button mBtnNewPlaylist;
    private Button mBtnCancel;

    private OnInteractionListener mListener;

    public static AddToPlaylistDialog newInstance(int audioId) {
        AddToPlaylistDialog fragment = new AddToPlaylistDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_AUDIO_ID, audioId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddToPlaylistDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAudioId = getArguments().getInt(ARG_AUDIO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addtoplaylist_list, container, false);
        mBtnNewPlaylist = (Button) rootView.findViewById(R.id.button_new_playlist);
        mBtnCancel = (Button) rootView.findViewById(R.id.button_cancel);

        View view = rootView.findViewById(R.id.list);
//        Log.d(TAG, "onCreateView");
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            List<Playlist> playlists = MediaManager.getAllPlaylists(context);

            AddToPlaylistRecyclerViewAdapter adapter = new AddToPlaylistRecyclerViewAdapter(playlists, mAudioId, mListener);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Cannot get RecyclerView");
        }
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
        void playlistOnSelect(int audioId, Playlist playlist);
    }

    public void removeDialog(FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.remove(this);
        ft.commit();
    }
    public void removeThisDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    private void setListeners() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "BtnCancelOnClick()");
//                removeDialog(getActivity().getSupportFragmentManager(), TAG);
            removeThisDialog();
            }
        });
        mBtnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "BtnNewPlaylistOnClick");
                //
                Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
                startActivity(intent);
            }
        });
    }
}
