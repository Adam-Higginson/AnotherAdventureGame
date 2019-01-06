package com.adam.adventure.event;

public class LockInputEvent extends Event {
    private final Object source;
    private final boolean locked;

    public LockInputEvent(final Object source, final boolean locked) {
        this.source = source;
        this.locked = locked;
    }

    public Object getSource() {
        return source;
    }

    public boolean isLocked() {
        return locked;
    }
}
