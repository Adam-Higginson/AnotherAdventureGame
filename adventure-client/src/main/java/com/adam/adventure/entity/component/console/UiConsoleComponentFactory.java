package com.adam.adventure.entity.component.console;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.RequestConnectionToServerEvent;

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
                    if (args.length < 4) {
                        console.writeError("Usage: connect <username> <address> <port>");
                    } else {
                        eventBus.publishEvent(new RequestConnectionToServerEvent(args[1], args[2], Integer.parseInt(args[3])));
                    }
                });
    }
}
