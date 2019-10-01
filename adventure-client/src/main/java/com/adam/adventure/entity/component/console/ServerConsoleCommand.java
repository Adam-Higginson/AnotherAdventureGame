package com.adam.adventure.entity.component.console;

import com.adam.adventure.entity.component.network.event.ServerCommandEvent;
import com.adam.adventure.event.EventBus;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerConsoleCommand implements ConsoleCommand {

    private final EventBus eventBus;

    @Inject
    public ServerConsoleCommand(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void execute(final UiConsoleComponent console, final String... args) {
        if (args.length < 2) {
            console.writeError("Usage: server <command> <args>");
            return;
        }

        final String command = Arrays.stream(args, 1, args.length)
                .collect(Collectors.joining(" "));
        eventBus.publishEvent(new ServerCommandEvent(command));
    }
}
