package com.example.chao.mediahub;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chao.mediahub.AddToPlaylistDialog.OnInteractionListener;
import com.example.chao.mediahub.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnInteractionListener}.
 */

public class AddToPlaylistRecyclerViewAdapter extends
        RecyclerView.Adapter<AddToPlaylistRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "AddToPlaylistAdapter";

    private final List<Playlist> mPlaylists;
    private final int mAudioId;
    private final OnInteractionListener mListener;

    public AddToPlaylistRecyclerViewAdapter(List<Playlist> playlists, int audioId, OnInteractionListener listener) {
        mPlaylists = playlists;
        mAudioId = audioId;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Playlist playlist = mPlaylists.get(position);

        holder.mPlaylist = playlist;
        holder.mPlaylistName.setText(playlist.getName());
        String playlistSize = Integer.toString(playlist.getSize());
        holder.mPlaylistSize.setText(playlistSize);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Playlist OnClick(): " + playlist.getName());
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onClickPlaylist(playlist);
                    mListener.playlistOnSelect(mAudioId, playlist);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPlaylistName;
        public final TextView mPlaylistSize;
        public Playlist mPlaylist;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mPlaylistName = (TextView) mView.findViewById(R.id.playlist_info_name);
            mPlaylistSize = (TextView) mView.findViewById(R.id.playlist_info_size);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }

}
