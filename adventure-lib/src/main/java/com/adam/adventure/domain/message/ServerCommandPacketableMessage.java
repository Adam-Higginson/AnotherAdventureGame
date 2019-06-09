package com.adam.adventure.domain.message;

import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.google.flatbuffers.FlatBufferBuilder;

public class ServerCommandPacketableMessage implements PacketableMessage<ServerCommandPacketableMessage> {
    private final String command;

    public ServerCommandPacketableMessage(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public int serialise(final FlatBufferBuilder builder,
                         final PacketConverter packetConverter,
                         final long packetId,
                         final long timestamp) {
        return packetConverter.buildServerCommandPacket(builder, command, packetId, timestamp);
    }

    @Override
    public void deserialise(final ServerCommandPacketableMessage packet,
                            final PacketConverter packetConverter,
                            final long packetId) {

    }
}
