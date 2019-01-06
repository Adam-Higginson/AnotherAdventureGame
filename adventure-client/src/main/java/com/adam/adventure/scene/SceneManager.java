package com.adam.adventure.scene;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.InitialisedEvent;
import com.adam.adventure.scene.event.NewSceneEvent;
import com.adam.adventure.scene.event.SceneTransitionEvent;
import com.adam.adventure.update.event.NewLoopIterationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.adam.adventure.scene.SceneFactory.START_MENU_SCENE_NAME;

public class SceneManager {
    private enum SceneManagerState {ACTIVE_SCENE, TRANSITION_TO_SCENE}

    private static final Logger LOG = LoggerFactory.getLogger(SceneManager.class);

    private Scene currentScene;
    private final EventBus eventBus;
    private final Map<String, Scene> sceneNameToSceneSupplier;
    private final SceneFactory sceneFactory;
    private SceneManagerState sceneManagerState;

    @Inject
    public SceneManager(
            final SceneFactory sceneFactory,
            final EventBus eventBus) {
        this.eventBus = eventBus;
        this.sceneNameToSceneSupplier = new HashMap<>();
        this.sceneFactory = sceneFactory;

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

        //We update to scene in next frame rather than the current to allow for other processes to clean up
        sceneManagerState = SceneManagerState.TRANSITION_TO_SCENE;
        if (currentScene != null) {
            currentScene.destroy();
        }

        currentScene = scene;
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        if (sceneManagerState == SceneManagerState.TRANSITION_TO_SCENE) {
            //Transition to new scene if we need to
            currentScene.activate();
            sceneManagerState = SceneManagerState.ACTIVE_SCENE;
        }

        currentScene.update(newLoopIterationEvent.getElapsedTime());
    }
}
