package com.adam.adventure.lib.flatbuffer.schema.converter;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.lib.flatbuffer.schema.packet.*;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PacketConverter {

    public WorldState fromPacket(final WorldStatePacket worldStatePacket) {
        return WorldState.newBuilder()
                .withSceneInfo(fromPacketSceneInfo(worldStatePacket.activeScene()))
                .build();
    }


    public com.adam.adventure.domain.SceneInfo fromPacketSceneInfo(final SceneInfo packetSceneInfo) {
        final List<com.adam.adventure.domain.EntityInfo> entityInfo = new ArrayList<>(packetSceneInfo.entitiesLength());
        for (int i = 0; i < packetSceneInfo.entitiesLength(); i++) {
            entityInfo.add(fromPacketEntityInfo(packetSceneInfo.entities(i)));
        }

        return com.adam.adventure.domain.SceneInfo.newBuilder()
                .sceneName(packetSceneInfo.sceneName())
                .entities(entityInfo)
                .build();
    }

    public com.adam.adventure.domain.EntityInfo fromPacketEntityInfo(final EntityInfo packetEntityInfo) {
        return com.adam.adventure.domain.EntityInfo.newBuilder()
                .id(UUID.fromString(packetEntityInfo.id()))
                .name(packetEntityInfo.name())
                .animationName(packetEntityInfo.animationName())
                .transform(fromPacketMatrix4f(packetEntityInfo.transform()))
                .attributes(fromPacketMap(packetEntityInfo.attributes()))
                .type(fromPacketEntityType(packetEntityInfo.type()))
                .build();
    }

    public com.adam.adventure.domain.EntityInfo.EntityType fromPacketEntityType(final byte packetEntityType) {
        switch (packetEntityType) {
            case EntityType.STANDARD:
                return com.adam.adventure.domain.EntityInfo.EntityType.STANDARD;
            case EntityType.PLAYER:
                return com.adam.adventure.domain.EntityInfo.EntityType.PLAYER;
        }

        return null;
    }

    public java.util.Map<String, String> fromPacketMap(final Map packetMap) {
        final java.util.Map<String, String> map = new HashMap<>();
        for (int i = 0; i < packetMap.entriesLength(); i++) {
            map.put(packetMap.entries(i).key(), packetMap.entries(i).value());
        }

        return map;
    }


    public int buildLoginPacket(final FlatBufferBuilder builder,
                                final String username,
                                final long packetId,
                                final long timestamp) {
        final int usernameId = builder.createString(username);

        LoginPacket.startLoginPacket(builder);
        LoginPacket.addPlayerUsername(builder, usernameId);
        final int loginPacketId = LoginPacket.endLoginPacket(builder);

        return wrapIntoPacket(builder, loginPacketId, PacketType.LoginPacket, packetId, timestamp);
    }

    public int buildClientReadyPacket(final FlatBufferBuilder builder,
                                      final com.adam.adventure.domain.EntityInfo entityInfo,
                                      final long packetId,
                                      final long timestamp) {
        final int entityInfoId = buildEntityInfoId(builder, entityInfo);

        ClientReadyPacket.startClientReadyPacket(builder);
        ClientReadyPacket.addPlayerEntity(builder, entityInfoId);
        final int clientReadyPacketId = ClientReadyPacket.endClientReadyPacket(builder);
        return wrapIntoPacket(builder, clientReadyPacketId, PacketType.ClientReadyPacket, packetId, timestamp);
    }

    public byte[] buildLoginSuccessfulPacket(final com.adam.adventure.domain.EntityInfo entityInfo, final long tickrate) {
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final int entityInfoId = buildEntityInfoId(builder, entityInfo);

        LoginSuccessfulPacket.startLoginSuccessfulPacket(builder);
        LoginSuccessfulPacket.addPlayerEntity(builder, entityInfoId);
        LoginSuccessfulPacket.addTickrate(builder, tickrate);
        final int loginSuccessfulPacketId = LoginSuccessfulPacket.endLoginSuccessfulPacket(builder);

        return wrapIntoPacket(builder, loginSuccessfulPacketId, PacketType.LoginSuccessfulPacket);
    }


    public byte[] buildWorldStatePacket(final WorldState worldState, final long packetIndex, final long timestamp) {
        final FlatBufferBuilder builder = new FlatBufferBuilder();
        final int sceneInfoId = buildSceneInfo(worldState, builder);

        WorldStatePacket.startWorldStatePacket(builder);
        WorldStatePacket.addActiveScene(builder, sceneInfoId);
        final int worldStatePacketId = WorldStatePacket.endWorldStatePacket(builder);

        return wrapIntoPacketByteArray(builder, worldStatePacketId, PacketType.WorldStatePacket, packetIndex, timestamp);
    }

    public int buildServerCommandPacket(final FlatBufferBuilder builder,
                                           final String command,
                                           final long packetIndex,
                                           final long timestamp) {

        final int commandId = builder.createString(command);
        ServerCommandPacket.startServerCommandPacket(builder);
        ServerCommandPacket.addCommand(builder, commandId);
        final int serverCommandPacketId = ServerCommandPacket.endServerCommandPacket(builder);

        return wrapIntoPacket(builder, serverCommandPacketId, PacketType.ServerCommandPacket, packetIndex, timestamp);
    }

    public int buildEntityTransformPacket(
            final FlatBufferBuilder builder,
            final UUID entityId,
            final org.joml.Matrix4f transform,
            final long packetId,
            final long timestamp) {
        final int entityIdId = builder.createString(entityId.toString());

        EntityTransformPacket.startEntityTransformPacket(builder);
        EntityTransformPacket.addEntityId(builder, entityIdId);
        EntityTransformPacket.addTransform(builder, buildPacketMatrix4fId(builder, transform));
        final int entityTransformPacketId = EntityTransformPacket.endEntityTransformPacket(builder);

        return wrapIntoPacket(builder, entityTransformPacketId, PacketType.EntityTransformPacket, packetId, timestamp);
    }

    private int buildSceneInfo(final WorldState worldState, final FlatBufferBuilder builder) {
        final int sceneNameId = builder.createString(worldState.getSceneInfo().getSceneName());
        final int[] entityInfoIds = worldState.getSceneInfo().getEntities()
                .stream()
                .mapToInt(entity -> buildEntityInfoId(builder, entity))
                .toArray();
        final int entitiesVectorId = SceneInfo.createEntitiesVector(builder, entityInfoIds);

        SceneInfo.startSceneInfo(builder);
        SceneInfo.addSceneName(builder, sceneNameId);
        SceneInfo.addEntities(builder, entitiesVectorId);

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

    private int wrapIntoPacket(final FlatBufferBuilder builder,
                               final int id,
                               final byte packetType,
                               final long packetIndex,
                               final long timestamp) {
        Packet.startPacket(builder);
        Packet.addPacketType(builder, packetType);
        Packet.addPacket(builder, id);
        Packet.addPacketId(builder, packetIndex);
        Packet.addPacketTimestamp(builder, timestamp);
        return Packet.endPacket(builder);
    }

    private byte[] wrapIntoPacketByteArray(final FlatBufferBuilder builder,
                                  final int id,
                                  final byte packetType,
                                  final long packetIndex,
                                  final long timestamp) {
        final int generatedPacketId = wrapIntoPacket(builder, id, packetType, packetIndex, timestamp);
        builder.finish(generatedPacketId);

        return builder.sizedByteArray();
    }


    private int buildEntityInfoId(final FlatBufferBuilder builder, final com.adam.adventure.domain.EntityInfo entityInfo) {
        final int idStringId = builder.createString(entityInfo.getId().toString());
        final int attributesId = buildMapId(builder, entityInfo.getAttributes());
        final int nameId = builder.createString(entityInfo.getName());
        final int animationNameId = builder.createString(entityInfo.getAnimationName());

        EntityInfo.startEntityInfo(builder);
        EntityInfo.addId(builder, idStringId);
        EntityInfo.addName(builder, nameId);
        EntityInfo.addAnimationName(builder, animationNameId);
        EntityInfo.addTransform(builder, buildPacketMatrix4fId(builder, entityInfo.getTransform()));
        EntityInfo.addAttributes(builder, attributesId);
        EntityInfo.addType(builder, (byte) entityInfo.getType().ordinal());
        return EntityInfo.endEntityInfo(builder);
    }


    private int buildMapId(final FlatBufferBuilder builder, final java.util.Map<String, String> map) {
        final int[] entryIds = map.entrySet().stream()
                .mapToInt(entry -> buildEntryId(builder, entry.getKey(), entry.getValue()))
                .toArray();
        final int entriesId = Map.createEntriesVector(builder, entryIds);

        Map.startMap(builder);
        Map.addEntries(builder, entriesId);
        return Map.endMap(builder);
    }

    private int buildEntryId(final FlatBufferBuilder builder, final String key, final String value) {
        final int keyId = builder.createString(key);
        final int valueId = builder.createString(value);

        MapEntry.startMapEntry(builder);
        MapEntry.addKey(builder, keyId);
        MapEntry.addValue(builder, valueId);
        return MapEntry.endMapEntry(builder);
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
