package com.example.alexh.ajenda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarHolder.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarHolder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarHolder extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ControlCalendar calendar;
    HashMap<String, ArrayList<Event>> events;

    public void updateCalendar() {
        calendar.updateView();
    }

    public CalendarHolder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarHolder.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarHolder newInstance(String param1, String param2) {
        CalendarHolder fragment = new CalendarHolder();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_holder, container, false);

        calendar = view.findViewById(R.id.calendar_view);
        calendar.activity = this.getActivity();
        //Set background
        //View root = calendar.getRootView();
        view.getRootView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        Date e1 = new Date();
//        ArrayList<String> birthday = new ArrayList<String>();
//        birthday.add("Alex's Birthday");
//        events.put((String) DateFormat.format("yyyy MM dd", e1), birthday);
        Log.d("2", "onCreateView: Done");
//        calendar.setEvents(events);
        calendar.setEventHandler(new ControlCalendar.eventHandler()
        {
            @Override
            public void onDayPress(Date date)
            {
                // show returned day
//                openDaySchedule(date);
                if(mListener != null) {
                    mListener.setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED, date);
//                    calendar.collapse();
                    ViewGroup.LayoutParams p = calendar.getLayoutParams();
                    //changes grid height instead of individual height
//                    p.= 250;
//                    view.setLayoutParams(p);
//                    Log.d("Test", "onDayPress: done");
                }

            }
        });
        return view;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ControlCalendar getCalendar() {
        return calendar;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void setBottomSheetState(int i, Date date);
        void onFragmentInteraction(Uri uri);
    }
}
