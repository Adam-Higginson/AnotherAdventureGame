package com.adam.adventure.entity.component.event;

public class MovementComponentEvent extends ComponentEvent {

    public enum MovementType {
        ENTITY_NO_MOVEMENT,
        ENTITY_MOVE_NORTH,
        ENTITY_MOVE_SOUTH,
        ENTITY_MOVE_EAST,
        ENTITY_MOVE_WEST
    }

    private final MovementType movementType;

    public MovementComponentEvent(final MovementType movementType) {
        this.movementType = movementType;
    }

    public MovementType getMovementType() {
        return movementType;
    }
}
