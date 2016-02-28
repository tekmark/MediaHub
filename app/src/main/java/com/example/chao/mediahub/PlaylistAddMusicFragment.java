package com.example.chao.mediahub;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */

public class PlaylistAddMusicFragment extends Fragment {

    final static String TAG = "PlaylistAddMusicFrag";

    //full list
    private List<MusicFile> mMusicFiles;

    //selected music files - status_added files
    private List<MusicFile> mAddedMusicFiles;


    private RecyclerView mAddMusicList;
    private AddMusicListAdapter mAdapter;

    //constructor
    public PlaylistAddMusicFragment() {
        mAddedMusicFiles = new ArrayList<>();
        mMusicFiles = new ArrayList<>();
    }

    public static PlaylistAddMusicFragment newInstance() {
        Bundle args = new Bundle();
        PlaylistAddMusicFragment fragment = new PlaylistAddMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PlaylistAddMusicFragment newInstance(List<MusicFile> list) {
        Bundle args = new Bundle();
        PlaylistAddMusicFragment fragment = new PlaylistAddMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_add_music, container, false);
        mAddMusicList = (RecyclerView) rootView.findViewById(R.id.add_music_list);
        mAddMusicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void loadAddedMusicFiles (List<MusicFile> addedlist) {
        Log.d(TAG, "load Added Music Files, # of music files : " + addedlist.size());
        mAddedMusicFiles = new ArrayList<>(addedlist);
    }

    public void reloadMusicFiles(List<MusicFile> list) {
        Log.d(TAG, "Reload music file lists. # of element : " + list.size());
        mMusicFiles = list;
        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new AddMusicListAdapter(mMusicFiles);
            Log.d(TAG, "New adapter, # of items : " + mAdapter.getItemCount());
            mAddMusicList.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "Data set changed, # of Items : " + mAdapter.getItemCount());
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "UI updated");
    }

    public List<MusicFile> getAddedMusicList() {
        Log.d(TAG, "getAddedMusicList, # of music files : " + mAddedMusicFiles.size());
        return mAddedMusicFiles;
    }


    private class AddMusicListItemHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mArtist;
        private TextView mDuration;

        private Button mButtonAdd;
        private boolean isAdded;
        private MusicFile mMusicFile;

        public AddMusicListItemHolder(View itemView) {
            super(itemView);
//            View v = (View) itemView.findViewById(R.id.music_file_info);
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Music Info On click");
//                }
//            });
            mTitle = (TextView) itemView.findViewById(R.id.music_file_info_title);
            mArtist = (TextView) itemView.findViewById(R.id.music_file_info_artist);
            mDuration = (TextView) itemView.findViewById(R.id.music_file_info_duration);
            mButtonAdd = (Button) itemView.findViewById(R.id.paylist_add_music_button_add);
            if (mTitle == null || mArtist == null || mDuration == null || mButtonAdd == null) {
                Log.e(TAG, "ViewHolder layout Error");
            }

            mButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Song : " + mMusicFile.getTitle() + " id : " + mMusicFile.getId()  + " ButtonAdd onClick()");

                    if (!isAdded) {
                        mAddedMusicFiles.add(mMusicFile);
                    } else {
                        mAddedMusicFiles.remove(mMusicFile);
                    }
                    buttonAddToggle();
                    String s = "";
                    for (MusicFile f : mAddedMusicFiles) {
                        s += f.getId() + ",";
                    }
                    Log.d(TAG, "current added list " + s);

                }
            });
        }

        public void bind(MusicFile file) {
            Log.d(TAG, "Bind Music File, ID : " + file.getId());

            mMusicFile = file;
            mTitle.setText(mMusicFile.getTitle());
            mArtist.setText(mMusicFile.getArtist());
            mDuration.setText(Utilities.millSecondsToTime(mMusicFile.getDuration()));

            if (mAddedMusicFiles.contains(mMusicFile)) {
                Log.d(TAG, "Music file (ID : " + mMusicFile.getId() + ") is already added");
                isAdded = true;
                mButtonAdd.setText(R.string.added);
            } else {
                Log.d(TAG, "Music file (ID : " + mMusicFile.getId() + ") is not added");
                isAdded = false;
                mButtonAdd.setText(R.string.add);
            }
        }

        private void buttonAddToggle() {
            if (!isAdded) {
                mButtonAdd.setText(R.string.added);
                isAdded = true;
            } else {
                mButtonAdd.setText(R.string.add);
                isAdded = false;
            }
        }
    }

    private class AddMusicListAdapter extends RecyclerView.Adapter<AddMusicListItemHolder> {

        private List<MusicFile> mMusicFiles;
        public AddMusicListAdapter(List<MusicFile> files) {
            mMusicFiles = files;
        }

        @Override
        public AddMusicListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.playlist_add_music_entry, parent, false);
            return new AddMusicListItemHolder(view);
        }

        @Override
        public void onBindViewHolder(AddMusicListItemHolder holder, int position) {
            MusicFile file = mMusicFiles.get(position);
            Log.d(TAG, "onBindViewHolder() : " + position + " in " + mMusicFiles.size() + " id : " + file.getId());
            holder.bind(file);
        }

        @Override
        public int getItemCount() {
            return mMusicFiles.size();
        }
    }
}
