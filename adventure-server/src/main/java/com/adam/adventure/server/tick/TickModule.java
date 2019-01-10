package com.adam.adventure.server.tick;

import com.adam.adventure.server.tick.event.processor.ProcessorModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TickModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(ServerTickScheduler.class, ServerTickScheduler.class)
                .build(ServerTickSchedulerFactory.class));

        install(new ProcessorModule());
    }
}
