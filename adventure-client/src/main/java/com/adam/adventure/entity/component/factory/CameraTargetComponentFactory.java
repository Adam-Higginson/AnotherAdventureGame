package com.adam.adventure.entity.component.factory;

import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.window.Window;

public class CameraTargetComponentFactory implements EntityComponentFactory {
    private final Window window;
    private final Camera camera;

    public CameraTargetComponentFactory(final Window window, final Camera camera) {
        this.window = window;
        this.camera = camera;
    }

    @Override
    public void registerNewInstanceWithContainer(final ComponentContainer componentContainer) {
        final EntityComponent cameraTargetComponent = new CameraTargetComponent(componentContainer, window, camera);
        componentContainer.addComponent(cameraTargetComponent);
    }
}
