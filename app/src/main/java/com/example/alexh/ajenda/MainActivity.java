package com.example.alexh.ajenda;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

/*TODO: Implement add dates method (inserts new event date into the arraylist (auto sorts it when
inserted)
 */

public class MainActivity extends AppCompatActivity implements CalendarHolder.OnFragmentInteractionListener {

    //TESTING
    //Format is:
    //Start Time: yyyy MM dd HHmm
    //End Time: HHmm


    //Form Popup
    Dialog eventForm;

    //Navigation
    ImageButton todoButton;
    ImageButton contactsButton;

    //Events
    private HashMap<String, ArrayList<Event>> events; //Date in yyyy mm dd : arraylist of events
    public WeatherObject[][] weatherDays = new WeatherObject[366][8];
    private int currentYear; //This is for the weather calendar for looping
    private int nextYear;
    private int currentLimit = 0; //Limit for forecasts (to prevent previous years forecasts)

    //ToDoLists Activity Variables
    public ArrayList<ToDoList> toDoListArrayList = new ArrayList<>();
    ArrayList<String> ToDoListNames = new ArrayList<>();
    ArrayList<module> allModules = new ArrayList<>();


    //Saving and loading files
    private static final String EVENT_FILE = "events.txt";
    private static final String TODOLIST_FILE = "todolists.txt";
    private static final String TODOLISTNAMES_FILE = "names.txt";


    //Voice Commands/Speech Recognition
    private TextToSpeech myTTS;
    private SpeechRecognizer mySpeechRecognizer;
    private static String[] commands = {};
    FloatingActionButton fab;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    //Calendar Stuff
    ControlCalendar calendar;
    CalendarHolder calendarFragment;
    Fragment dayFragment;

    //Bottom Sheet
    private BottomSheetBehavior mBottomSheetBehavior;
    TextView testTemp;
    TextView testDescription;

    //Weather
    String[] conditionsArr = {"clear sky", "few clouds", "scattered clouds", "broken clouds", "shower rain", "rain", "thunderstorm", "snow", "mist"};

    //Adding Form
//    ScrollView formScroll;
    ImageButton addNewEvent;
    Button submitButton;
    Button cancelButton;

    EditText eventName;
    EditText eventLocation;
    EditText eventStart;
    EditText eventEnd;
    CheckBox allDay;

    //DaySchedule Scroll
    private static final String TAG = MainActivity.class.getSimpleName();
    RelativeLayout mLayout;
    int eventIndex;
    public static int numViews = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //eventForm
        eventForm = new Dialog(this);


        //Testing
//        events = new HashMap<>();
//        Date e1 = new Date();
//        ArrayList<Event> birthday = new ArrayList<>();
//        Event testEvent = new Event("Balup", "Dickpit",
//                false, "1900", "2100", true, "Oh my goodness! I'm so excited for blarp!");
//        birthday.add(testEvent);
//        events.put((String) DateFormat.format("yyyy MM dd", e1), birthday);
//        if(events == null) Log.d("Events", "onCreate: Events inserted");
//        ((DataStorage)this.getApplication()).setEvents(events);

        //Initialization of storage variables

        loadEvents();
        for(String key: events.keySet()) {
            Log.d("event check", "onCreate: 1");
        }
        loadToDoLists();
        Log.d(TAG, "onCreate: " + toDoListArrayList.size());
        Log.d(TAG, "onCreate: " + ToDoListNames.size());

//        if(toDoListArrayList.size() < ToDoListNames.size()) {
//            for(int i = toDoListArrayList.size(); i < ToDoListNames.size(); i++) {
//                toDoListArrayList.add(new ToDoList(ToDoListNames.get(i)));
//            }
//            saveToDoLists();
//        }

        if(((DataStorage)this.getApplication()).getEvents().isEmpty()) {
            Log.d(TAG, "onCreate: successful");
        }

        setContentView(R.layout.activity_main);
//        setupUI(findViewById(R.id.formScroll));





        //Day Schedule
        mLayout = findViewById(R.id.left_event_column);
        eventIndex = mLayout.getChildCount();

        //To other activities
        todoButton = findViewById(R.id.todoButton);
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openToDoLists();
            }
        });
        todoButton.bringToFront();

        contactsButton = findViewById(R.id.contactButton);
        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContacts();
            }
        });
        contactsButton.bringToFront();

        //Fragments
        calendarFragment = new CalendarHolder();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calendarHolder, calendarFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //Set Background
        contactsButton.getRootView().setBackgroundResource(R.color.colorPrimary);

        //Bottom Sheet
        final View bottomSheet = findViewById(R.id.dayBottomSheet);
        bottomSheet.setBackgroundColor(Color.WHITE);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels/3);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch(i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
//                        calendarFragment.getCalendar().expand();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//                            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)fab.getLayoutParams();
//
//                            float d = getResources().getDisplayMetrics().density;
//                            lp.setMargins(0, 0, (int)(50*d), (int)(70*d));
//                            fab.setLayoutParams(lp);
                        }

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.setStatusBarColor(Color.WHITE);
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

//                            int dpValue = 2; // margin in dips
//                            float d = getResources().getDisplayMetrics().density;
////                            int margin = (int)(dpValue * d); // margin in pixels
//                            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)fab.getLayoutParams();
//                            lp.setMargins(0, 0, (int)(50*d), (int) (200*d));
//                            fab.setLayoutParams(lp);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        //Speech Recognition Stuff
        requestAudioPermissions();
//        initializeTextToSpeech();
        initializeSpeechRecognizer();
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mySpeechRecognizer.startListening(intent);
                Log.d("check", "onClick: here");
            }
        });

        //Weather API
        find_weather();
        testDescription = findViewById(R.id.testDescription);
        testTemp = findViewById(R.id.testTemp);

        //Adding Form Stuff
//        formScroll = findViewById(R.id.formScroll);
//        formScroll.setVisibility(View.INVISIBLE);
        addNewEvent = findViewById(R.id.addNewEvent);
        addNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                openForm();
            }
        });
        addNewEvent.bringToFront();
//        cancelButton = findViewById(R.id.cancel);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancelForm();
//            }
//        });
//        submitButton = findViewById(R.id.submit);
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitForm();
//            }
//        });
//        eventName = findViewById(R.id.eventName);
//        eventLocation = findViewById(R.id.eventLocation);
//        eventStart = findViewById(R.id.startTime);
//        eventEnd = findViewById(R.id.endTime);
//        allDay = findViewById(R.id.allDay);

        Log.d(TAG, "numViews: " + Integer.toString(mLayout.getChildCount()));

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(MainActivity.this);
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

    //Navigation
    public void openToDoLists() {
        Intent intent = new Intent(this, ToDoLists.class);
        intent.putExtra("toDoListArrayList", toDoListArrayList);
        intent.putExtra("toDoListArrayListNames", ToDoListNames);
        intent.putExtra("allModules", allModules);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle.get("toDoListArrayList") != null) {
                        toDoListArrayList = (ArrayList<ToDoList>) bundle.get("toDoListArrayList");
                    }
                    ToDoListNames = bundle.getStringArrayList("toDoListArrayListNames");
                    if (bundle.get("allModules") != null) {
                        allModules = (ArrayList<module>) bundle.get("allModules");
                    }
                }
            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    Bundle bundle = data.getExtras();
                    if(bundle.getBoolean("delete")) {
                        Event e = (Event) bundle.get("event");
                        if(e != null) {
//                            int i = events.get(e.getDay()).indexOf(e);
//                            events.get(e.getDay()).remove(i);
                            events.get(e.getDay()).remove(e);
//                            events.put(e.getDay(), temp);
                            Log.d("Day", e.getDay());
                            Log.d("Removed?", "Removed");
                            for(String key: events.keySet()) {
                                Log.d("Check Events", events.get(key).get(0).getName());
                            }
                            removeEventViews();
                            displayDailyEvents(e.getDay());
                        }

                        updateCalendar();
                        saveEvents();
                    }
                }
            }
        }
    }


    public void openContacts() {
        Intent intent = new Intent(this, Contacts.class);
        startActivity(intent);
    }


    //Form stuff
    public void clearFields() {
        eventName.setText("");
        eventLocation.setText("");
        eventStart.setText("");
        eventEnd.setText("");
        allDay.setChecked(false);
    }

    public void openForm() {
//        formScroll.setVisibility(View.VISIBLE);
//        formScroll.bringToFront();
        eventForm.setContentView(R.layout.event_form);
        LinearLayout f = eventForm.findViewById(R.id.form);
//        setupUI(eventForm.findViewById(R.id.form));
        eventName = eventForm.findViewById(R.id.eventName);
        eventLocation = eventForm.findViewById(R.id.eventLocation);
        eventStart = eventForm.findViewById(R.id.startTime);
        eventEnd = eventForm.findViewById(R.id.endTime);
        submitButton = eventForm.findViewById(R.id.submit);
        allDay = eventForm.findViewById(R.id.allDay);

        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        Button cancel = eventForm.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventForm.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
                eventForm.dismiss();
            }
        });
        eventForm.show();
    }

    public void cancelForm() {
        //Clear all stuff
//        formScroll.setVisibility(View.INVISIBLE);
        clearFields();
    }

    public void submitForm() {
        parseData();
        clearFields();
//        formScroll.setVisibility(View.INVISIBLE);
        updateCalendar();
    }

    public void updateCalendar() {
        calendarFragment.updateCalendar();
    }

    public void parseData() {
        String name = eventName.getText().toString();
        String location = eventLocation.getText().toString();
        String startTime = eventStart.getText().toString();
        String endTime = eventEnd.getText().toString();
        boolean allD = allDay.isChecked();

        Log.d("startTime", "parseData: " + startTime);
        String startTimeTime = startTime.substring(11, 15);

        Event newEvent = new Event(startTime.substring(0, 10), name, location, allD, startTimeTime, endTime, false, "I have stuff to do");
        addEvent(newEvent, null, startTime.substring(0, 10), 1);

//        int year = Integer.parseInt(startTime.substring(0, 4));
//        int dayOfYear = 0;
//        Node temp = eventDates[dayOfYear];
//        if(temp == null) eventDates[dayOfYear] = new Node(year, newEvent, null);
//        else {
//            Node prev = null;
//            while (temp != null && temp.year <= year) {
//                if(temp.year == year) {
//                    if(Integer.parseInt(temp.event.start) > Integer.parseInt(startTime)) {
//                        break;
//                    }
//                }
//                prev = temp;
//                temp = temp.next;
//
//            }
//            if(prev == null) {
//                eventDates[dayOfYear] = new Node(year, newEvent, temp);
//            } else {
//                prev.next = new Node(year, newEvent, null);
//                prev.next.next = temp;
//            }
//        }
    }

    public void addEvent(Event event, Date date, String SDate, int method) {
        if(method == 0) {
            if (events.containsKey((String) DateFormat.format("yyyy MM dd", date))) {
                ArrayList<Event> temp = events.get((String) DateFormat.format("yyyy MM dd", date));
                boolean inserted = false;
                for(int i = 0; i < temp.size(); i++) {
                    int indexStart = Integer.parseInt(temp.get(i).getStart());
                    int indexEnd = Integer.parseInt(temp.get(i).getEnd());
                    int eventStart = Integer.parseInt(event.getStart());
                    int eventEnd = Integer.parseInt(event.getEnd());
                    if(!temp.get(i).busy && ((indexStart <= eventStart && eventEnd <= indexEnd) ||
                            (indexStart > eventStart && eventEnd >= indexStart) ||
                            (eventStart < indexEnd && eventEnd > indexEnd))) {
                        //TODO: Refine nonbusy logic
                        temp.add(i, event);
                        inserted = true;
                        break;
                    }
                    if(indexStart > eventStart) {
                        temp.add(i, event);
                        inserted = true;
                        break;
                    }
                }
                if(!inserted) {
                    temp.add(event);
                }
            } else {
                ArrayList<Event> newEventList = new ArrayList<>();
                newEventList.add(event);
                events.put((String) DateFormat.format("yyyy MM dd", date), newEventList);
            }
        } else {
            if (events.containsKey(SDate)) {
                ArrayList<Event> temp = events.get(SDate);
                boolean inserted = false;
                for(int i = 0; i < temp.size(); i++) {
                    int indexStart = Integer.parseInt(temp.get(i).getStart());
                    int indexEnd = Integer.parseInt(temp.get(i).getEnd());
                    int eventStart = Integer.parseInt(event.getStart());
                    int eventEnd = Integer.parseInt(event.getEnd());
                    if(!temp.get(i).busy && ((indexStart <= eventStart && eventEnd <= indexEnd) ||
                            (indexStart > eventStart && eventEnd >= indexStart) ||
                            (eventStart < indexEnd && eventEnd > indexEnd))) {
                        //TODO: Refine nonbusy logic
                        temp.add(i, event);
                        inserted = true;
                        break;
                    }
                    if(indexStart > eventStart) {
                        temp.add(i, event);
                        inserted = true;
                        break;
                    }
                }
                if(!inserted) {
                    temp.add(event);
                }
            } else {
                ArrayList<Event> newEventList = new ArrayList<>();
                newEventList.add(event);
                events.put(SDate, newEventList);
            }
        }
        saveEvents();
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

    //Speech Recognition
    private void initializeTextToSpeech() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(myTTS.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "No TTS Engine on device", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    myTTS.setLanguage(Locale.US);
                    speak("Hello, I am ready.");
                }
            }
        });
    }

    private void speak(String s) {
        if(Build.VERSION.SDK_INT >= 21) {
            myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        myTTS.shutdown();
//    }

    private void initializeSpeechRecognizer() {
        if(SpeechRecognizer.isRecognitionAvailable(this)) {
            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d("Active", "onBeginningOfSpeech: Yes");
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> r = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if(r != null) processResult(r.get(0));
                    Log.d("Listen", "onResults: did it");
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String command) {
        //This is where we can parse the input
        String[] split = command.split("\\s+");
        Stack<String> commandStack = new Stack<>();
        Stack<String> operandStack = new Stack<>();
        for(int i = split.length-1; i >= 0; i--) {
            switch(split[i].toLowerCase()) {
                case "add":
                case "delete":
                case "select":
                    commandStack.push(split[i].toLowerCase());
                default:
                    operandStack.push(split[i].toLowerCase());
                    break;
            }
        }

        while(!commandStack.isEmpty()) {
            String operCommand = commandStack.pop();
            switch(operCommand) {
                case "add":
                    String name = "";
                    String temp = operandStack.pop();
                    while(!operandStack.isEmpty() && !temp.equals("to")) {
                        name += temp;
                        temp = operandStack.pop();
                    }
                    if(temp.equals("to")) {
                        //
                    } else {

                    }
                    break;
                case "delete":
                    //
                    break;
                case "select":
                    //
                    break;
                default:
                    break;
            }
        }
    }

    private void requestAudioPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                Toast.makeText(this, "grant permissions", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeTextToSpeech();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    //Fragment and bottom sheets
    @Override
    public void setBottomSheetState(int i, Date date) {
        mBottomSheetBehavior.setState(i);
        String dayOfMonth = (String) DateFormat.format("d", date);
        String month = (String) DateFormat.format("LLLL", date);
        TextView monthDay = findViewById(R.id.day_of_month);
        monthDay.setText(dayOfMonth);
        TextView monthView = findViewById(R.id.month);
        monthView.setText(month);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        removeEventViews();
//        mLayout.removeViewAt(eventIndex - 1);
        displayDailyEvents(DateFormat.format("yyyy MM dd", date).toString());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //Saving Events to Memory
    public void saveEvents() {
        File directory = getFilesDir();
        File yourFile = new File(directory, EVENT_FILE);
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
                if(events != null) {
                    fos = openFileOutput(EVENT_FILE, MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(events);
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

    public void loadEvents() {
        File directory = getFilesDir();
        File yourFile = new File(directory, EVENT_FILE);
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            events = new HashMap<>();
        } else {
            FileInputStream fis = null;

            try {
                fis = openFileInput(EVENT_FILE);
                File lengthCheck = getFileStreamPath(EVENT_FILE);
                if(lengthCheck.length() == 0) {
                    events = new HashMap<>();
                } else {
                    Log.d("File Check", "loadEvents: " + lengthCheck.length());
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    events = (HashMap<String, ArrayList<Event>>) ois.readObject();
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String text;
//
//            while((text = br.readLine()) != null) {
//                sb.append(text).append("\n");
//            }
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
        if(events == null) {
            Log.d(TAG, "loadEvents: Failed");
        }
        ((DataStorage)this.getApplication()).setEvents(events);
        
    }

    //Saving ToDoLists to memory
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
                if(events != null) {
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
                    Log.d("File Check", "loadEvents: " + lengthCheck.length());
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
                    Log.d("File Check", "loadEvents: " + lengthCheck.length());
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

    //OpenWeatherMap API
    public void find_weather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=boston,us&appid=ad1df4f64a5e8c772af19ba2fd8a7941";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String mainDescription = object.getString("main");
                    String description = object.getString("description");
                    String city = response.getString("name");

                    int tempInt = (int)((Float.parseFloat(temp) - 273)*9/5 + 32);
                    testTemp.setText(Integer.toString(tempInt) + ((char) 0x00B0) + "F");
                    testDescription.setText(description);

                    ImageView weatherIcon = findViewById(R.id.weatherIcon);
                    //"clear sky", "few clouds", "scattered clouds", "broken clouds", "shower rain", "rain", "thunderstorm", "snow", "mist"
                    switch(mainDescription) {
                        case "Clear":
                            weatherIcon.setImageResource(R.drawable.oned);
                            break;
                        case "few clouds":
                            weatherIcon.setImageResource(R.drawable.twod);
                            break;
                        case "Clouds":
                            weatherIcon.setImageResource(R.drawable.threed);
                            break;
                        case "broken clouds":
                            weatherIcon.setImageResource(R.drawable.fourd);
                            break;
                        case "Drizzle":
                            weatherIcon.setImageResource(R.drawable.fived);
                            break;
                        case "Rain":
                            weatherIcon.setImageResource(R.drawable.sixd);
                            break;
                        case "Thunderstorm":
                            weatherIcon.setImageResource(R.drawable.sevend);
                            break;
                        case "Snow":
                            weatherIcon.setImageResource(R.drawable.eightd);
                            break;
                        case "Mist":
                            weatherIcon.setImageResource(R.drawable.nined);
                            break;
                        default:
                            weatherIcon.setImageResource(R.drawable.oned);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    public void get_forecast() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=boston,us&appid=ad1df4f64a5e8c772af19ba2fd8a7941";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    int day = 0;
                    for(int i = 0; i < list.length(); i++) {
                        JSONObject timeItem = (JSONObject) list.get(i);
                        JSONObject main_object = timeItem.getJSONObject("main");
                        JSONArray array = timeItem.getJSONArray("weather");
                        JSONObject object = array.getJSONObject(0);
                        String temp = String.valueOf(main_object.getDouble("temp"));
                        String mainDescription = object.getString("main");
                        String description = object.getString("description");
                        String city = response.getString("name");

                        int tempInt = (int) ((Float.parseFloat(temp) - 273) * 9 / 5 + 32);

                        String date = timeItem.getString("dt_txt");
                        //Format: 2017-01-30 21:00:00
                        if(date.substring(5, 10).equals("02-29")) day = 366;
                        else {
                            GregorianCalendar tempCal = new GregorianCalendar(2009, Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
                            day = tempCal.get(GregorianCalendar.DAY_OF_YEAR);
                        }
                        int time = Integer.parseInt(date.substring(11, 13));
                        if(weatherDays[day][time/3] == null) {
                            weatherDays[day][time/3] = new WeatherObject(tempInt, description);
                        } else {
                            weatherDays[day][time/3].temp = tempInt;
                            weatherDays[day][time/3].description = description;
                        }
                    }
                    currentLimit = (day+1)%365;
                    //"clear sky", "few clouds", "scattered clouds", "broken clouds", "shower rain", "rain", "thunderstorm", "snow", "mist"
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    public WeatherObject[][] getWeatherDays() {
        return weatherDays;
    }

    public int getCurrentLimit() {
        return currentLimit;
    }

    //Show calendar events in day schedule
//    private void previousCalendarDate(){
//        mLayout.removeViewAt(eventIndex - 1);
//        cal.add(Calendar.DAY_OF_MONTH, -1);
//        currentDate.setText(displayDateInString(cal.getTime()));
//        displayDailyEvents();
//    }
//    private void nextCalendarDate(){
//        mLayout.removeViewAt(eventIndex - 1);
//        cal.add(Calendar.DAY_OF_MONTH, 1);
//        currentDate.setText(displayDateInString(cal.getTime()));
//        displayDailyEvents();
//    }

    private String displayDateInString(Date mDate){
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM, yyyy", Locale.ENGLISH);
        return formatter.format(mDate);
    }

    private void displayDailyEvents(String day){
        ArrayList<Event> dailyEvent = getDayEvents(day);
        if(dailyEvent != null) {
            for (Event eObject : dailyEvent) {
                String eventDate = eObject.getStart();
                String endDate = eObject.getEnd();
                Event event = eObject;
                int eventBlockHeight = getEventTimeFrame(eventDate, endDate);
                Log.d(TAG, "Height " + eventBlockHeight);
                displayEventSection(eventDate, eventBlockHeight, event);
            }
        }
    }

    private ArrayList<Event> getDayEvents(String day) {
        return events.get(day);
    }

    private int getEventTimeFrame(String start, String end){
        int startInt = Integer.parseInt(start);
        int endInt = Integer.parseInt(end);
        int timeDifference = (endInt/100)*60 + endInt%100 - ((startInt/100)*60 + startInt%100);
        int hours = timeDifference/60;
        int minutes = timeDifference%60;
        return (hours * 60) + minutes;
//        ((minutes * 60) / 100)
    }
    private void displayEventSection(String eventDate, int height, Event event){
        int hours = Integer.parseInt(eventDate)/100;
        int minutes = Integer.parseInt(eventDate)%100;
        Log.d(TAG, "Hour value " + hours);
        Log.d(TAG, "Minutes value " + minutes);
        int topViewMargin = (hours * 60) + minutes;
//        ((minutes * 60) / 100)
        Log.d(TAG, "Margin top " + topViewMargin);
        createEventView(topViewMargin, height, event);
    }
    private void createEventView(int topMargin, int height, final Event event){
        //TODO: Edit this view
        float factor =  getResources().getDisplayMetrics().density;
        TextView mEventView = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(800, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = (int)(topMargin * factor); //2
        lParam.leftMargin = 24;
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight((int)(height * factor)); //2 original value
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#ffffff"));
        mEventView.setText(event.getName());
        mEventView.setBackgroundColor(Color.parseColor("#3F51B5"));
        mEventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventActivity(event);
            }
        });
        mLayout.addView(mEventView, eventIndex - 1);
    }

    private void removeEventViews() {
        while(mLayout.getChildCount() > numViews) {
            mLayout.removeViewAt(mLayout.getChildCount()-2);
        }
    }

    private void openEventActivity(Event event) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event", event);
        startActivityForResult(intent, 2);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}

//    private Node[] eventDates = new Node[366]; //Feb 29 = 366 (technically 365)