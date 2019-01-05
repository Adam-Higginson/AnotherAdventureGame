package com.adam.adventure.entity.component.factory;

import com.adam.adventure.entity.component.ComponentContainer;

@FunctionalInterface
public interface EntityComponentFactory {

    void registerNewInstanceWithContainer(final ComponentContainer componentContainer);
}
