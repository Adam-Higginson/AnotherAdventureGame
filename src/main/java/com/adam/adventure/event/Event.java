package com.adam.adventure.event;

public class Event {
    private final EventType eventType;

    public Event(final EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}

