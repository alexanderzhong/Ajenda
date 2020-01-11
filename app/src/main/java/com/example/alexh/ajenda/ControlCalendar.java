package com.example.alexh.ajenda;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ControlCalendar extends LinearLayout
{
    public interface eventHandler {
        void onDayPress(Date date);
    }
    // internal components
    private LinearLayout header;
    private TextView month_display;
    private ImageView btnPrev;
    private ImageView btnNext;
    private GridView grid;
    private LayoutInflater inflater;
    Activity activity;
    private HashMap<String, ArrayList<Event>> events;
    eventHandler eH;
    Calendar currentMonth;
    ArrayList<Date> cells;
    int mode;

    //Storage
//    DataStorage storage = DataStorage.getInstance();

    public ControlCalendar(Context context) {
        super(context);
        initControl(context);
    }

    public ControlCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    public ControlCalendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initControl(context);
    }

    /**
     * Load component XML layout
     */
    private void initControl(Context context)
    {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.control_calendar, this);

        events = ((DataStorage)(context.getApplicationContext())).getEvents();

        mode = 2;

        // layout is inflated, assign local variables to components
        month_display = findViewById(R.id.calendar_date_display);
        month_display.setBackgroundResource(R.color.colorPrimary);
        header = findViewById(R.id.calendar_header);
        header.setBackgroundResource(R.color.colorPrimary);
        grid = findViewById(R.id.calendar_grid);
//        grid.setBackgroundResource(R.color.colorPrimary);
        ArrayList<Date> initDates = new ArrayList<>(42);
        CalendarAdapter adapter = new CalendarAdapter(getContext(), R.layout.control_calendar_day, initDates);

        grid.setAdapter(adapter);

        //Set background
        View root = month_display.getRootView();
        root.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        RelativeLayout head = findViewById(R.id.head);
        head.setBackgroundResource(R.color.colorPrimary);

        // long-pressing a day
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> view, View cell, int position, long id)
            {
//                 handle long-press
                if (eH == null) return;

                Date date = (Date) view.getItemAtPosition(position);
                eH.onDayPress(date);
            }
        });
        currentMonth = Calendar.getInstance();
        updateCalendar(currentMonth);
        btnPrev = findViewById(R.id.calendar_prev_button);
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth.add(Calendar.MONTH, -1);
                updateCalendar(currentMonth);
            }
        });
        btnNext = findViewById(R.id.calendar_next_button);
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth.add(Calendar.MONTH, 1);
                updateCalendar(currentMonth);
            }
        });
    }

    public void updateView() {
        ((ArrayAdapter<Date>)grid.getAdapter()).notifyDataSetChanged();
    }

    public void setActivity(Activity a) {
        activity = a;
        events = ((DataStorage)activity.getApplication()).getEvents();
    }

    public void setEventHandler(eventHandler e) {
        eH = e;
    }

    public HashMap<String, ArrayList<Event>> getEvents() {
        return events;
    }

    public void collapse() {
        mode = 0;
        updateCalendar(currentMonth);
    }

    public void expand() {
        mode = 1;
        updateCalendar(currentMonth);
    }

    private void updateCalendar(Calendar calendar)
    {
        cells = new ArrayList<>();
        SimpleDateFormat month_format = new SimpleDateFormat("LLLL");
        month_display.setText(month_format.format(calendar.getTime()));
        month_display.setTextColor(Color.WHITE);
        // determine the cell for current month's beginning
        Calendar temp = (Calendar)calendar.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int maximumDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH);
        int monthBeginningCell = temp.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        temp.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells (42 days calendar as per our business logic)
        //int DAYS_COUNT = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int DAYS_COUNT;
        int days_for_month = monthBeginningCell + maximumDays;
//        Log.d("I am bad at math", Integer.toString(days_for_month));
//        Log.d("dayofmonth", Integer.toString(monthBeginningCell));
//        Log.d("daysofmonth", Integer.toString(maximumDays));
//        if(days_for_month <= 28) {
//            DAYS_COUNT = 28;
//        } else if(days_for_month <= 35) {
//            DAYS_COUNT = 35;
//        } else {
//            DAYS_COUNT = 42;
//        }
        DAYS_COUNT = 42;
        while (cells.size() < DAYS_COUNT) {
            cells.add(temp.getTime());
            temp.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid

        if(grid.getAdapter() != null) {
            ((CalendarAdapter) grid.getAdapter()).updateData(cells);
        }
    }

    class CalendarAdapter extends ArrayAdapter<Date> {

        public CalendarAdapter(Context context, int resource, ArrayList<Date> cells) {
            super(context, resource, cells);
        }


        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
//            view.setBackgroundResource(eventDays.contains(date)) ?
//                    R.drawable.reminder : 0);
            //background
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            // clear styling
            TextView day = view.findViewById(R.id.day);
            String dayOfMonth = (String) DateFormat.format("d", date);
            day.setText(dayOfMonth);
            day.setTypeface(null, Typeface.NORMAL);
            day.setTextColor(Color.WHITE);

//            if (date.getMonth() != today.getMonth() ||
//                    date.getYear() != today.getYear())
            if(date.getMonth() != currentMonth.getTime().getMonth())
            {
                // if this day is outside current month, grey it out
                day.setTextColor(getResources().getColor(R.color.greyed_out));
            }
            else if (date.getDate() == today.getDate() && date.getMonth() == today.getMonth())
            {
                 //if it is today, set it to blue/bold
                day.setTypeface(null, Typeface.BOLD);
                day.setTextColor(getResources().getColor(R.color.today));
            }

            // set text
//            view.setText(String.valueOf(date.getDate()));
            ImageView event1 = view.findViewById(R.id.event1);
            ImageView event2 = view.findViewById(R.id.event2);
            ImageView event3 = view.findViewById(R.id.event3);

//             && (mode == 1 || mode == 2)

//            if(events != null && mode != 0) {
            //Show events
            Log.d("Events", "getView: events retrieved");
            if(events != null) {
                if (!events.containsKey((String) DateFormat.format("yyyy MM dd", date))) {
                    event1.setVisibility(View.INVISIBLE);
                    event2.setVisibility(View.INVISIBLE);
                    event3.setVisibility(View.INVISIBLE);
                    Log.d("stuff", "getView: failed to load");
                } else {
                    Log.d("stuff", "getView: successfully loaded");
                    if (events.get((String) DateFormat.format("yyyy MM dd", date)).size() == 1) {
                        event1.setVisibility(View.VISIBLE);
                        event2.setVisibility(View.INVISIBLE);
                        event3.setVisibility(View.INVISIBLE);
                    } else if (events.get((String) DateFormat.format("yyyy MM dd", date)).size() == 2) {
                        event3.setVisibility(View.INVISIBLE);
                        event2.setVisibility(View.VISIBLE);
                        event1.setVisibility(View.VISIBLE);
                    } else {
                        event1.setVisibility(View.VISIBLE);
                        event2.setVisibility(View.VISIBLE);
                        event3.setVisibility(View.VISIBLE);
//                        Log.d("error", Integer.toString(events.get((String) DateFormat.format("yyyy MM dd", date)).size()));
                    }
                }
            }
//            } else {
//                event1.setVisibility(View.GONE);
//                event2.setVisibility(View.GONE);
//                event3.setVisibility(View.GONE);
//            }


//            if(mode == 1) {
//                ViewGroup.LayoutParams p = view.getLayoutParams();
//                //changes grid height instead of individual height
//                p.height = 250;
//                view.setLayoutParams(p);
//            } else if(mode == 0) {
//                //runs 42 times
//                ViewGroup.LayoutParams p = view.getLayoutParams();
//                //changes grid height instead of individual height
//                p.height = 180;
//                view.setLayoutParams(p);
//            }
            return view;
        }

        void updateData(ArrayList<Date> cells) {
            clear();
            for(int i = 0; i < cells.size(); i++) {
                add(cells.get(i));
            }
        }
    }

}
