package com.adam.adventure.ui;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.window.Window;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3BatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

public class UiManager {

    private final Nifty nifty;

    public UiManager(final Window window, final InputManager inputManager) {
        this.nifty = new Nifty(new BatchRenderDevice(Lwjgl3BatchRenderBackendCoreProfileFactory.create(window.getWindowHandle())),
                new NullSoundDevice(),
                inputManager.getLwjflInputSystem(),
                new AccurateTimeProvider());
    }
}
