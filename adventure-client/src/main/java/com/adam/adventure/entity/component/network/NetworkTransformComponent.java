package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.message.EntityTransformPacketableMessage;
import com.adam.adventure.entity.AnimationName;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import org.joml.Matrix4f;

import java.util.LinkedList;
import java.util.UUID;

/**
 * Responsible for managing updating the server about this entity's transform and processing network updates
 * regarding it.
 */
public class NetworkTransformComponent extends NetworkComponent {

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
    protected void receiveNetworkUpdates(final EntityInfo entityInfo, final long serverTickrate) {
        if (!authoritative) {
            calculatePosition(entityInfo, serverTickrate);
            setAnimation(entityInfo);
        }
    }

    private void calculatePosition(final EntityInfo entityInfo, final long serverTickrate) {
        final long currentTimestamp = System.currentTimeMillis();
        entityInfoBuffer.add(new EntityInfoBufferElement(currentTimestamp, entityInfo));

        final long renderTimestamp = currentTimestamp - (1000 / serverTickrate);
        //Drop older positions in buffer
        while (entityInfoBuffer.size() >= 2 && entityInfoBuffer.get(1).timestamp <= renderTimestamp) {
            entityInfoBuffer.removeFirst();
        }

        interpolatePosition(renderTimestamp);
    }

    private void setAnimation(final EntityInfo entityInfo) {

        switch (entityInfo.getAnimationName()) {
            case AnimationName.MOVE_NORTH:
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH;
                break;
            case AnimationName.MOVE_EAST:
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_EAST;
                break;
            case AnimationName.MOVE_SOUTH:
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH;
                break;
            case AnimationName.MOVE_WEST:
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_MOVE_WEST;
                break;
            case AnimationName.NO_MOVEMENT:
            default:
                lastReceivedMovementType = MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT;
                break;
        }
    }

    /**
     * The way interpolation works is by actually viewing updates from the server as being in the past. We keep
     * track of the previous 2 server updates and set the entity's position as being transformed somewhere between
     * them, based on a lerp between the 2 points.
     */
    private void interpolatePosition(final long renderTimestamp) {
        if (entityInfoBuffer.size() >= 2
                && entityInfoBuffer.getFirst().timestamp <= renderTimestamp
                && renderTimestamp <= entityInfoBuffer.get(1).timestamp) {
            final EntityInfo beforeEntityInfo = entityInfoBuffer.getFirst().entityInfo;
            final long beforeTimestamp = entityInfoBuffer.getFirst().timestamp;
            final EntityInfo afterEntityInfo = entityInfoBuffer.get(1).entityInfo;
            final long afterTimestamp = entityInfoBuffer.get(1).timestamp;

            final float interpolationFactor = (renderTimestamp - beforeTimestamp) / (afterTimestamp - beforeTimestamp);
            lastReceivedTransform = beforeEntityInfo.getTransform().lerp(afterEntityInfo.getTransform(), interpolationFactor);
        }
    }

    private static class EntityInfoBufferElement {
        private final long timestamp;
        private final EntityInfo entityInfo;

        EntityInfoBufferElement(final long timestamp, final EntityInfo entityInfo) {
            this.timestamp = timestamp;
            this.entityInfo = entityInfo;
        }
    }
}
