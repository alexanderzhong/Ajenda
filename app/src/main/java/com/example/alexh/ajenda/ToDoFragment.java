package com.example.alexh.ajenda;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.MotionEvent.ACTION_DOWN;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ToDoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ToDoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TODOLIST = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ToDoList toDoList;
    private ArrayList<String> moduleNames;

    private OnFragmentInteractionListener mListener;

    public ToDoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tdl Parameter 1.
     * @return A new instance of fragment ToDoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoFragment newInstance(ToDoList tdl) {
        ToDoFragment fragment = new ToDoFragment();
        Bundle args = new Bundle();
        args.putSerializable(TODOLIST, tdl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toDoList = (ToDoList) getArguments().getSerializable(TODOLIST);
            moduleNames = new ArrayList<>();
            for(int i = 0; i < toDoList.getTodolist().size(); i++) {
                moduleNames.add(toDoList.getTodolist().get(i).getModuleName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do, container, false);
        Button back = view.findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.finishFragment();
            }
        });

        TextView title = view.findViewById(R.id.toDoListTitle);
        title.setText(toDoList.getName());

        ListView tdlView = view.findViewById(R.id.tdlView);
        ToDoListAdapter toDoListAdapter = new ToDoListAdapter(getContext(), moduleNames);
        tdlView.setAdapter(toDoListAdapter);

        Button delete = view.findViewById(R.id.deleteToDoList);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteToDoList(toDoList);
            }
        });
//        view.getRootView().setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                int e = event.getAction();
////                switch(e) {
////                    case MotionEvent.ACTION_MOVE:
////
////                        break;
////                    default:
////                        break;
////                }
////                return false;
////            }
////
////
////        });
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
        void finishFragment();
        void deleteToDoList(ToDoList tdl);
        void onFragmentInteraction(Uri uri);
    }

    class ToDoListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> names;

        ToDoListAdapter(Context c, ArrayList<String> names) {
            super(c, R.layout.todolist_item, R.id.module_name, names);
            this.context = c;
            this.names = names;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.todolist_item, parent, false);
            TextView name = row.findViewById(R.id.name);

            //Set views
            name.setText(names.get(position));

            return super.getView(position, convertView, parent);
        }
    }
}
