package com.adam.adventure.lib.flatbuffer.schema.converter;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.lib.flatbuffer.schema.packet.*;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PacketConverter {

    public WorldState fromPacket(final WorldStatePacket worldStatePacket) {

        final com.adam.adventure.domain.SceneInfo sceneInfo = new com.adam.adventure.domain.SceneInfo(worldStatePacket.activeScene().sceneName());

        final List<com.adam.adventure.domain.PlayerInfo> players = new ArrayList<>(worldStatePacket.playersLength());
        for (int i = 0; i < worldStatePacket.playersLength(); i++) {
            final PlayerInfo player = worldStatePacket.players(i);
            players.add(fromPacketPlayerInfo(player));
        }

        return WorldState.newBuilder()
                .withSceneInfo(sceneInfo)
                .withPlayers(players)
                .build();
    }

    public com.adam.adventure.domain.PlayerInfo fromPacketPlayerInfo(final PlayerInfo packetPlayerInfo) {
        return com.adam.adventure.domain.PlayerInfo.newBuilder()
                .withUsername(packetPlayerInfo.username())
                .withId(packetPlayerInfo.userId())
                .withTransform(fromPacketMatrix4f(packetPlayerInfo.transform()))
                .build();
    }

    public byte[] buildLoginPacket(final String username) {
        final FlatBufferBuilder builder = new FlatBufferBuilder(32);
        final int usernameId = builder.createString(username);

        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUsername(builder, usernameId);
        final int playerInfoId = PlayerInfo.endPlayerInfo(builder);
        LoginPacket.startLoginPacket(builder);
        LoginPacket.addPlayer(builder, playerInfoId);
        final int loginPacketId = LoginPacket.endLoginPacket(builder);

        return wrapIntoPacket(builder, loginPacketId, PacketType.LoginPacket);
    }

    public byte[] buildClientReadyPacket(final com.adam.adventure.domain.PlayerInfo playerInfo) {
        final FlatBufferBuilder builder = new FlatBufferBuilder(32);
        final int playerInfoId = buildPlayerInfoId(builder, playerInfo);

        ClientReadyPacket.startClientReadyPacket(builder);
        ClientReadyPacket.addPlayer(builder, playerInfoId);
        final int clientReadyPacketId = ClientReadyPacket.endClientReadyPacket(builder);
        return wrapIntoPacket(builder, clientReadyPacketId, PacketType.ClientReadyPacket);
    }

    public byte[] buildLoginSuccessfulPacket(final com.adam.adventure.domain.PlayerInfo playerInfo) {
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final int playerInfoId = buildPlayerInfoId(builder, playerInfo);

        LoginSuccessfulPacket.startLoginSuccessfulPacket(builder);
        LoginSuccessfulPacket.addPlayer(builder, playerInfoId);
        final int loginSuccessfulPacketId = LoginSuccessfulPacket.endLoginSuccessfulPacket(builder);

        return wrapIntoPacket(builder, loginSuccessfulPacketId, PacketType.LoginSuccessfulPacket);
    }


    public byte[] buildWorldStatePacket(final WorldState worldState) {
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final int[] playerInfoIds = new int[worldState.getPlayers().size()];
        for (int i = 0; i < worldState.getPlayers().size(); i++) {
            playerInfoIds[i] = buildPlayerInfoId(builder, worldState.getPlayers().get(i));
        }
        final int playersVectorId = WorldStatePacket.createPlayersVector(builder, playerInfoIds);
        final int sceneInfoId = buildSceneInfo(worldState, builder);

        WorldStatePacket.startWorldStatePacket(builder);
        WorldStatePacket.addActiveScene(builder, sceneInfoId);
        WorldStatePacket.addPlayers(builder, playersVectorId);
        final int worldStatePacketId = WorldStatePacket.endWorldStatePacket(builder);

        return wrapIntoPacket(builder, worldStatePacketId, PacketType.WorldStatePacket);
    }

    private int buildSceneInfo(final WorldState worldState, final FlatBufferBuilder builder) {
        final int sceneNameId = builder.createString(worldState.getSceneInfo().getSceneName());
        SceneInfo.startSceneInfo(builder);
        SceneInfo.addSceneName(builder, sceneNameId);
        return SceneInfo.endSceneInfo(builder);
    }

    public LoginSuccessfulPacket getLoginSuccessfulPacket(final byte[] buffer, final int offset, final int length) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, offset, length);
        final Packet packet = Packet.getRootAsPacket(byteBuffer);
        return (LoginSuccessfulPacket) packet.packet(new LoginSuccessfulPacket());
    }

    private byte[] wrapIntoPacket(final FlatBufferBuilder builder, final int id, final byte packetType) {
        Packet.startPacket(builder);
        Packet.addPacketType(builder, packetType);
        Packet.addPacket(builder, id);
        final int packetId = Packet.endPacket(builder);
        builder.finish(packetId);

        return builder.sizedByteArray();
    }


    private int buildPlayerInfoId(final FlatBufferBuilder builder, final com.adam.adventure.domain.PlayerInfo playerInfo) {
        final int usernameId = builder.createString(playerInfo.getUsername());

        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUsername(builder, usernameId);
        PlayerInfo.addTransform(builder, buildPacketMatrix4fId(builder, playerInfo.getTransform()));
        PlayerInfo.addUserId(builder, playerInfo.getId());
        return PlayerInfo.endPlayerInfo(builder);
    }

    private int buildPacketMatrix4fId(final FlatBufferBuilder builder, final org.joml.Matrix4f matrix4f) {
        return Matrix4f.createMatrix4f(builder,
                matrix4f.m00(), matrix4f.m01(), matrix4f.m02(), matrix4f.m03(),
                matrix4f.m10(), matrix4f.m11(), matrix4f.m12(), matrix4f.m13(),
                matrix4f.m20(), matrix4f.m21(), matrix4f.m22(), matrix4f.m23(),
                matrix4f.m30(), matrix4f.m31(), matrix4f.m32(), matrix4f.m33());
    }

    public org.joml.Matrix4f fromPacketMatrix4f(final Matrix4f packetMatrix4f) {
        return new org.joml.Matrix4f(
                packetMatrix4f.m00(), packetMatrix4f.m01(), packetMatrix4f.m02(), packetMatrix4f.m03(),
                packetMatrix4f.m10(), packetMatrix4f.m11(), packetMatrix4f.m12(), packetMatrix4f.m13(),
                packetMatrix4f.m20(), packetMatrix4f.m21(), packetMatrix4f.m22(), packetMatrix4f.m23(),
                packetMatrix4f.m30(), packetMatrix4f.m31(), packetMatrix4f.m32(), packetMatrix4f.m33());
    }
}
