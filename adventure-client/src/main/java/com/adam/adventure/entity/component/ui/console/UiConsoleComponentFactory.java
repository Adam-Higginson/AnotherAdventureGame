package com.adam.adventure.entity.component.ui.console;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.RequestConnectionToServerEvent;
import com.adam.adventure.scene.NewSceneEvent;
import com.adam.adventure.scene.NoSceneFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;

import static com.adam.adventure.event.WriteUiConsoleInfoEvent.consoleInfoEvent;

public class UiConsoleComponentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(UiConsoleComponentFactory.class);

    private final EventBus eventBus;
    private final ServerConsoleCommand serverConsoleCommand;

    @Inject
    public UiConsoleComponentFactory(final EventBus eventBus, final ServerConsoleCommand serverConsoleCommand) {
        this.eventBus = eventBus;
        this.serverConsoleCommand = serverConsoleCommand;
    }

    public UiConsoleComponent buildDefaultUiConsoleComponent() {
        return new UiConsoleComponent()
                .addConsoleCommand("connect", (console, args) -> {
                    if (args.length < 4) {
                        console.writeError("Usage: connect <username> <address> <port>");
                    } else {
                        eventBus.publishEvent(new RequestConnectionToServerEvent(args[1], args[2], Integer.parseInt(args[3])));
                    }
                })
                .addConsoleCommand("server", serverConsoleCommand)
                .addConsoleCommand("c", (console, args) ->
                        eventBus.publishEvent(new RequestConnectionToServerEvent(
                                "TEST-" + ThreadLocalRandom.current().nextInt(),
                                "localhost",
                                50)))
                .addConsoleCommand("scene", (console, args) -> {
                    if (args.length < 2) {
                        console.writeError("Usage: scene <scene_name>");
                    } else {
                        try {
                            eventBus.publishEvent(new NewSceneEvent(args[1]));
                            eventBus.publishEvent(consoleInfoEvent("Scene changed to: " + args[1]));
                        }
                        catch (final NoSceneFoundException e) {
                            console.writeError("No scene found for name: " + args[1]);
                            LOG.error("No scene found", e);
                        }
                    }
                });
    }
}
