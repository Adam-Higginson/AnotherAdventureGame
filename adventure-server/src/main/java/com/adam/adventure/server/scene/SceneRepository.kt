package com.adam.adventure.server.scene

import com.adam.adventure.entity.EntityFactoryImpl
import com.adam.adventure.entity.component.tilemap.TilemapComponent
import com.adam.adventure.scene.Scene
import com.adam.adventure.scene.SceneFactory
import javax.inject.Inject

class SceneRepository @Inject constructor(val sceneFactory : SceneFactory,
                                          val entityFactory: EntityFactoryImpl) {

    fun buildTestScene() : Scene {
        val scene = sceneFactory.createScene("Test Scene")
        val tilemap = entityFactory.create("tilemap")
                .addComponent(TilemapComponent("tilemaps/test-world.json"))
        scene.addEntity(tilemap)

        return scene
    }
}