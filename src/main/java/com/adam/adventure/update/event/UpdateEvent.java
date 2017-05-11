package com.adam.adventure.update.event;

import com.adam.adventure.event.Event;

public class UpdateEvent extends Event {
    private final float elapsedTime;

    public UpdateEvent(final float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }


}
