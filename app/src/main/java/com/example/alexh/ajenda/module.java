package com.example.alexh.ajenda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class module implements Serializable {
    private String moduleName;
    private int priority;
    private ArrayList<String> dates; //Event name : Dates
    private int preferredWeather;
    private String location;
    private int drivingTime;
    private int eventTime;
    private String unit;
    private String timeRangeStart;
    private String timeRangeEnd;
    private boolean inserted;


    private module() {

    }

    private module(String name, int p, int pw, String l, int dt, int et,
                   String u, String ts, String te) {
        moduleName = name;
        priority = p;
        preferredWeather = pw;
        location = l;
        drivingTime = dt;
        eventTime = et;
        unit = u;
        timeRangeStart = ts;
        timeRangeEnd = te;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    public int getPreferredWeather() {
        return preferredWeather;
    }

    public void setPreferredWeather(int preferredWeather) {
        this.preferredWeather = preferredWeather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    public int getEventTime() {
        return eventTime;
    }

    public void setEventTime(int eventTime) {
        this.eventTime = eventTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimeRangeStart() {
        return timeRangeStart;
    }

    public void setTimeRangeStart(String timeRangeStart) {
        this.timeRangeStart = timeRangeStart;
    }

    public String getTimeRangeEnd() {
        return timeRangeEnd;
    }

    public void setTimeRangeEnd(String timeRangeEnd) {
        this.timeRangeEnd = timeRangeEnd;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }
}
