package com.adam.adventure.entity.component.console;

import com.adam.adventure.event.ConnectionRequestEvent;
import com.adam.adventure.event.EventBus;

import javax.inject.Inject;

public class UiConsoleComponentFactory {

    private final EventBus eventBus;

    @Inject
    public UiConsoleComponentFactory(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public UiConsoleComponent buildDefaultUiConsoleComponent() {
        return new UiConsoleComponent()
                .addConsoleCommand("connect", (console, args) -> {
                    if (args.length < 2) {
                        console.writeError("Usage: connect <address:port>");
                    } else {
                        eventBus.publishEvent(new ConnectionRequestEvent(args[1]));
                    }
                });
    }
}
