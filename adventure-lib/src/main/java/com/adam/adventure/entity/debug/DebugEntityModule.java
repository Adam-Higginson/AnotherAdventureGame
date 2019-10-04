package com.adam.adventure.entity.debug;

import com.adam.adventure.entity.EntityFactory;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;

public class DebugEntityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityFactory.class).to(DebugEntityFactory.class).in(Singleton.class);
    }
}
