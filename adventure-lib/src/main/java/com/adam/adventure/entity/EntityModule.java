package com.adam.adventure.entity;

import com.google.inject.AbstractModule;

import javax.inject.Singleton;

public class EntityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityFactory.class).to(EntityFactoryImpl.class).in(Singleton.class);
    }

}
