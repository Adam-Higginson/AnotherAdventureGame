package com.adam.adventure.scene;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.InitialisedEvent;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.ui.UiManager;
import com.adam.adventure.scene.event.NewSceneEvent;
import com.adam.adventure.scene.event.SceneTransitionEvent;
import com.adam.adventure.update.event.NewLoopIterationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.adam.adventure.scene.SceneFactory.START_MENU_SCENE_NAME;

public class SceneManager {
    private static final Logger LOG = LoggerFactory.getLogger(SceneManager.class);

    private Scene currentScene;
    private final EventBus eventBus;
    private final Renderer renderer;
    private final Map<String, Scene> sceneNameToSceneSupplier;
    private final SceneFactory sceneFactory;

    public SceneManager(final EventBus eventBus,
                        final Renderer renderer,
                        final InputManager inputManager,
                        final UiManager uiManager) {
        this.eventBus = eventBus;
        this.renderer = renderer;
        this.sceneNameToSceneSupplier = new HashMap<>();
        this.sceneFactory = new SceneFactory(eventBus, renderer, inputManager, uiManager);

        final Scene startScene = sceneFactory.createStartScene();
        sceneNameToSceneSupplier.put(startScene.getName(), startScene);

        eventBus.register(this);
    }

    public SceneManager addScene(final String sceneName, final Scene scene) {
        sceneNameToSceneSupplier.put(sceneName, scene);
        return this;
    }

    public SceneFactory getSceneFactory() {
        return sceneFactory;
    }

    @EventSubscribe
    public void onInitialisedEvent(final InitialisedEvent initialisedEvent) {
        LOG.info("Initialised event received, setting current scene to start scene.");
        eventBus.publishEvent(new NewSceneEvent(START_MENU_SCENE_NAME));
    }

    @EventSubscribe
    public void newSceneEvent(final NewSceneEvent newSceneEvent) {
        final Scene scene = sceneNameToSceneSupplier.get(newSceneEvent.getSceneName());
        eventBus.publishEvent(new SceneTransitionEvent(scene));
    }

    @EventSubscribe
    public void onSceneTransitionEvent(final SceneTransitionEvent sceneTransitionEvent) {
        final Scene scene = sceneTransitionEvent.getScene();
        LOG.info("Activating scene: {}", scene.getName());
        currentScene = scene;
        currentScene.activate();
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        currentScene.update(newLoopIterationEvent.getElapsedTime());
    }
}
