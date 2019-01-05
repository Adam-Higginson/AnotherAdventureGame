package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.KeyboardListenerComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.scene.event.NewSceneEvent;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class StartSceneFactory {
    public static final String START_MENU_SCENE_NAME = "StartMenu";

    public Scene buildStartScene(final EventBus eventBus, final Renderer renderer, final InputManager inputManager) {
        final Scene startScene = new Scene(eventBus, START_MENU_SCENE_NAME, renderer);

        final Entity keyboardListenerEntity = new Entity("Keyboard Listener")
                .addComponent((container) -> {
                    final KeyboardListenerComponent keyboardListenerComponent = new KeyboardListenerComponent(container,
                            inputManager,
                            GLFW_KEY_ESCAPE,
                            () -> onEscapePressed(eventBus));
                    container.addComponent(keyboardListenerComponent);
                });
        startScene.addEntity(keyboardListenerEntity);
        return startScene;
    }

    private void onEscapePressed(final EventBus eventBus) {
        eventBus.publishEvent(new NewSceneEvent("Test Scene"));
    }
}
