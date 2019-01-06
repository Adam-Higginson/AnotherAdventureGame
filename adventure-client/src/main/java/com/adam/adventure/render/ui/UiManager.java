package com.adam.adventure.render.ui;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.window.Window;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3BatchRenderBackendCoreProfileFactory;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

/**
 * Handles Nifty GUI and all attached elements.
 */
public class UiManager {

    public static final String BASE_SCREEN_ID = "base";
    public static final String BASE_LAYER_ID = "baseLayer";
    private final Window window;
    private final InputManager inputManager;
    private final Nifty nifty;
    private final Screen baseScreen;

    public UiManager(final Window window, final InputManager inputManager, final RenderQueue renderQueue) {
        this.window = window;
        this.inputManager = inputManager;

        final RenderDevice renderDevice = new BatchRenderDevice(Lwjgl3BatchRenderBackendCoreProfileFactory.create(window.getWindowHandle()));
        this.nifty = new Nifty(renderDevice,
                new NullSoundDevice(),
                inputManager.getLwjflInputSystem(),
                new AccurateTimeProvider());
        this.baseScreen = buildScreen(nifty);

        renderQueue.addRenderable(new NiftyUiRenderable(nifty));
    }

    private static Screen buildScreen(final Nifty nifty) {
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.loadStyleFile("nifty-default-styles.xml");

        nifty.addScreen(BASE_SCREEN_ID, new ScreenBuilder(BASE_SCREEN_ID)
                .layer(new LayerBuilder(BASE_LAYER_ID) {{
                    childLayoutVertical();
                    visible(true);
                }}).build(nifty));


        nifty.gotoScreen(BASE_SCREEN_ID);
        return nifty.getScreen(BASE_SCREEN_ID);
    }

    public Screen getBaseScreen() {
        return baseScreen;
    }

    public Nifty getNifty() {
        return nifty;
    }
}
