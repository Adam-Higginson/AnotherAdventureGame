package com.adam.adventure.server.receiver;

import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.lib.flatbuffer.schema.packet.PacketType;
import com.adam.adventure.server.receiver.processor.LoginPacketProcessor;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import java.net.DatagramPacket;
import java.util.function.BiConsumer;

public class ReceiverModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder.newMapBinder(binder(),
                new TypeLiteral<Byte>() {},
                new TypeLiteral<BiConsumer<DatagramPacket, Packet>>() {})
        .addBinding(PacketType.LoginPacket).to(LoginPacketProcessor.class);
    }
}
