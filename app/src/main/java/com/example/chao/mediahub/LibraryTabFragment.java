package com.example.chao.mediahub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LibraryTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LibraryTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryTabFragment extends Fragment {

    private static final String TAG = "LibraryTabFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnInteractionListener mListener;

    private LibraryAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryTabFragment newInstance(String param1, String param2) {
        LibraryTabFragment fragment = new LibraryTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LibraryTabFragment() {
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

    private RecyclerView mLibraryRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_library_tab, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        textView.setText("Library");
        //set up RecyclerView
        mLibraryRecyclerView = (RecyclerView) rootView.findViewById(R.id.library_recycler_view);
        mLibraryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
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
        void onClickMoreOptions(int audioId);
    }

    private void updateUI() {
        List<MusicFile> list = MediaManager.getAllMusicFiles(this.getContext());
        mAdapter = new LibraryAdapter(list);
        mLibraryRecyclerView.setAdapter(mAdapter);
    }

    private class LibraryTabItemHolder extends RecyclerView.ViewHolder {
        private View mMusicFileInfo;
        private TextView mTitle;
        private TextView mArtistAlbum;
        private TextView mDuration;
        private ImageButton mOptions;
        private int mAudioId;

        public LibraryTabItemHolder(View itemView) {
            super(itemView);
            mMusicFileInfo = itemView.findViewById(R.id.library_list_entry_music_file_info);
            mTitle = (TextView)itemView.findViewById(R.id.music_file_info_title);
            mArtistAlbum = (TextView) itemView.findViewById(R.id.music_file_info_artist_album);
            mDuration = (TextView) itemView.findViewById(R.id.music_file_info_duration);
            mOptions = (ImageButton) itemView.findViewById(R.id.library_list_entry_button_more);
            mAudioId = -1;
            if (mMusicFileInfo == null || mOptions == null) {
                Log.e(TAG, "RecyclerView item holders layout error");
            } else {
                setListeners();
            }
        }

        public void bind(MusicFile file) {
            mTitle.setText(file.getTitle());
            mArtistAlbum.setText(file.getArtist());
            mDuration.setText(Utils.millSecondsToTime(file.getDuration()));
            mAudioId = file.getAudioId();
        }

        private void setListeners() {
            mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "more option on click, AudioId: " + mAudioId);
                    mListener.onClickMoreOptions(mAudioId);
                }
            });
            mMusicFileInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Music file info on click");
                }
            });
        }
    }

    private class LibraryAdapter extends RecyclerView.Adapter<LibraryTabItemHolder> {
        private List<MusicFile> mMusicFiles;
        public LibraryAdapter(List<MusicFile> files) {
            mMusicFiles = files;
        }

        @Override
        public LibraryTabItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.library_list_entry, parent, false);
            return new LibraryTabItemHolder(view);
        }
        @Override
        public void onBindViewHolder(LibraryTabItemHolder holder, int position) {
            MusicFile file = mMusicFiles.get(position);
            Log.d(TAG, "AudioId : " + file.getAudioId() + " Id: " + file.getId());
            holder.bind(file);
        }

        @Override
        public int getItemCount() {
            return mMusicFiles.size();
        }
    }
}
