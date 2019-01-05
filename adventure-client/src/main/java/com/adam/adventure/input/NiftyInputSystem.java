package com.adam.adventure.input;

import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class NiftyInputSystem implements InputSystem {
    private static final Logger LOG = LoggerFactory.getLogger(NiftyInputSystem.class);

    @Override
    public void setResourceLoader(@Nonnull NiftyResourceLoader niftyResourceLoader) {

    }

    @Override
    public void forwardEvents(@Nonnull NiftyInputConsumer inputEventConsumer) {

    }

    @Override
    public void setMousePosition(int x, int y) {

    }
}
