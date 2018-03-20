package com.kadek.tripgo;

/**
 * Created by K on 11/03/2018.
 */

public class Event {


    public String name;
    public String event_image;
    public String event_start;
    public String event_end;
    public String event_thumb_image;
    public String description;

    public Event(String name, String event_image, String event_start, String event_end, String event_thumb_image, String description) {
        this.name = name;
        this.event_image = event_image;
        this.event_start = event_start;
        this.event_end = event_end;
        this.event_thumb_image = event_thumb_image;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public String getEvent_start() {
        return event_start;
    }

    public void setEvent_start(String event_start) {
        this.event_start = event_start;
    }

    public String getEvent_end() {
        return event_end;
    }

    public void setEvent_end(String event_end) {
        this.event_end = event_end;
    }

    public String getEvent_thumb_image() {
        return event_thumb_image;
    }

    public void setEvent_thumb_image(String event_thumb_image) {
        this.event_thumb_image = event_thumb_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event() {
    }

}
