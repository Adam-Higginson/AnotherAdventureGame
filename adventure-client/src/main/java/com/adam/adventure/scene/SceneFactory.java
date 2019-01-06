package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.KeyboardListenerComponent;
import com.adam.adventure.entity.component.console.UiConsoleComponentFactory;
import com.adam.adventure.entity.component.network.NetworkManagerComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.scene.event.NewSceneEvent;

import javax.inject.Inject;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class SceneFactory {
    static final String START_MENU_SCENE_NAME = "StartMenu";

    private final EventBus eventBus;
    private final Renderer renderer;
    private final EntityFactory entityFactory;
    private final UiConsoleComponentFactory uiConsoleComponentFactory;

    @Inject
    SceneFactory(final EventBus eventBus,
                 final Renderer renderer,
                 final EntityFactory entityFactory,
                 final UiConsoleComponentFactory uiConsoleComponentFactory) {
        this.eventBus = eventBus;
        this.renderer = renderer;
        this.entityFactory = entityFactory;
        this.uiConsoleComponentFactory = uiConsoleComponentFactory;
    }

    public Scene createStartScene() {
        final Entity keyboardListenerEntity = entityFactory.create("Keyboard Listener")
                .addComponent(new KeyboardListenerComponent(
                        GLFW_KEY_ESCAPE,
                        this::onEscapePressed));

        final Entity networkEntity = entityFactory.create("Network manager")
                .addComponent(new NetworkManagerComponent());

        return createScene(START_MENU_SCENE_NAME)
                .addEntity(keyboardListenerEntity)
                .addEntity(networkEntity);
    }

    private void onEscapePressed() {
        eventBus.publishEvent(new NewSceneEvent("Test Scene"));
    }

    public Scene createScene(final String name) {
        final Entity commandConsole = entityFactory.create("Command Console")
                .addComponent(uiConsoleComponentFactory.buildDefaultUiConsoleComponent());

        return new Scene(eventBus, name, renderer).addEntity(commandConsole);
    }
}
