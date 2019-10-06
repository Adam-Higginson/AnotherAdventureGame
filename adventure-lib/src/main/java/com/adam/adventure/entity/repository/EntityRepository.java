package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;

import java.util.Optional;

/**
 * Manages the construction of entities which need to be created by both client and server. The client and server
 * can then implement this in different ways depending on what components need to be added to the entities (e.g.
 * client code will add rendering functionality whereas server code won't need it).
 */
public interface EntityRepository {
    String NPC_SKELETON_NAME = "skeleton";
    String PLAYER_NAME = "player";
    String OTHER_PLAYER = "other_player";

    Entity buildNpcSkeletonEntity();

    Entity buildPlayerEntity();

    Entity buildOtherPlayerEntity();

    /**
     * Builds an entity for the given name, or returns an empty optional if no entity for the given name could be built.
     *
     * @param name The name the entity that is built should have.
     * @return An {@code Optional} containing the given entity if it could be built, false otherwise.
     */
    default Optional<Entity> buildEntityForName(final String name) {
        switch (name) {
            case NPC_SKELETON_NAME:
                return Optional.of(buildNpcSkeletonEntity());
            case PLAYER_NAME:
                return Optional.of(buildPlayerEntity());
            case OTHER_PLAYER:
                return Optional.of(buildOtherPlayerEntity());
        }

        return Optional.empty();
    }
}
