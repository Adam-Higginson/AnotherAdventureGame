package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.EntityComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Getter
public class NetworkIdComponent extends EntityComponent {
    private final UUID entityId;

    private final AtomicReference<Matrix4f> queuedTransform = new AtomicReference<>();

    /**
     * Queue a transform update which will be applied upon next invocation of the entity update
     * @param transform the transform to queue
     */
    public void queueTransformUpdate(final Matrix4f transform) {
        //For now no queue, just overwrite what was there previously
        queuedTransform.set(transform);
    }

    @Override
    protected void update(final float deltaTime) {
        final Matrix4f transform = queuedTransform.getAndUpdate(m -> null);
        if (transform != null) {
            getTransformComponent().getTransform().set(transform);
        }
    }
}
