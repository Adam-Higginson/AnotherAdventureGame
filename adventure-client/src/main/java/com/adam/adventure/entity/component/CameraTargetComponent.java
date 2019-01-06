package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.window.Window;
import org.joml.Vector3f;

import javax.inject.Inject;

public class CameraTargetComponent extends EntityComponent {

    @Inject
    private Window window;

    @Inject
    private Camera camera;

    @Override
    public void update(final float deltaTime) {
        final Vector3f targetEntityTranslate = new Vector3f();
        getTransformComponent().getTransform().getTranslation(targetEntityTranslate);
        //TODO these 32f offset are for the fact the sprite is not centered. Need to decide how best to fix this.
        targetEntityTranslate.x -= (window.getWidth() / 2) - 32f;
        targetEntityTranslate.y -= (window.getHeight() / 2) - 32f;
        camera.setTarget(targetEntityTranslate);


        final Vector3f eye = camera.getEye();
        eye.x = targetEntityTranslate.x;
        eye.y = targetEntityTranslate.y;
        camera.setEye(eye);
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        //Nothing doing
    }
}
