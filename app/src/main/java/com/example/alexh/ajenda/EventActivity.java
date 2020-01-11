package com.example.alexh.ajenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {
    Event event;

    //UI Components
    Button exit;
    Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Bundle bundle = getIntent().getExtras();
        event = (Event) bundle.get("event");

        TextView title = findViewById(R.id.eventTitle);
        title.setText(event.getName());
        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete = findViewById(R.id.deleteEvent);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("delete", true);
                resultIntent.putExtra("event", event);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        TextView description = findViewById(R.id.eventDescription);
        description.setText("Description: " + event.getDescription());
        TextView location = findViewById(R.id.eventLocation);
        location.setText("Location: " + event.getLocation());
        TextView startTime = findViewById(R.id.startTime);
        String st = event.getStart();
        startTime.setText("Start Time: " + st.substring(0, 2) + ":" + st.substring(2, 4));
        TextView endTime = findViewById(R.id.endTime);
        String en = event.getEnd();
        endTime.setText("End Time: " + en.substring(0, 2) + ":" + en.substring(2, 4));
        TextView allDay = findViewById(R.id.allDay);
        allDay.setText("All Day: " + Boolean.toString(event.isAllDay()));
    }
}
