package com.example.alexh.ajenda;

import java.io.Serializable;

public class Event implements Serializable {
    private String name;
    private String location;
    private boolean allDay;
    private String start; //Should be in format HHMM (No colon)
    private String end; //Same
    private String unit;
    private String description;
    private String day;

    //Repeat?
    boolean busy; //Check if we can schedule stuff during this time

    public Event(String day, String name, String location, boolean allDay, String start, String end,
                 boolean busy, String description) {
        this.name = name;
        this.location = location;
        this.allDay = allDay;
        this.start = start;
        this.end = end;
        this.busy = busy;
        this.description = description;
        this.day = day;
    }

    //Getters
    public String getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isBusy() {
        return busy;
    }

    public String getDescription() {
        return description;
    }

    //Setters
    public void setDay(String day) {
        this.day = day;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        else if(o == null || o.getClass()!= this.getClass())
            return false;
        else {
            Event e = (Event) o;
            return (name.equals(e.getName()) && location.equals(e.getLocation()) && allDay == e.isAllDay()
                    && start.equals(e.getStart()) && end.equals(e.getEnd())
                    && description.equals(e.getDescription()) && day.equals(e.getDay()));
        }
    }

}
