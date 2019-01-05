package com.adam.adventure.update.event;

import com.adam.adventure.event.Event;

public class NewLoopIterationEvent extends Event {
    private final float elapsedTime;

    public NewLoopIterationEvent(final float elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }


}
