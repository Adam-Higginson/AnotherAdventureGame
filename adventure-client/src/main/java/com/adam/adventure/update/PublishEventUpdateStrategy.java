package com.adam.adventure.update;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.update.event.NewLoopIterationEvent;

import javax.inject.Inject;

public class PublishEventUpdateStrategy implements UpdateStrategy {

    private final EventBus eventBus;

    @Inject
    public PublishEventUpdateStrategy(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void update(final float elapsedTime) {
        eventBus.publishEvent(new NewLoopIterationEvent(elapsedTime));
    }
}
