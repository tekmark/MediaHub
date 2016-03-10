package com.example.chao.mediahub;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPlaylistChange} interface
 * to handle interaction events.
 * Use the {@link ClockChoosePlaylistDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClockChoosePlaylistDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "ChoosePlaylistDialog";

    private static int NO_ITEM_CHECKED = -1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Playlist> mPlaylists;
    private int mCheckedPosition;


    private OnPlaylistChange mListener;

    public ClockChoosePlaylistDialog() {
        // Required empty public constructor
        mCheckedPosition = -1;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClockChoosePlaylistDialog.
     */
    // TODO: Rename and change types and number of parameters
    public static ClockChoosePlaylistDialog newInstance(String param1, String param2) {
        ClockChoosePlaylistDialog fragment = new ClockChoosePlaylistDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //set title of dialog
        builder.setTitle("Choose Playlist");
        mPlaylists = MediaManager.getAllPlaylists(getContext());
        int size = mPlaylists.size();
        String playlistNames[] = new String[size];
        int i = 0;
        for (Playlist playlist : mPlaylists) {
            playlistNames[i] = playlist.getName();
            ++i;
        }
        builder.setSingleChoiceItems(playlistNames, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which){
                Log.d(TAG, "onClick(): " + which);
                mCheckedPosition = which;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onOK, position: " + mCheckedPosition);
                if (mCheckedPosition != NO_ITEM_CHECKED) {
                    //get playlist id;
                    int playlistId = mPlaylists.get(mCheckedPosition).getId();
                    Log.d(TAG, "playlistID : " + playlistId);
                    mListener.onPlaylistChange(playlistId);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onCancel()");
                dialog.cancel();
            }
        });
        return builder.create();
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
         //Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_clock_choose_playlist_dialog, container, false);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaylistChange) {
            mListener = (OnPlaylistChange) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlaylistChange");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPlaylistChange {
        // TODO: Update argument type and name
        void onPlaylistChange(int playlistId);
    }
}
