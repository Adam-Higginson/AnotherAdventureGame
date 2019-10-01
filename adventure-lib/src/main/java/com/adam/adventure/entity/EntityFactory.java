package com.adam.adventure.entity;

import com.google.inject.Injector;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityFactory {

    private final Injector injector;
    private final AtomicInteger currentEntityId = new AtomicInteger();

    @Inject
    EntityFactory(final Injector injector) {
        this.injector = injector;
    }

    public Entity create(final String name) {
        return new Entity(name, currentEntityId.incrementAndGet(), injector);
    }
}
