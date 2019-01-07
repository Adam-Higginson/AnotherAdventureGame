package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
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
        LoginPacket.startLoginPacket(builder);
        LoginPacket.addUsername(builder, usernameId);
        final int loginPacketId = LoginPacket.endLoginPacket(builder);
        builder.finish(loginPacketId);

        return builder.sizedByteArray();
    }

    WorldState fromPacket(final byte[] buffer, final DatagramPacket packet) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, packet.getOffset(), packet.getLength());
        final Packet loginPacket = Packet.getRootAsPacket(byteBuffer);
        WorldStatePacket worldStatePacket = (WorldStatePacket) loginPacket.packet(new WorldStatePacket());
        return packetConverter.fromPacket(worldStatePacket);
    }
}
