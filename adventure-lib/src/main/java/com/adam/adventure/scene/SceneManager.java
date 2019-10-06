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
import java.util.function.Supplier;

public class SceneManager {


    private enum SceneManagerState {ACTIVE_SCENE, DESTROY_CURRENT_SCENE, ACTIVATE_NEXT_SCENE}

    private static final Logger LOG = LoggerFactory.getLogger(SceneManager.class);

    private Scene currentScene;
    private Scene nextScene;

    private final EventBus eventBus;
    private final Map<String, Supplier<Scene>> sceneNameToSceneSupplier;
    private final SceneFactory sceneFactory;
    private final List<Entity> rootEntities;
    private SceneManagerState sceneManagerState;

    @Inject
    public SceneManager(
            final SceneFactory sceneFactory,
            final EventBus eventBus) {
        this.sceneFactory = sceneFactory;
        this.eventBus = eventBus;
        this.sceneNameToSceneSupplier = new HashMap<>();
        this.rootEntities = new LinkedList<>();

        eventBus.register(this);
    }

    public SceneManager addScene(final String sceneName, final Supplier<Scene> sceneSupplier) {
        sceneNameToSceneSupplier.put(sceneName.toLowerCase(), sceneSupplier);
        return this;
    }

    /**
     * Adds a root entity. A root entity is an entity which isn't attached to any individual scene but is instead
     * always present.
     */
    public SceneManager addRootEntity(final Entity entity) {
        rootEntities.add(entity);
        return this;
    }

    public SceneFactory getSceneFactory() {
        return sceneFactory;
    }

    public Optional<Scene> getCurrentScene() {
        return Optional.ofNullable(currentScene);
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void newSceneEvent(final RequestNewSceneEvent requestNewSceneEvent) {
        final Supplier<Scene> newSceneCreator = sceneNameToSceneSupplier.get(requestNewSceneEvent.getSceneName().toLowerCase());
        nextScene = newSceneCreator.get();
        if (nextScene == null) {
            throw new NoSceneFoundException("Scene with name: " + requestNewSceneEvent.getSceneName().toLowerCase() + " could not be found");
        }

        if (currentScene == null) {
            //No need to destroy existing scene
            activateNextScene();
        } else {
            //We update to scene in next frame rather than the current to allow for other processes to clean up
            sceneManagerState = SceneManagerState.DESTROY_CURRENT_SCENE;
        }
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        switch (sceneManagerState) {
            case DESTROY_CURRENT_SCENE:
                destroyCurrentScene();
                break;
            case ACTIVATE_NEXT_SCENE:
                activateNextScene();
                break;
        }

        if (currentScene != null) {
            currentScene.update(newLoopIterationEvent.getElapsedTime());
        }
    }

    /**
     * Called when the application is shutting down
     */
    public void shutdown() {
        LOG.info("Shutting down scene manager");
        rootEntities.forEach(Entity::destroy);
        destroyCurrentScene();
        LOG.info("Scene manager shut down");
    }


    private void destroyCurrentScene() {
        if (currentScene != null) {
            LOG.info("Destroying current scene: {}", currentScene.getName());
            currentScene.removeEntities(rootEntities);
            currentScene.destroy();
            LOG.info("Current scene: {} destroyed", currentScene.getName());
        }

        sceneManagerState = SceneManagerState.ACTIVATE_NEXT_SCENE;
    }

    private void activateNextScene() {
        currentScene = nextScene;
        nextScene = null;
        LOG.info("Activating scene: {}", currentScene.getName());

        rootEntities.forEach(currentScene::addEntity);
        currentScene.activate();

        eventBus.publishEvent(new SceneActivatedEvent(currentScene.getName()));
        LOG.info("Activated scene: {}", currentScene.getName());

        sceneManagerState = SceneManagerState.ACTIVE_SCENE;

        eventBus.publishEvent(new NewSceneActivatedEvent(currentScene));
    }
}
