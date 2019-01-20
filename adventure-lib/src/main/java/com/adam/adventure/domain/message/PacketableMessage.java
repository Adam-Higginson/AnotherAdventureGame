package com.adam.adventure.domain.message;

import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.google.flatbuffers.FlatBufferBuilder;

public interface PacketableMessage<T> {

    /**
     * @return the flat buffer id of the created packet
     */
    int serialise(final FlatBufferBuilder builder, final PacketConverter packetConverter, final long packetId);

    void deserialise(final T packet, final PacketConverter packetConverter, final long packetId);
}
