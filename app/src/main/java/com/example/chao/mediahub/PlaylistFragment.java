package com.example.chao.mediahub;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaylistFragment extends Fragment {

    final private static String TAG = "PlaylistFragment";
    final private static String ARG_PLAYLIST_ID = Tags.Arguments.AGR_PLAYLIST_ID;

    private EventListener mListener;

    private RecyclerView mPlaylistRecyclerView;
    private PlaylistAdapter mAdapter;

    private int mPlaylistId;
    List<MusicFile> mPlaylist;

    public interface EventListener {
        void itemPositionOnClick(int position);
        void musicFileOnClick(int audioId);
        //public void syncPlayingList(List<MusicFile> list);
    }

    private int mCurrentHighlightedPosition = -1;

    public PlaylistFragment() {
    }

    public static PlaylistFragment newInstance(int playlistId) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PLAYLIST_ID, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() is called");
        if (getArguments() != null) {
            mPlaylistId = getArguments().getInt(ARG_PLAYLIST_ID);
            Log.d(TAG, "Playlist Id is set to " + mPlaylistId);
        } else {
            Log.d(TAG, "# of Arg is 0");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView is called");
        //return inflater.inflate(R.layout.fragment_playlist, container, false);
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

        mPlaylistRecyclerView = (RecyclerView) rootView.findViewById(R.id.playlist_entries_recycler_view);
        mPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return rootView;
    }

    public List<MusicFile> getPlaylist() {
        return mPlaylist;
    }



    public void setPositionHighlighted(int position) {
        if (position == mCurrentHighlightedPosition) {
            Log.d(TAG, "Position : " + position + " is already highlighted");
        } else {
            //reset current highlighted position
            PlaylistItemHolder holder = (PlaylistItemHolder)
                    mPlaylistRecyclerView.findViewHolderForAdapterPosition(mCurrentHighlightedPosition);
            if (holder != null) {
                Log.d(TAG, "remove highlight at Position: " + mCurrentHighlightedPosition);
                holder.resetHighlight();
            }
            if (position != -1) {
                int count = mPlaylistRecyclerView.getAdapter().getItemCount();
                Log.d(TAG, "item count: " + count);
                holder = (PlaylistItemHolder) mPlaylistRecyclerView.findViewHolderForAdapterPosition(
                        position);
                if (holder != null) {
                    Log.d(TAG, "highlight position: " + position);
                    holder.setHighlight();
                    mCurrentHighlightedPosition = position;
                } else {
                    Log.d(TAG, "hold is null. pos : " + position);
                }
            } else {
                mCurrentHighlightedPosition = -1;
            }
        }
    }

    public void clearHighlighted() {
        setPositionHighlighted(-1);
    }

    public MusicFile getMusicFile(int position) {
        MusicFile file = mPlaylist.get(position);
        return file;
    }

    public List<MusicFile> getMusicFiles() {
        return mPlaylist;
    }

    public void updateUI() {
        Log.d(TAG, "Update UI");
        mPlaylist = MediaManager.getPlaylistMusicFiles(getContext(), mPlaylistId);
        if (mAdapter == null) {
            mAdapter = new PlaylistAdapter(mPlaylist, mCurrentHighlightedPosition);
            mPlaylistRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter = new PlaylistAdapter(mPlaylist, mCurrentHighlightedPosition);
            mPlaylistRecyclerView.setAdapter(mAdapter);
            //mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "UI updated");
    }

    private class PlaylistItemHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mArtist;
        private TextView mDuration;
        private TextView mPosition;
        private ImageButton mButton;
        private MusicFile mMusicFile;

        private boolean isHighlighted;

        public PlaylistItemHolder(View itemView) {
            super(itemView);
            isHighlighted = false;
            //Music File info
            mTitle = (TextView)itemView.findViewById(R.id.music_file_info_title);
            mArtist = (TextView) itemView.findViewById(R.id.music_file_info_artist_album);
            mDuration = (TextView) itemView.findViewById(R.id.music_file_info_duration);

            mPosition = (TextView) itemView.findViewById(R.id.playlist_entry_label_position);
            mButton = (ImageButton) itemView.findViewById(R.id.playlist_entry_button_more);
        }

        public void bind(MusicFile file, final int position) {
            mMusicFile = file;
            mTitle.setText(mMusicFile.getTitle());
            mArtist.setText(mMusicFile.getArtist());
            mDuration.setText(Utils.millSecondsToTime(mMusicFile.getDuration()));
            mPosition.setText(String.valueOf(position));
            View view = itemView.findViewById(R.id.playlist_music_file_info);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.selectPlaybackServicePosition(1);
                    //int position = Integer.parseInt(mPosition.getText().toString());
                    Log.d(TAG, "itemPositionOnClick -> Position : " + position + " Title : " + mMusicFile.getTitle());
                    //mListener.itemPositionOnClick(position);
//                    Intent intent = new Intent(getContext(), PlayingActivity.class);
//                    startActivity(intent);
                    mListener.musicFileOnClick(position);
                }
            });
        }

        public void setHighlight() {
            if (!isHighlighted) {
                mTitle.setTypeface(null, Typeface.BOLD_ITALIC);
                mArtist.setTypeface(null, Typeface.ITALIC);
                isHighlighted = true;
            } else {
                Log.w(TAG, "already highlighted");
            }
        }

        public void resetHighlight() {
            if (isHighlighted) {
                mTitle.setTypeface(null, Typeface.NORMAL);
                mArtist.setTypeface(null, Typeface.NORMAL);
                isHighlighted = false;
            } else {
                Log.w(TAG, "not highlighted");
            }
        }
    }

    private class PlaylistAdapter extends RecyclerView.Adapter<PlaylistItemHolder> {
        private List<MusicFile> mMusicFiles;
        private int mHighlightedPosition;
        public PlaylistAdapter(List<MusicFile> files) {
            mMusicFiles = files;
            mHighlightedPosition = RecyclerView.NO_POSITION;
        }

        public PlaylistAdapter(List<MusicFile> files, int highlightedPosition) {
            mMusicFiles = files;
            mHighlightedPosition = highlightedPosition;
        }

        @Override
        public PlaylistItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.playlist_activity_item, parent, false);
            return new PlaylistItemHolder(view);
        }
        @Override
        public void onBindViewHolder(PlaylistItemHolder holder, int position) {
            MusicFile file = mMusicFiles.get(position);
            holder.bind(file, position);
            if (mHighlightedPosition == position) {
                holder.setHighlight();
            }
        }

        @Override
        public int getItemCount() {
            return mMusicFiles.size();
        }

        public void setHighlighted(int position) {

        }
    }
}
