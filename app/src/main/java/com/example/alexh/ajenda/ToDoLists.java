package com.example.alexh.ajenda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ToDoLists extends AppCompatActivity implements ToDoFragment.OnFragmentInteractionListener {

    private static final String TODOLIST_FILE = "todolists.txt";
    private static final String TODOLISTNAMES_FILE = "names.txt";

    //Navigation
    ImageButton calendarButton;

    //UI Variables
    TextView todolists;
    GridView myLists;
    FragmentManager fm = getSupportFragmentManager();
    ToDoFragment tdf;
    ImageButton addToDoList;
    PopupWindow popUp;

    //ToDoList Variables
    public ArrayList<ToDoList> toDoListArrayList = new ArrayList<>();
    ArrayList<String> ToDoListNames = new ArrayList<>();
    ArrayList<module> allModules = new ArrayList<>();

    //Passed in vars from MainActivity
    private HashMap<String, ArrayList<String>> events;
//    private MainActivity.Node[] eventDates = new MainActivity.Node[366]; //Feb 29 = 366 (technically 365)
    public WeatherObject[][] weatherDays = new WeatherObject[366][8];
    private int currentYear; //This is for the weather calendar for looping
    private int nextYear;
    private int currentLimit = 0; //Limit for forecasts (to prevent previous years forecasts)

    //Add New To Do List
    LinearLayout addForm;
    Button cancel;
    Button submit;
    EditText newToDoListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        loadToDoLists();
        super.onCreate(savedInstanceState);

        //Initializing ToDoList Variables
        Bundle bundle = getIntent().getExtras();
        if(bundle.get("toDoListArrayList") != null) {
            toDoListArrayList = (ArrayList<ToDoList>) bundle.get("toDoListArrayList");
            Log.d("Debug", "onCreate: Yeet");
        }
        Log.d("Debug", "onCreate: " + toDoListArrayList.size());
        ToDoListNames = bundle.getStringArrayList("toDoListArrayListNames");
        if(bundle.get("allModules") != null) {
            allModules = (ArrayList<module>) bundle.get("allModules");
        }

        setContentView(R.layout.activity_to_do_lists);
        calendarButton = findViewById(R.id.cButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openCalendar();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("toDoListArrayList", toDoListArrayList);
                resultIntent.putExtra("toDoListArrayListNames", ToDoListNames);
                resultIntent.putExtra("allModules", allModules);
                setResult(RESULT_OK, resultIntent);
                finish();


            }
        });

        todolists = findViewById(R.id.title);
        todolists.setText("ToDoLists");

        myLists = findViewById(R.id.myLists);
        //TODO: Transfer ToDoList Names to the string arraylist
        myListAdapter adapter = new myListAdapter(this, ToDoListNames);
        myLists.setAdapter(adapter);

//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
//        ft.replace(R.id.todofragment_container, tdf);
//        ft.hide(tdf);
//        ft.commit();


        popUp = new PopupWindow();
        addToDoList = findViewById(R.id.addToDoList);
        addToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddToDoListMenu();

            }
        });

        setupUI(findViewById(R.id.newToDoListPopup));

        //Form
        addForm = findViewById(R.id.newToDoListPopup);
        addForm.setVisibility(View.INVISIBLE);
        newToDoListName = findViewById(R.id.todolistname);
        cancel = findViewById(R.id.cancelToDo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeToDoListMenu();
            }
        });
        submit = findViewById(R.id.submitToDo);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitToDoList();
            }
        });
    }

    //Form functions
    public void openAddToDoListMenu() {
        //Opens menu for todolist
        addForm.setVisibility(View.VISIBLE);
    }

    public void closeToDoListMenu() {
        newToDoListName.setText("");
        addForm.setVisibility(View.INVISIBLE);
    }

    public void submitToDoList() {
        addToDoList(newToDoListName.getText().toString());
//        ((myListAdapter)myLists.getAdapter()).addListNames(newToDoListName.getText().toString());
        closeToDoListMenu();
        ((ArrayAdapter)myLists.getAdapter()).notifyDataSetChanged();
//        myLists.invalidate();
//        myLists.setVisibility(View.INVISIBLE); //Maybe a better solution required?
//        myLists.setVisibility(View.VISIBLE);
//        refreshView(myLists);
    }

    public void refreshView(View v) {


    }

    public void addToDoList(String name) {
        toDoListArrayList.add(new ToDoList(name, new ArrayList<module>()));
        ToDoListNames.add(name);
        saveToDoLists();
    }

    //Closes keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if(imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(ToDoLists.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void finishFragment() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(tdf);
        ft.commit();
        calendarButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteToDoList(ToDoList tdl) {
        toDoListArrayList.remove(tdl);
        ToDoListNames.remove(tdl.getName());
        ((ArrayAdapter) myLists.getAdapter()).notifyDataSetChanged();
        saveToDoLists();
        finishFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openCalendar() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    //Saving to memory
    public void saveToDoLists() {
        File directory = getFilesDir();
        File yourFile = new File(directory, TODOLIST_FILE);
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                if(toDoListArrayList != null) {
                    fos = openFileOutput(TODOLIST_FILE, MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(toDoListArrayList);
                    oos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(yourFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                pw.close();
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        saveToDoListNames();
    }
    public void saveToDoListNames() {
        File directory = getFilesDir();
        File yourFile = new File(directory, TODOLISTNAMES_FILE);
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                if(ToDoListNames != null) {
                    fos = openFileOutput(TODOLISTNAMES_FILE, MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(ToDoListNames);
                    oos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(yourFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                pw.close();
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadToDoLists() {
        File directory = getFilesDir();
        File yourFile = new File(directory, TODOLIST_FILE);
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            toDoListArrayList = new ArrayList<>();
        } else {
            FileInputStream fis = null;

            try {
                fis = openFileInput(TODOLIST_FILE);
                File lengthCheck = getFileStreamPath(TODOLIST_FILE);
                if(lengthCheck.length() == 0) {
                    toDoListArrayList = new ArrayList<>();
                } else {
                    Log.d("File Check", "loadToDoLists: " + lengthCheck.length());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    toDoListArrayList = (ArrayList<ToDoList>) ois.readObject();
                    ois.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(yourFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                pw.close();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {

            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        loadToDoListNames();
    }

    public void loadToDoListNames() {
        File directory = getFilesDir();
        File yourFile = new File(directory, TODOLISTNAMES_FILE);
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ToDoListNames = new ArrayList<>();
        } else {
            FileInputStream fis = null;

            try {
                fis = openFileInput(TODOLISTNAMES_FILE);
                File lengthCheck = getFileStreamPath(TODOLISTNAMES_FILE);
                if(lengthCheck.length() == 0) {
                    ToDoListNames = new ArrayList<>();
                } else {
                    Log.d("File Check", "loadToDoListNames: " + lengthCheck.length());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    ToDoListNames = (ArrayList<String>) ois.readObject();
                    ois.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(yourFile);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                pw.close();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {

            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class myListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> listNames;

        myListAdapter(Context c, ArrayList<String> listsNames) {
            super(c, R.layout.my_list_grid_item, R.id.list, listsNames);
            this.context = c;
            listNames = listsNames;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View block;
            if(convertView == null)
                block = layoutInflater.inflate(R.layout.my_list_grid_item, parent, false);
            else block = convertView;
            ImageButton myList = block.findViewById(R.id.list);
            myList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                    Log.d("index", "onClick: ");
                    tdf = ToDoFragment.newInstance(toDoListArrayList.get(position));
                    ft.replace(R.id.todofragment_container, tdf);
                    ft.commit();
                    calendarButton.setVisibility(View.INVISIBLE);
                }
            });

            //Set views
            myList.setImageResource(R.drawable.testlist);

            TextView name = block.findViewById(R.id.ToDoListName);
            name.setText(listNames.get(position));

            return block;
        }

//        public void addListNames(String newName) {
//            listNames.add(newName);
//            for(String a : listNames) {
//                Log.d("Yeet", "addListNames: " + a);
//            }
//        }
    }


    //MERGE SORT

    // Merges two subarrays of arr[].
    // First subarray is arr[l..m]
    // Second subarray is arr[m+1..r]
    void merge(module[] arr, int l, int m, int r)
    {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 =  r - m;

        /* create temp arrays */
        module[] L = new module[n1];
        module[] R = new module[n2];

        /* Copy data to temp arrays L[] and R[] */
        for (i = 0; i < n1; i++)
            L[i] = arr[l + i];
        for (j = 0; j < n2; j++)
            R[j] = arr[m + 1+ j];

        /* Merge the temp arrays back into arr[l..r]*/
        i = 0; // Initial index of first subarray
        j = 0; // Initial index of second subarray
        k = l; // Initial index of merged subarray
        while (i < n1 && j < n2)
        {
            if (L[i].getPriority() >= R[j].getPriority())
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

    /* Copy the remaining elements of L[], if there
       are any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

    /* Copy the remaining elements of R[], if there
       are any */
        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    void mergeSort(module[] arr, int l, int r)
    {
        if (l < r)
        {
            // Same as (l+r)/2, but avoids overflow for
            // large l and h
            int m = l+(r-l)/2;

            // Sort first and second halves
            mergeSort(arr, l, m);
            mergeSort(arr, m+1, r);

            merge(arr, l, m, r);
        }
    }

    //TODO: A lot of optimization can be done here
    public void combineModules(ArrayList<ToDoList> todolists) {
        //Optimization idea: just have a global variable with all modules
        ArrayList<module> modulesCombine = new ArrayList<>();
        for(int i = 0; i < todolists.size(); i++) {
            modulesCombine.addAll(todolists.get(i).getTodolist());
        }
        module[] arr = new module[modulesCombine.size()];
        for(int i = 0; i < modulesCombine.size(); i++) {
            arr[i] = modulesCombine.get(i);
        }
        mergeSort(arr, 0, arr.length);
        for(int i = 0; i < modulesCombine.size(); i++) {
            modulesCombine.set(i, arr[i]);
        }
        allModules = modulesCombine;

    }

//    void calculateBestTimes(ArrayList<ToDoList> todolists, HashMap<String, ArrayList<String>> events, Node[] dates) {
//
//        GregorianCalendar tempCal = new GregorianCalendar();
//        tempCal.set(GregorianCalendar.YEAR, 2018);
//        for(int i = 0; i < allModules.size(); i++) {
//
//            if (allModules.get(i) != null) { //Check if module is not null (should not be null, but just in case)
//                module mod = allModules.get(i);
//                String start = allModules.get(i).timeRangeStart;
//                int startYear = Integer.parseInt(start.substring(0, 4));
//                int startMonth = Integer.parseInt(start.substring(5, 7));
//                int startDay = Integer.parseInt(start.substring(8, 10));
//                String end = allModules.get(i).timeRangeEnd;
//                int endYear = Integer.parseInt(end.substring(0, 4));
//                int endMonth = Integer.parseInt(end.substring(5, 7));
//                int endDay = Integer.parseInt(end.substring(8, 10));
//
//                tempCal.set(GregorianCalendar.MONTH, startMonth);
//                tempCal.set(GregorianCalendar.DAY_OF_MONTH, startDay);
//                int starter = tempCal.get(GregorianCalendar.DAY_OF_YEAR);
//                tempCal.set(GregorianCalendar.MONTH, endMonth);
//                tempCal.set(GregorianCalendar.DAY_OF_MONTH, endDay);
//                int ender = tempCal.get(GregorianCalendar.DAY_OF_YEAR);
//
//                if (!allModules.get(i).inserted) {
//                    if(allModules.get(i).unit.equals("days")) { //For events longer than a day, i.e. a "trip"
//                        //Loop through array of events once within time period
//                        //check for null array segments and mark pairs for start and end indexes
//                    } else { //For short time events
//                        int opYear = -1;
//                        int opDay = -1;
//                        boolean reachedWeatherLimit = false;
//                        for (int j = starter; (startYear < endYear && j < 365) || (startYear == endYear && j < ender + 1); j++) {
//                            /*1. Check for available space, i.e. get day from array, check for events
//                            2. Check each day for the preferred weather
//                            3. For location:
//                            To maximize effectiveness and minimize costly operations, have threshold for amount of days
//                            to check over to find the "optimal" day, if passed this threshold, "break;"
//                            If no events on this day, then disregard location, otherwise check for locations of other events
//                            and minimize distance between them
//                            TODO: CHECK FOR LEAP YEARS
//                             */
//
//                            //Checks for current limit of weather forecasts
//                            if(j == currentLimit) {
//                                reachedWeatherLimit = true;
//                            }
//
//
//                            if(eventDates[j] == null) {
//                                GregorianCalendar tCal = new GregorianCalendar();
//                                tCal.set(GregorianCalendar.YEAR, 2009);
//                                //"clear sky", "few clouds", "scattered clouds", "broken clouds", "shower rain", "rain", "thunderstorm", "snow", "mist"
//                                //0, 1, 2, 3, 4, 5, 6, 7, 8
//
//                                //We want to schedule sometime during the day
//                                //For now, between 9AM - 9PM (21:00 military)
//                                int possibleTime = 3;
//                                int possibleTimePeriod = 0;
//                                if(!reachedWeatherLimit) {
//                                    for(int hour = 3; hour < 8; hour++) {
//                                        if(weatherDays[j][hour].descInt == mod.preferredWeather) {
//                                            possibleTimePeriod+=3;
//                                            if(possibleTimePeriod >= mod.eventTime) {
//                                                tCal.set(GregorianCalendar.DAY_OF_YEAR, j);
//                                                String date = Integer.toString(tCal.get(Calendar.YEAR));
//                                                date += " " + Integer.toString(tCal.get(Calendar.MONTH) + 1);
//                                                date += " " + Integer.toString(tCal.get(Calendar.DAY_OF_MONTH));
//
//                                                mod.dates.add(date);
//                                                String mD = mod.dates.get(0);
//
//                                                String time = "";
//                                                if(possibleTimePeriod*3 < 10) {
//                                                    time += "0" + Integer.toString(possibleTime*3);
//                                                } else {
//                                                    time += Integer.toString(possibleTime*3);
//                                                }
//                                                time+=":00";
//
//                                                tCal.set(GregorianCalendar.YEAR, 2009);
//                                                tCal.set(GregorianCalendar.MONTH, Integer.parseInt(mD.substring(5, 7)));
//                                                tCal.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(mD.substring(8, 10)));
//                                                int DOY = tCal.get(GregorianCalendar.DAY_OF_YEAR);
//
////                                                addeventbutton(mod.moduleName, mod.dates.get(0), DOY, time, mod.eventTime, mod.unit);
//                                            }
//                                        } else {
//                                            possibleTimePeriod = 0;
//                                            possibleTime = hour+1;
//                                        }
//                                    }
//                                }
//                            }
//
//                            if(j == 59 && startYear % 4 == 0) {//For Feb. 29 on leap years
//                                //Checks for current limit of weather forecasts
//                                if(currentLimit == 365) {
//                                    reachedWeatherLimit = true;
//                                }
//
//                                if(eventDates[365] == null) {
//                                    GregorianCalendar tCal = new GregorianCalendar();
//                                    tCal.set(GregorianCalendar.YEAR, 2009);
//                                    //"clear sky", "few clouds", "scattered clouds", "broken clouds", "shower rain", "rain", "thunderstorm", "snow", "mist"
//                                    //0, 1, 2, 3, 4, 5, 6, 7, 8
//
//                                    //We want to schedule sometime during the day
//                                    //For now, between 9AM - 9PM (21:00 military)
//                                    int possibleTime = 3;
//                                    int possibleTimePeriod = 0;
//                                    if(!reachedWeatherLimit) {
//                                        for(int hour = 3; hour < 8; hour++) {
//                                            if(weatherDays[365][hour].descInt == mod.preferredWeather) {
//                                                possibleTimePeriod+=3;
//                                                if(possibleTimePeriod >= mod.eventTime) {
//                                                    tCal.set(GregorianCalendar.DAY_OF_YEAR, j);
//                                                    String date = Integer.toString(tCal.get(Calendar.YEAR));
//                                                    date += " " + Integer.toString(tCal.get(Calendar.MONTH) + 1);
//                                                    date += " " + Integer.toString(tCal.get(Calendar.DAY_OF_MONTH));
//
//                                                    mod.dates.add(date);
//                                                    String mD = mod.dates.get(0);
//
//                                                    String time = "";
//                                                    if(possibleTimePeriod*3 < 10) {
//                                                        time += "0" + Integer.toString(possibleTime*3);
//                                                    } else {
//                                                        time += Integer.toString(possibleTime*3);
//                                                    }
//                                                    time+=":00";
//
//                                                    tCal.set(GregorianCalendar.YEAR, 2009);
//                                                    tCal.set(GregorianCalendar.MONTH, Integer.parseInt(mD.substring(5, 7)));
//                                                    tCal.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(mD.substring(8, 10)));
//                                                    int DOY = tCal.get(GregorianCalendar.DAY_OF_YEAR);
//
////                                                    addeventbutton(mod.moduleName, mod.dates.get(0), DOY, time, mod.eventTime, mod.unit);
//                                                }
//                                            } else {
//                                                possibleTimePeriod = 0;
//                                                possibleTime = hour+1;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            if (j == 365) startYear++;
//                        }
//                    }
//                }
//
//            }
//        }
//    }

    //This method assumes that the new event is properly inserted
    /*void addeventbutton(String name, String date, int dayOfYear, String time, int eventTime, String unit){
        ArrayList<String> temp = events.get(date);
        if(temp != null) {
            events.get(date).add(name);
        } else {
            ArrayList<String> newEventStub = new ArrayList<>();
            newEventStub.add(name);
            events.put(date, newEventStub);
        }

        MainActivity.Node node = eventDates[dayOfYear];
        if(node == null) {
//            eventDates[dayOfYear] = new MainActivity.Node(date.substring(0, 4), time, name, null, eventTime, unit);
        }
        while(node.next != null) {
            if(Integer.parseInt(node.year) < Integer.parseInt(date.substring(0, 4))) {
                node = node.next;
            }
            if(Integer.parseInt(node.year) == Integer.parseInt(date.substring(0, 4))) {
                int h1 = Integer.parseInt(node.time.substring(0, 2));
                int m1 = Integer.parseInt(node.time.substring(2, 4));
                int hn = Integer.parseInt(time.substring(0,2));
                int mn = Integer.parseInt(time.substring(2, 4));
                int h2 = Integer.parseInt(node.next.time.substring(0, 2));
                int m2 = Integer.parseInt(node.next.time.substring(2, 4));

                if(h1 < hn && hn < h2) {
                    break;
                } else if(h1 == hn && hn < h2) {
                    if(m1 < mn) break;
                } else if(h1 < hn && hn == h2) {
                    if(mn < m2) break;
                } else if(h1 == hn && hn == h2) {
                    if(m1 < mn && mn < m2) break;
                } else {
                    node = node.next;
                }
            }
        }
        MainActivity.Node p = node.next; //Either next node or null
        MainActivity.Node newEvent = new MainActivity.Node(date.substring(0, 4), time, name, p, eventTime, unit);
        node.next = newEvent;
    }
    */
}

