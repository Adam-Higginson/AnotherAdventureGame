package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.window.Window;
import org.joml.Vector3f;

public class CameraTargetComponent implements EntityComponent<Entity> {

    private final Window window;
    private final Camera camera;

    public CameraTargetComponent(final Window window, final Camera camera) {
        this.window = window;
        this.camera = camera;
    }

    @Override
    public void update(final Entity targetEntity, final float deltaTime, final ComponentContainer componentContainer) {
        final Vector3f targetEntityTranslate = new Vector3f();
        targetEntity.getTransform().getTranslation(targetEntityTranslate);
        //TODO these 32f offset are for the fact the sprite is not centered. Need to decide how best to fix this.
        targetEntityTranslate.x -= (window.getWidth() / 2) - 32f;
        targetEntityTranslate.y -= (window.getHeight() / 2) - 32f;
        camera.setTarget(targetEntityTranslate);


        final Vector3f eye = camera.getEye();
        eye.x = targetEntityTranslate.x;
        eye.y = targetEntityTranslate.y;
        camera.setEye(eye);

        //System.out.println("camera target: " + camera.getTarget() + " camera eye: " + camera.getEye() + " camera look at: " + camera.getLookAt());
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        //Nothing doing
    }
}
