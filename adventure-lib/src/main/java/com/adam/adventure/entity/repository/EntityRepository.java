package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;

import java.util.Optional;

public interface EntityRepository {
    String NPC_SKELETON_NAME = "skeleton";

    Entity buildNpcSkeletonEntity();

    /**
     * Builds an entity for the given name, or returns an empty optional if no entity for the given name could be built.
     * @param name The name the entity that is built should have.
     * @return An {@code Optional} containing the given entity if it could be built, false otherwise.
     */
    default Optional<Entity> buildEntityForName(final String name) {
        switch (name) {
            case NPC_SKELETON_NAME : return Optional.of(buildNpcSkeletonEntity());
        }

        return Optional.empty();
    }
}
