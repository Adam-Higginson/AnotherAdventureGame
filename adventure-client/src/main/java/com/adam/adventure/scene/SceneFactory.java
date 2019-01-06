package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.KeyboardListenerComponent;
import com.adam.adventure.entity.component.UiConsoleComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.ui.UiManager;
import com.adam.adventure.scene.event.NewSceneEvent;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class SceneFactory {
    static final String START_MENU_SCENE_NAME = "StartMenu";

    private final EventBus eventBus;
    private final Renderer renderer;
    private final InputManager inputManager;
    private final UiManager uiManager;

    SceneFactory(final EventBus eventBus,
                 final Renderer renderer,
                 final InputManager inputManager,
                 final UiManager uiManager) {
        this.eventBus = eventBus;
        this.renderer = renderer;
        this.inputManager = inputManager;
        this.uiManager = uiManager;
    }

    public Scene createStartScene() {
        final Entity keyboardListenerEntity = new Entity("Keyboard Listener")
                .addComponent(new KeyboardListenerComponent(
                        inputManager,
                        GLFW_KEY_ESCAPE,
                        this::onEscapePressed));

        return createScene(START_MENU_SCENE_NAME).addEntity(keyboardListenerEntity);
    }

    private void onEscapePressed() {
        eventBus.publishEvent(new NewSceneEvent("Test Scene"));
    }

    public Scene createScene(final String name) {
        final Entity commandConsole = new Entity("Command Console")
                .addComponent(new UiConsoleComponent(inputManager, uiManager));

        return new Scene(eventBus, name, renderer).addEntity(commandConsole);
    }
}
