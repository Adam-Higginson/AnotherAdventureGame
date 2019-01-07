package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.console.UiConsoleComponentFactory;
import com.adam.adventure.entity.component.network.NetworkManagerComponent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.render.Renderer;

import javax.inject.Inject;

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
        final Entity networkEntity = entityFactory.create("Network manager")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(new NetworkManagerComponent());

        final Entity commandConsole = entityFactory.create("Command Console")
                .setShouldDestroyOnSceneChange(false)
                .addComponent(uiConsoleComponentFactory.buildDefaultUiConsoleComponent());


        return createScene(START_MENU_SCENE_NAME)
                .addEntity(networkEntity)
                .addEntity(commandConsole);
    }

    public Scene createScene(final String name) {
        return new Scene(eventBus, name, renderer);
    }
}
