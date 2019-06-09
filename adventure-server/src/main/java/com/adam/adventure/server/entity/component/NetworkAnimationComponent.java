package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.AnimationName;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;

/**
 */
public class NetworkAnimationComponent extends EntityComponent {
    private String animationName;

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {
        if (!(componentEvent instanceof MovementComponentEvent)) {
            return;
        }

        final MovementComponentEvent movementComponentEvent = (MovementComponentEvent) componentEvent;
        switch (movementComponentEvent.getMovementType()) {
            case ENTITY_MOVE_NORTH:
                animationName = AnimationName.MOVE_NORTH;
                break;
            case ENTITY_MOVE_EAST:
                animationName = AnimationName.MOVE_EAST;
                break;
            case ENTITY_MOVE_SOUTH:
                animationName = AnimationName.MOVE_SOUTH;
                break;
            case ENTITY_MOVE_WEST:
                animationName = AnimationName.MOVE_WEST;
                break;
            case ENTITY_NO_MOVEMENT:
            default:
                animationName = AnimationName.NO_MOVEMENT;
        }
    }

    public String getAnimationName() {
        return animationName;
    }
}
