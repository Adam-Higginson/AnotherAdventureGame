package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import com.adam.adventure.lib.flatbuffer.schema.packet.PlayerInfo;
import com.adam.adventure.lib.flatbuffer.schema.packet.WorldStatePacket;
import com.google.flatbuffers.FlatBufferBuilder;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

class PacketFactory {

    private final PacketConverter packetConverter;

    PacketFactory() {
        this.packetConverter = new PacketConverter();
    }

    byte[] newLoginPacket(final String username) {
        final FlatBufferBuilder builder = new FlatBufferBuilder(32);
        final int usernameId = builder.createString(username);

        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUsername(builder, usernameId);
        int playerInfoId = PlayerInfo.endPlayerInfo(builder);
        LoginPacket.startLoginPacket(builder);
        LoginPacket.addPlayer(builder, playerInfoId);
        final int loginPacketId = LoginPacket.endLoginPacket(builder);
        builder.finish(loginPacketId);

        return wrapIntoPacket(builder, loginPacketId, PacketType.LoginPacket);
    }

    WorldState fromPacket(final byte[] buffer, final DatagramPacket packet) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, packet.getOffset(), packet.getLength());
        final Packet loginPacket = Packet.getRootAsPacket(byteBuffer);
        WorldStatePacket worldStatePacket = (WorldStatePacket) loginPacket.packet(new WorldStatePacket());
        return packetConverter.fromPacket(worldStatePacket);
    }

    private byte[] wrapIntoPacket(final FlatBufferBuilder builder, final int id, final byte packetType) {
        Packet.startPacket(builder);
        Packet.addPacketType(builder, packetType);
        Packet.addPacket(builder, id);
        int packetId = Packet.endPacket(builder);
        builder.finish(packetId);

        return builder.sizedByteArray();
    }
}
