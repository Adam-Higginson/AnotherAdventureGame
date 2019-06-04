package com.adam.adventure.render.ui;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.window.Window;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.render.batch.core.BatchRenderBackendCoreProfileInternal;
import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;
import de.lessvoid.nifty.render.batch.spi.MouseCursorFactory;
import de.lessvoid.nifty.renderer.lwjgl3.render.*;
import de.lessvoid.nifty.renderer.lwjgl3.time.Lwjgl3TimeProvider;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;

/**
 * Handles Nifty GUI and all attached elements.
 */
public class UiManager {

    public static final String BASE_SCREEN_ID = "base";
    public static final String BASE_LAYER_ID = "baseLayer";
    private final Nifty nifty;
    private final Screen baseScreen;

    @Inject
    public UiManager(final Window window, final InputManager inputManager, final RenderQueue renderQueue) {
        final RenderDevice renderDevice = new BatchRenderDevice(buildBatchRenderBackend(window));
        this.nifty = new Nifty(renderDevice,
                new NullSoundDevice(),
                inputManager.getLwjglInputSystem(),
                new Lwjgl3TimeProvider());
        this.baseScreen = buildScreen(nifty);

        renderQueue.addRenderable(new NiftyUiRenderable(nifty));
    }


    private static BatchRenderBackend buildBatchRenderBackend(final Window window) {
        return new BatchRenderBackendCoreProfileInternal(
                new Lwjgl3CoreGL(),
                new Lwjgl3BufferFactory(),
                new Lwjgl3ImageFactory(),
                new TestMouseCursorFactory());
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

    private static class TestMouseCursorFactory implements MouseCursorFactory {

        @Nullable
        @Override
        public MouseCursor create(@Nonnull String filename, int hotspotX, int hotspotY, @Nonnull NiftyResourceLoader resourceLoader) throws IOException {
            return null;
        }
    }
}
