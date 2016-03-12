package com.example.chao.mediahub;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import android.os.Handler;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class ClockFragment extends Fragment {
    private static final String TAG = "ClockFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClockFragment() {
        // Required empty public constructor
    }

    private TextView mLeft;
    private TextView mRight;
    private TextView mSeparator;

    private TextView mAM;
    private TextView mPM;
    private TextView mAlarm;

    private boolean m24Hour;

    private Handler mHandler = new Handler();
    private Calendar mCalendar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClockFragment newInstance(String param1, String param2) {
        ClockFragment fragment = new ClockFragment();
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
        mCalendar = new GregorianCalendar();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //getActivity().getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences pref = this.getActivity().getSharedPreferences("pref_header_clock", Context.MODE_PRIVATE);
        Map<String, ?> map = pref.getAll();
        Log.d(TAG, "Size: " + map.size());
        for (String s : map.keySet()) {
            Log.d(TAG, "Key" + s);
        }

        m24Hour = pref.getBoolean("example_switch1", true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_clock, container, false);
        int height = rootView.getHeight();
        int width = rootView.getWidth();
        Log.d(TAG, "height: " + height + " , width: " + width);

        mAM = (TextView) rootView.findViewById(R.id.clock_indicator_am);
        mPM = (TextView) rootView.findViewById(R.id.clock_indicator_pm);

        mLeft = (TextView) rootView.findViewById(R.id.clock_left_part);
        mRight = (TextView) rootView.findViewById(R.id.clock_right_part);
        mSeparator = (TextView) rootView.findViewById(R.id.clock_separator);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LiquidCrystal/LiquidCrystal-Bold.otf");
        //Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BlackBall/Black Ball.ttf");
        mLeft.setTypeface(tf);
        mRight.setTypeface(tf);
        mSeparator.setTypeface(tf);
        //separatorBlink();
//        rootView.getViewTreeObserver().add(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int height = rootView.getHeight();
//                int width = rootView.getWidth();
//                Log.d(TAG, "height: " + height + " , width: " + width);
//                resizeClockText(height, width);
//            }
//        });
        //TextView textView = new TextView(getActivity());
        //textView.setText(R.string.hello_blank_fragment);
        mHandler.postDelayed(updateClockThread, 500);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mHandler.removeCallbacks(updateClockThread);
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
        void onFragmentInteraction(Uri uri);
    }

    //TODO: a new resizing algorithm.
    private void resizeClockText(int height, int width) {
        float left_width = mLeft.getPaint().measureText("00");

        float left = mLeft.getWidth();
        float left_size = mLeft.getTextSize();
        float right_size = mLeft.getTextSize();
        int screen_width_px = getResources().getDisplayMetrics().widthPixels;
        float textSize = left * left_size / left_width;
        Log.d(TAG, "left_size: " + left_size + ", left: " + left + ", width: " + left_width + ", text size: " + textSize);
        mLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 9 / 10);
//        float width_max = screen_width_px * 9 / 10;
//        float curr_width = mClockView.getPaint().measureText("00:00");
//        float curr_text_size = mClockView.getTextSize();
//        Log.d("Size", "screen px: " + screen_width_px + " width " + width_max + " curr : " + curr_width + "curr_text : " + curr_text_size);
//        float size = width_max * curr_text_size / curr_width;
//        Log.d("Size", "new size: " + size);
//        mClockView.setTextSize(size/2);
        //mLeft.measure();
    }

    public void separatorBlink() {
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(900);
        //anim.setRepeatCount(Animation.INFINITE);
        mSeparator.startAnimation(anim);
        //mSeparator.setAnimation(anim);
    }

    private Runnable updateClockThread = new Runnable() {
        @Override
        public void run() {
            //mCalendar = new GregorianCalendar();
            mCalendar = Calendar.getInstance();
            if (m24Hour) {     //not 24 hour, use AM_PM
                int h = mCalendar.get(Calendar.HOUR_OF_DAY);
                String hour = String.valueOf(h);
                mLeft.setText(hour);
                disableAMPM();
            } else {
                int h = mCalendar.get(Calendar.HOUR);
                String hour = String.valueOf(h);
                mLeft.setText(hour);
                if (mCalendar.get(Calendar.AM_PM) == Calendar.AM) {
                    displayAM();
                } else {
                    displayPM();
                }
            }

            int m = mCalendar.get(Calendar.MINUTE);
            String minute;
            if (m < 10) {
                minute = "0" + String.valueOf(m);
            } else {
                minute = String.valueOf(m);
            }
            mRight.setText(minute);
            separatorBlink();

            mHandler.postDelayed(this, 1000);
        }
    };

    private void disableAMPM() {
        mAM.setVisibility(View.INVISIBLE);
        mPM.setVisibility(View.INVISIBLE);
    }
    private void displayAM() {
        mAM.setVisibility(View.VISIBLE);
        mPM.setVisibility(View.INVISIBLE);
    }
    private void displayPM() {
        mAM.setVisibility(View.INVISIBLE);
        mPM.setVisibility(View.VISIBLE);
    }

}
