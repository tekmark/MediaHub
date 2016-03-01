package com.example.chao.mediahub;

import android.content.Intent;
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

import java.util.ArrayList;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final static private String TAG = "LibraryTabFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnInteractionListener");
//        }
//    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void updateUI() {
//        List<String> list = new ArrayList<String>();
//        list.add("item1");
//        list.add("item2");
//        list.add("item3");
//        list.add("item4");
//
//        mAdapter = new LibraryAdapter(list);
        List<MusicFile> list = MediaManager.getAllMusicFiles(this.getContext());
        mAdapter = new LibraryAdapter(list);
        mLibraryRecyclerView.setAdapter(mAdapter);
    }

    private class LibraryItemHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mArtistAlbum;
        public ImageButton mOptions;
        public LibraryItemHolder(View itemView) {
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.library_song_entry_label_title);
            mArtistAlbum = (TextView) itemView.findViewById(R.id.library_song_entry_label_artist_album);
            mOptions = (ImageButton) itemView.findViewById(R.id.library_entry_button_more);
            mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "more option on click");
                }
            });
        }
    }

    private class LibraryAdapter extends RecyclerView.Adapter<LibraryItemHolder> {
        private List<MusicFile> mMusicFiles;
        public LibraryAdapter(List<MusicFile> files) {
            mMusicFiles = files;
        }

        @Override
        public LibraryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.library_song_entry, parent, false);
            return new LibraryItemHolder(view);
        }
        @Override
        public void onBindViewHolder(LibraryItemHolder holder, int position) {
            MusicFile file = mMusicFiles.get(position);
            holder.mTitle.setText(file.getTitle());
            holder.mArtistAlbum.setText(file.getArtist() + " - " + file.getAlbum());
        }

        @Override
        public int getItemCount() {
            return mMusicFiles.size();
        }
    }
}
