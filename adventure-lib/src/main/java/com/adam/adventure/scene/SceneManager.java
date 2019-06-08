package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.NewLoopIterationEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.SceneActivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

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

        eventBus.register(this);
    }

    public SceneManager addScene(final Scene scene) {
        sceneNameToSceneSupplier.put(scene.getName(), scene);
        return this;
    }

    /**
     * Forces the destruction of the current scene and all associated entities
     */
    public void forceDestroy() {
        if (currentScene != null) {
            currentScene.forceDestroy();
        }
    }

    public SceneFactory getSceneFactory() {
        return sceneFactory;
    }

    public Optional<Scene> getCurrentScene() {
        return Optional.ofNullable(currentScene);
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void newSceneEvent(final NewSceneEvent newSceneEvent) {
        final Scene newScene = sceneNameToSceneSupplier.get(newSceneEvent.getSceneName());
        if (newScene == null) {
            LOG.error("Could not find scene with name: {}", newSceneEvent.getSceneName());
            return;
        }

        LOG.info("Activating scene: {}", newScene.getName());
        //We update to scene in next frame rather than the current to allow for other processes to clean up
        sceneManagerState = SceneManagerState.TRANSITION_TO_SCENE;
        if (currentScene != null) {
            currentScene.destroy();
            getNonDestroyableEntitiesInCurrentScene()
                    .forEach(newScene::addEntity);
        }

        currentScene = newScene;
    }

    private List<Entity> getNonDestroyableEntitiesInCurrentScene() {
        if (currentScene == null) {
            return new ArrayList<>();
        }

        return currentScene.getEntities().stream()
                .filter(entity -> !entity.shouldDestroyOnSceneChange())
                .collect(Collectors.toList());
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        if (sceneManagerState == SceneManagerState.TRANSITION_TO_SCENE) {
            //Transition to new scene if we need to
            currentScene.activate();
            sceneManagerState = SceneManagerState.ACTIVE_SCENE;
            eventBus.publishEvent(new SceneActivatedEvent(currentScene.getName()));
        }

        if (currentScene != null) {
            currentScene.update(newLoopIterationEvent.getElapsedTime());
        }
    }
}
