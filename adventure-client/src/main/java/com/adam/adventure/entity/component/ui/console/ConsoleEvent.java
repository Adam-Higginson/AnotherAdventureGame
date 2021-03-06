package com.adam.adventure.entity.component.ui.console;

import com.adam.adventure.event.Event;

public abstract class ConsoleEvent extends Event {

    public abstract void handle(final UiConsoleComponent console);
}
