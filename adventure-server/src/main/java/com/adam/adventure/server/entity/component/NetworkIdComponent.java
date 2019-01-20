package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.EntityComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class NetworkIdComponent extends EntityComponent {
    private final UUID entityId;
}
