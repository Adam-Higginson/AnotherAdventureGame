package com.adam.adventure.server.entity.repository;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.repository.EntityRepository;
import com.adam.adventure.server.entity.component.AIComponent;
import com.adam.adventure.server.entity.component.NetworkIdComponent;

import javax.inject.Inject;
import java.util.UUID;

public class ServerEntityRepository implements EntityRepository {

    private final EntityFactory entityFactory;

    @Inject
    public ServerEntityRepository(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public Entity buildNpcSkeletonEntity() {
        return entityFactory.create(NPC_SKELETON_NAME)
                .addComponent(new NetworkIdComponent(UUID.randomUUID()))
                .addComponent(new AIComponent(.2f));

    }
}
