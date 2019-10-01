package com.adam.adventure.event;

import com.adam.adventure.entity.component.console.ConsoleEvent;
import com.adam.adventure.entity.component.console.UiConsoleComponent;
import de.lessvoid.nifty.tools.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteUiConsoleInfoEvent extends ConsoleEvent {
    private static final Color GREEN = new Color(0.f, 1.0f, 0.f, 1.f);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String message;

    public WriteUiConsoleInfoEvent(final String message) {
        this.message = LocalDateTime.now().format(DATE_TIME_FORMATTER) + " [INFO] " + message;
    }

    public static ConsoleEvent consoleInfoEvent(final String message) {
        return new WriteUiConsoleInfoEvent(message);
    }


    @Override
    public void handle(final UiConsoleComponent console) {
        console.writeLine(message, GREEN);
    }

}
