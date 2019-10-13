package com.adam.adventure.event;

public class UncaughtThrowableEvent extends Event {
    private final Throwable throwable;

    UncaughtThrowableEvent(final Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
