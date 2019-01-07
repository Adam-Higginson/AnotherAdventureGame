package com.adam.adventure.lib.flatbuffer.schema.converter;

import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.lib.flatbuffer.schema.packet.Matrix4f;
import com.adam.adventure.lib.flatbuffer.schema.packet.PlayerInfo;
import com.adam.adventure.lib.flatbuffer.schema.packet.WorldStatePacket;

import java.util.ArrayList;
import java.util.List;

public class PacketConverter {

    public WorldState fromPacket(final WorldStatePacket worldStatePacket) {

        final SceneInfo sceneInfo = new SceneInfo(worldStatePacket.activeScene().sceneName());
        final com.adam.adventure.domain.PlayerInfo playerInfo = fromPacketPlayerInfo(worldStatePacket.currentPlayer());

        final List<com.adam.adventure.domain.PlayerInfo> players = new ArrayList<>(worldStatePacket.playersLength());
        for (int i = 0; i< worldStatePacket.playersLength(); i++) {
            PlayerInfo player = worldStatePacket.players(i);
            players.add(fromPacketPlayerInfo(player));
        }

        return WorldState.newBuilder()
                .withSceneInfo(sceneInfo)
                .withCurrentPlayer(playerInfo)
                .withPlayers(players)
                .build();
    }

    public com.adam.adventure.domain.PlayerInfo fromPacketPlayerInfo(final PlayerInfo packetPlayerInfo) {
        return com.adam.adventure.domain.PlayerInfo.newBuilder()
                .withUsername(packetPlayerInfo.username())
                .withUserId(packetPlayerInfo.userId())
                .withTransform(fromPacketMatrix4f(packetPlayerInfo.transform()))
                .build();
    }

    public org.joml.Matrix4f fromPacketMatrix4f(final Matrix4f packetMatrix4f) {
        return new org.joml.Matrix4f(
                packetMatrix4f.m00(), packetMatrix4f.m01(), packetMatrix4f.m02(), packetMatrix4f.m03(),
                packetMatrix4f.m10(), packetMatrix4f.m11(), packetMatrix4f.m12(), packetMatrix4f.m13(),
                packetMatrix4f.m20(), packetMatrix4f.m21(), packetMatrix4f.m22(), packetMatrix4f.m23(),
                packetMatrix4f.m30(), packetMatrix4f.m31(), packetMatrix4f.m32(), packetMatrix4f.m33());
    }
}
