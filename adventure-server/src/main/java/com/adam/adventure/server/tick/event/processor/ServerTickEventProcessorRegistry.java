package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.server.event.NewPlayerEvent;
import com.adam.adventure.server.tick.event.ServerTickEvent;
import com.google.inject.Injector;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ServerTickEventProcessorRegistry {

    private final Map<Class<?>, Consumer<ServerTickEvent>> eventTypeToConsumer;

    @Inject
    ServerTickEventProcessorRegistry(final Injector injector) {
        this.eventTypeToConsumer = new HashMap<>();
        put(NewPlayerEvent.class, injector.getInstance(NewPlayerEventProcessor.class));
    }

    public void process(ServerTickEvent obj) {
        process(obj.getClass(), obj);
    }

    private <T extends ServerTickEvent> Consumer<? super T> put(Class<T> key, Consumer<? super T> c) {
        return eventTypeToConsumer.put(key, o -> c.accept(key.cast(o)));
    }

    private <T extends ServerTickEvent> void process(Class<T> key, Object obj) {
        Consumer<? super T> c = get(key);
        if (c != null) {
            c.accept(key.cast(obj));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ServerTickEvent> Consumer<? super T> get(Class<T> key) {
        return eventTypeToConsumer.get(key);
    }
}
