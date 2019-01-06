package com.adam.adventure.entity;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class EntityModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Entity.class, Entity.class)
                .build(EntityFactory.class));
    }
}
