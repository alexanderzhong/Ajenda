package com.example.alexh.ajenda;
import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class DataStorage extends Application {
    private HashMap<String, ArrayList<Event>> events;

    public HashMap<String, ArrayList<Event>> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, ArrayList<Event>> events) {
        this.events = events;
    }
}
