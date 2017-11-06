package com.example.tawin.testmapgps;

/**
 * Created by boss on 10/2/2017 AD.
 */

public class Events {
    int event_id;
    String title,description;


    public Events(int event_id, String description, String title) {
        this.event_id = event_id;
        this.title = title;
        this.description = description;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
