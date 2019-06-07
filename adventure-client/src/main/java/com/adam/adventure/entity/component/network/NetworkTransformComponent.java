package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.message.EntityTransformPacketableMessage;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Responsible for managing updating the server about this entity's transform and processing network updates
 * regarding it.
 */
public class NetworkTransformComponent extends NetworkComponent {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkTransformComponent.class);

    private final boolean authoritative;

    private Matrix4f lastReceivedTransform;
    private MovementComponentEvent.MovementType lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT;
    private final LinkedList<EntityInfoBufferElement> entityInfoBuffer;

    /**
     * @param authoritative Whether this component is owned by the client
     */
    public NetworkTransformComponent(final boolean authoritative) {
        this.authoritative = authoritative;
        this.entityInfoBuffer = new LinkedList<>();
    }


    @Override
    protected void update(final float deltaTime) {
        if (lastReceivedTransform != null && lastReceivedMovementType != null) {
            getTransformComponent().getTransform().set(lastReceivedTransform);
            if (lastReceivedMovementType != MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT) {
                LOG.debug("Next movement type: {}", lastReceivedMovementType);
            }
            broadcastComponentEvent(new MovementComponentEvent(lastReceivedMovementType));
        }
    }

    @Override
    protected void writeNetworkUpdates(final UUID entityId,
                                       final OutputMessageQueue outputMessageQueue) {
        if (authoritative) {
            final EntityTransformPacketableMessage message
                    = new EntityTransformPacketableMessage(entityId, getTransformComponent().getTransform());
            outputMessageQueue.add(message);
        }
    }

    @Override
    protected void receiveNetworkUpdates(final EntityInfo entityInfo) {
        if (!authoritative) {

            final long currentTimestamp = System.currentTimeMillis();
            entityInfoBuffer.add(new EntityInfoBufferElement(currentTimestamp, entityInfo));

            //TODO Need to actual get the render timestamp here
            final long renderTimestamp = currentTimestamp - (1000 / 50);

            //Drop older positions in buffer
            while (entityInfoBuffer.size() >= 2 && entityInfoBuffer.get(1).timestamp <= renderTimestamp) {
                entityInfoBuffer.removeFirst();
            }

            interpolatePosition(renderTimestamp);
        }
    }


    /**
     * The way interpolation works is by actually viewing updates from the server as being in the past. We keep
     * track of the previous 2 server updates and set the entity's position as being transformed somewhere between
     * them, based on a lerp between the 2 points.
     */
    private void interpolatePosition(long renderTimestamp) {
        if (entityInfoBuffer.size() >= 2
                && entityInfoBuffer.getFirst().timestamp <= renderTimestamp
                && renderTimestamp <= entityInfoBuffer.get(1).timestamp) {
            final EntityInfo beforeEntityInfo = entityInfoBuffer.getFirst().entityInfo;
            final long beforeTimestamp = entityInfoBuffer.getFirst().timestamp;
            final EntityInfo afterEntityInfo = entityInfoBuffer.get(1).entityInfo;
            final long afterTimestamp = entityInfoBuffer.get(1).timestamp;

            final float interpolationFactor = (renderTimestamp - beforeTimestamp) / (afterTimestamp - beforeTimestamp);

            lastReceivedTransform = beforeEntityInfo.getTransform().lerp(afterEntityInfo.getTransform(), interpolationFactor);

            final Vector3f lastPosition = beforeEntityInfo.getTransform().getTranslation(new Vector3f());
            final Vector3f targetPosition = afterEntityInfo.getTransform().getTranslation(new Vector3f());
            final Vector3f difference = targetPosition.sub(lastPosition);

            if (!difference.equals(new Vector3f(0.f, 0.f, 0.f))) {
                final Vector3f normalised = difference.normalize();
                if (normalised.y == 1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH;
                } else if (normalised.y == -1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH;
                } else if (normalised.x == 1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_EAST;
                } else if (normalised.x == -1.f) {
                    lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_WEST;
                }
            }


        }
    }

    private static class EntityInfoBufferElement {
        private final long timestamp;
        private final EntityInfo entityInfo;

        public EntityInfoBufferElement(long timestamp, EntityInfo entityInfo) {
            this.timestamp = timestamp;
            this.entityInfo = entityInfo;
        }
    }
}
