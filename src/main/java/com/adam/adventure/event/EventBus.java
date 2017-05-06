package com.adam.adventure.event;

import java.util.ArrayList;
import java.util.List;

//Potentially have the event bus as a background thread
public class EventBus {
    private final List<EventListener> eventListeners;

    public EventBus() {
        this.eventListeners = new ArrayList<>();
    }

    public EventBus registerEventListener(final EventListener eventListener) {
        eventListeners.add(eventListener);
        return this;
    }

    public void publishEvent(final Event event) {
        eventListeners.forEach(eventListener -> eventListener.onEvent(event));
    }
}
