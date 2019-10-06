package com.adam.adventure.entity.debug;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.google.inject.Injector;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugEntityFactory implements EntityFactory {

    private final Injector injector;
    private final AtomicInteger currentEntityId = new AtomicInteger();

    @Inject
    DebugEntityFactory(final Injector injector) {
        this.injector = injector;
    }

    @Override
    public Entity create(final String name) {
        return new TimedEntity(name, currentEntityId.incrementAndGet(), injector);
    }
}
