package com.adam.adventure.server.state;

import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.server.player.PlayerSession;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorldState {
    private SceneInfo sceneInfo;
    private List<PlayerSession> players;
}
