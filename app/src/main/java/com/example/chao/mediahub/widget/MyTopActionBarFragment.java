package com.example.chao.mediahub.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.chao.mediahub.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventListener} interface
 * to handle interaction events.
 * Use the {@link MyTopActionBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTopActionBarFragment extends Fragment {
    //private static final String TAG = "MyTopActionBar";



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "ArgTitle";
    private static final String ARG_TAG = "ArgTag";
    private String mTitle;
    private String TAG;

    private Button mBtnDone;
    private Button mBtnCancel;
    private TextView mLableTitle;

    private EventListener mListener;

    public MyTopActionBarFragment() {
        // Required empty public constructor
    }

    public static MyTopActionBarFragment newInstance(String title, String tag) {
        MyTopActionBarFragment fragment = new MyTopActionBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            TAG = getArguments().getString(ARG_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_top_action_bar, container, false);
        mBtnCancel = (Button) rootView.findViewById(R.id.top_bar_button_cancel);
        mBtnDone = (Button) rootView.findViewById(R.id.top_bar_button_done);
        mLableTitle = (TextView) rootView.findViewById(R.id.top_bar_title);

        return rootView;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface EventListener {
        // TODO: Update argument type and name
        void onCancel();
        void onDone();
    }
}
