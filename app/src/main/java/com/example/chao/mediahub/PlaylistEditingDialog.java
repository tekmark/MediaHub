package com.example.chao.mediahub;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditingDoneListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistEditingDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistEditingDialog extends DialogFragment {
    private static final String TAG = "PlaylistEditingDialog";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MUSIC_FILE_LIST = "ArgMusicFileList";
    private static final String ARG_PLAYLIST_NAME = Tags.Arguments.AGR_PLAYLIST_NAME;


    // TODO: Rename and change types of parameters
    private ArrayList<MusicFile> mMusicFiles;
    private String mPlaylistName;

    private OnEditingDoneListener mListener;

    private RecyclerView mEditingList;
    private Button mBtnDone;
    private Button mBtnCancel;
    private TextView mLabelName;

    private PlaylistEditingAdapter mAdapter;

    public PlaylistEditingDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistEditingDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistEditingDialog newInstance(ArrayList<MusicFile> list) {
        PlaylistEditingDialog fragment = new PlaylistEditingDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MUSIC_FILE_LIST, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMusicFiles = getArguments().getParcelableArrayList(ARG_MUSIC_FILE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_playlist_editing, container, false);
        mEditingList = (RecyclerView) rootView.findViewById(R.id.editing_list);
        mEditingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PlaylistEditingAdapter(mMusicFiles);
        mEditingList.setAdapter(mAdapter);

        PlaylistEditingCallback callback = new PlaylistEditingCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mEditingList);

        mBtnCancel = (Button)rootView.findViewById(R.id.top_bar_button_cancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        mBtnDone = (Button) rootView.findViewById(R.id.top_bar_button_done);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListener.onDone();
                List<String> audioIds = new ArrayList<>();
                for (MusicFile file : mAdapter.getMusicFiles()) {
                    String audioId = String.valueOf(file.getAudioId());
                    audioIds.add(audioId);
                }
                mListener.onEditingDone(audioIds);
            }
        });

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditingDoneListener) {
            mListener = (OnEditingDoneListener) context;
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
    public interface OnEditingDoneListener {
        // TODO: Update argument type and name
        void onEditingDone(List<String> audioIds);
    }



    private class PlaylistEditingHolder extends RecyclerView.ViewHolder {
        //private View mMusicFileInfo;
        private TextView mTitle;
        private TextView mArtistAlbum;
        private TextView mDuration;
        private int mAudioId;

        public PlaylistEditingHolder(View itemView) {
            super(itemView);
            //mMusicFileInfo = itemView.findViewById(R.id.library_list_entry_music_file_info);
            mTitle = (TextView)itemView.findViewById(R.id.music_file_info_title);
            mArtistAlbum = (TextView) itemView.findViewById(R.id.music_file_info_artist_album);
            mDuration = (TextView) itemView.findViewById(R.id.music_file_info_duration);
            mAudioId = -1;
//            if (mMusicFileInfo == null) {
//                Log.e(TAG, "RecyclerView item holders layout error");
//            } else {
//                setListeners();
//            }
        }

        public void bind(MusicFile file) {
            mTitle.setText(file.getTitle());
            mArtistAlbum.setText(file.getArtist());
            mDuration.setText(Utils.millSecondsToTime(file.getDuration()));
            mAudioId = file.getAudioId();
        }

        private void setListeners() {
//            mMusicFileInfo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Music file info on click");
//                }
//            });

        }
    }

    private class PlaylistEditingAdapter extends RecyclerView.Adapter<PlaylistEditingHolder> implements ItemTouchHelperAdapter {
        private List<MusicFile> mMusicFiles;
        public PlaylistEditingAdapter(List<MusicFile> files) {
            mMusicFiles = files;
        }

        @Override
        public PlaylistEditingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.music_file_info, parent, false);
            return new PlaylistEditingHolder(view);
        }
        @Override
        public void onBindViewHolder(PlaylistEditingHolder holder, int position) {
            MusicFile file = mMusicFiles.get(position);
            Log.d(TAG, "AudioId : " + file.getAudioId() + " Id: " + file.getId());
            holder.bind(file);
        }

        @Override
        public int getItemCount() {
            return mMusicFiles.size();
        }

        public List<MusicFile> getMusicFiles() {
            return mMusicFiles;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mMusicFiles, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mMusicFiles, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            mMusicFiles.remove(position);
            notifyItemRemoved(position);
        }
    }

    private interface ItemTouchHelperAdapter {

        void onItemMove(int fromPosition, int toPosition);

        void onItemDismiss(int position);
    }

    private class PlaylistEditingCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public PlaylistEditingCallback (ItemTouchHelperAdapter adapter) {
            //super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.mAdapter = adapter;
        }


        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

    }
}
