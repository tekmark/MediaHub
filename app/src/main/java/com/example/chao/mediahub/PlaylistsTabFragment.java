package com.example.chao.mediahub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsTabFragment extends Fragment {

    final static private String TAG = "PlaylistsTabFragment";
    final static public String EXTRA_AGR_PLAYLIST_ID = "PlaylistsTabFragment.PLAYLIST_ID";
    final static public String EXTRA_AGR_PLAYLIST_NAME = "PlaylistsTabFragment.PLAYLIST_NAME";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnInteractionListener mListener;

    private RecyclerView mPlaylistsRecyclerView;
    private PlaylistsAdapter mAdapter;

    private Button mBtnNewPlaylist;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistsTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistsTabFragment newInstance(String param1, String param2) {
        PlaylistsTabFragment fragment = new PlaylistsTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistsTabFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_playlists_tab, container, false);
        mBtnNewPlaylist = (Button) rootView.findViewById(R.id.button_new_playlist);
        mBtnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Create a new playlist");
                createPlaylist();
            }
        });
        mPlaylistsRecyclerView = (RecyclerView) rootView.findViewById(R.id.playlists_recycler_view);
        mPlaylistsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return rootView;
    }

//    TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    private void createPlaylist() {
        Intent intent = new Intent(getContext(), CreatePlaylistActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_no_anim);
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
        try {
            mListener = (OnInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
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
        void playlistOptionsOnClick(int playlistId);
        void playlistOnClick(int playlistId, String playlistName);
    }

    public void updateUI() {
        Log.d(TAG, "update UI");
//        List<Playlist> list = MediaManager.getAllPlaylists(getActivity().getApplicationContext());
        List<Playlist> list = MediaManager.getAllPlaylists(getContext());
        mAdapter = new PlaylistsAdapter(list);
        mPlaylistsRecyclerView.setAdapter(mAdapter);
        Log.d(TAG, "UI updated");
    }

    private class PlaylistsItemHolder extends RecyclerView.ViewHolder {

        public TextView mLabelPlaylistName;
        public TextView mLabelPlaylistSize;
        public ImageButton mBtnMoreOptions;
        public Playlist mPlaylist;

        public PlaylistsItemHolder(View itemView) {
            super(itemView);

            mLabelPlaylistName = (TextView) itemView.findViewById(R.id.playlist_info_name);
            mLabelPlaylistSize = (TextView) itemView.findViewById(R.id.playlist_info_size);
            mBtnMoreOptions = (ImageButton) itemView.findViewById(R.id.playlist_more_options);
            mBtnMoreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick() - more options. Playlist Id : " + mPlaylist.getId() +
                    " Name " + mPlaylist.getName());
                    mListener.playlistOptionsOnClick(mPlaylist.getId());
                }
            });
            //itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "OnClick() - Playlist info. Playlist Id: " + mPlaylist.getId() +
                            " Name: " + mPlaylist.getName());
                    mListener.playlistOnClick(mPlaylist.getId(), mPlaylist.getName());
                }
            });
        }

//        @Override
//        public void onClick(View v) {
//            Log.d(TAG, "OnClick() - Playlist info. Playlist Id: " + mPlaylist.getId() +
//                    " Name: " + mPlaylist.getName());
//            Intent intent = new Intent(getActivity(), PlaylistActivity.class);
//            intent.putExtra(EXTRA_AGR_PLAYLIST_ID, mPlaylist.getId());
//            intent.putExtra(EXTRA_AGR_PLAYLIST_NAME, mPlaylist.getName());
//            startActivity(intent);
//            mPlaylist.playlistOnClick(mPlaylist.getId());
//        }
    }

    private class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsItemHolder> {
        private List<Playlist> mPlaylists;
        public PlaylistsAdapter(List<Playlist> playlists) {
            mPlaylists = playlists;
        }
        @Override
        public PlaylistsItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.playlists_item_entry, parent, false);
            return new PlaylistsItemHolder(view);
        }
        @Override
        public void onBindViewHolder(PlaylistsItemHolder holder, int position) {
            Playlist playlist = mPlaylists.get(position);
            holder.mPlaylist = playlist;
//            Log.d(TAG, "onBindViewHolder(): name: " + playlist.getName() + " id: " + playlist.getId());
            String playlistSize = String.valueOf(playlist.getSize());
            holder.mLabelPlaylistSize.setText(playlistSize);
            holder.mLabelPlaylistName.setText(playlist.getName());
        }

        @Override
        public int getItemCount() {
            return mPlaylists.size();
        }
    }
}
