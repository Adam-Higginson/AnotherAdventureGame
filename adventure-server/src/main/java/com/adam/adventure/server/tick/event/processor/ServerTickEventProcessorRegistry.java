package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.server.event.ClientReadyEvent;
import com.adam.adventure.server.event.NewPlayerEvent;
import com.adam.adventure.server.tick.event.EntityTransformEvent;
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
        put(ClientReadyEvent.class, injector.getInstance(ClientReadyEventProcessor.class));
        put(EntityTransformEvent.class, injector.getInstance(EntityTransformEventProcessor.class));
    }

    public void process(final ServerTickEvent obj) {
        process(obj.getClass(), obj);
    }

    private <T extends ServerTickEvent> Consumer<? super T> put(final Class<T> key, final Consumer<? super T> c) {
        return eventTypeToConsumer.put(key, o -> c.accept(key.cast(o)));
    }

    private <T extends ServerTickEvent> void process(final Class<T> key, final Object obj) {
        final Consumer<? super T> c = get(key);
        if (c != null) {
            c.accept(key.cast(obj));
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ServerTickEvent> Consumer<? super T> get(final Class<T> key) {
        return eventTypeToConsumer.get(key);
    }
}
