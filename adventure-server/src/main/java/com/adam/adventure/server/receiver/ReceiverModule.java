package com.adam.adventure.server.receiver;

import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import com.adam.adventure.server.receiver.processor.ClientReadyPacketProcessor;
import com.adam.adventure.server.receiver.processor.EntityTransformPacketProcessor;
import com.adam.adventure.server.receiver.processor.LoginPacketProcessor;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import java.net.DatagramPacket;
import java.util.function.BiConsumer;

public class ReceiverModule extends AbstractModule {

    @Override
    protected void configure() {
        //@formatter:off
        final MapBinder<Byte, BiConsumer<DatagramPacket, Packet>> mapBinder
                = MapBinder.newMapBinder(binder(),
                new TypeLiteral<Byte>() {},
                new TypeLiteral<BiConsumer<DatagramPacket, Packet>>() {});
        //@formatter:on

        mapBinder.addBinding(PacketType.LoginPacket).to(LoginPacketProcessor.class);
        mapBinder.addBinding(PacketType.ClientReadyPacket).to(ClientReadyPacketProcessor.class);
        mapBinder.addBinding(PacketType.EntityTransformPacket).to(EntityTransformPacketProcessor.class);
    }
}
