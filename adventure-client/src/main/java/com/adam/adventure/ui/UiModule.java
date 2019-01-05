package com.adam.adventure.ui;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.window.Window;
import com.google.inject.AbstractModule;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3BatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

public class UiModule extends AbstractModule {

    private final Window window;
    private final InputManager inputManager;

    public UiModule(final Window window, final InputManager inputManager) {
        this.window = window;
        this.inputManager = inputManager;
    }

    @Override
    protected void configure() {
        final RenderDevice renderDevice = new BatchRenderDevice(Lwjgl3BatchRenderBackendCoreProfileFactory.create(window.getWindowHandle()));

        final Nifty nifty = new Nifty(renderDevice,
                new NullSoundDevice(),
                inputManager.getLwjflInputSystem(),
                new AccurateTimeProvider());

        bind(Nifty.class).toInstance(nifty);
    }


}
