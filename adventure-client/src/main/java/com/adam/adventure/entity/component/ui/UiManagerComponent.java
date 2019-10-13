package com.adam.adventure.entity.component.ui;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.NiftyUiRenderable;
import com.adam.adventure.render.renderable.Renderable;
import com.adam.adventure.window.WindowResizeEvent;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;
import de.lessvoid.nifty.render.batch.BatchRenderDevice;
import de.lessvoid.nifty.render.batch.core.BatchRenderBackendCoreProfileInternal;
import de.lessvoid.nifty.render.batch.spi.BatchRenderBackend;
import de.lessvoid.nifty.render.batch.spi.MouseCursorFactory;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3BufferFactory;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3CoreGL;
import de.lessvoid.nifty.renderer.lwjgl3.render.Lwjgl3ImageFactory;
import de.lessvoid.nifty.renderer.lwjgl3.time.Lwjgl3TimeProvider;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles Nifty GUI and all attached elements.
 */
public class UiManagerComponent extends EntityComponent {

    public static final String BASE_SCREEN_ID = "base";
    public static final String BASE_LAYER_ID = "baseLayer";

    private Nifty nifty;
    private Screen baseScreen;

    private Renderable niftyRenderable;

    @Inject
    private InputManager inputManager;

    @Inject
    private RenderQueue renderQueue;

    @Inject
    private EventBus eventBus;


    @Override
    protected void activate() {
        //Disable annoying nifty logging
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        final BatchRenderDevice renderDevice = new BatchRenderDevice(buildBatchRenderBackend());
        nifty = new Nifty(renderDevice,
                new NullSoundDevice(),
                inputManager.getLwjglInputSystem(),
                new Lwjgl3TimeProvider());
        nifty.resolutionChanged();
        baseScreen = buildScreen(nifty);
        eventBus.register(this);
    }

    @Override
    protected void afterActivate() {
        niftyRenderable = new NiftyUiRenderable(nifty);
        renderQueue.initialiseRenderable(niftyRenderable);
    }

    @Override
    protected void update(final float deltaTime) {
        renderQueue.addRenderableToBeRendered(niftyRenderable);
    }

    @Override
    protected void afterUpdate(final float deltaTime) {
        nifty.update();
    }

    @Override
    protected void destroy() {
        eventBus.unsubscribe(this);
        nifty.exit();
    }

    @EventSubscribe
    public void onWindowResize(final WindowResizeEvent windowResizeEvent) {
        nifty.resolutionChanged();
    }

    public Screen getBaseScreen() {
        return baseScreen;
    }

    public Nifty getNifty() {
        return nifty;
    }


    private static BatchRenderBackend buildBatchRenderBackend() {
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

    private static class TestMouseCursorFactory implements MouseCursorFactory {

        @Nullable
        @Override
        public MouseCursor create(@Nonnull final String filename, final int hotspotX, final int hotspotY, @Nonnull final NiftyResourceLoader resourceLoader) {
            return new MouseCursor() {

                @Override
                public void enable() {
                    //Do nothing
                }

                @Override
                public void disable() {
                    //Do nothing

                }

                @Override
                public void dispose() {
                    //Do nothing

                }
            };
        }
    }
}
