package com.adam.adventure.server;

import com.adam.adventure.lib.flatbuffer.schema.packet.*;
import com.google.flatbuffers.FlatBufferBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class AdventureServerOld {
    private static final Logger LOG = LoggerFactory.getLogger(AdventureServerOld.class);

    private boolean running;
    private final int port;

    public AdventureServerOld(final int port) {
        this.port = port;
    }

    public void start() throws IOException {
        running = true;
        final DatagramSocket datagramSocket = new DatagramSocket(port);
        acceptData(datagramSocket);
    }

    private void acceptData(final DatagramSocket datagramSocket) throws IOException {
        LOG.info("Adventure server started...");
        final byte[] buffer = new byte[256];

        while (running) {

            // receiver request
            final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);
            final ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, packet.getOffset(), packet.getLength());
            final LoginPacket loginPacket = LoginPacket.getRootAsLoginPacket(byteBuffer);

            String username = loginPacket.username();
            LOG.info("Received login request with username: {}", username);

            returnWorldState(datagramSocket, packet, username);
        }

    }

    private void returnWorldState(DatagramSocket datagramSocket, DatagramPacket packet, String username) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder(256);
        int playerUsernameId = builder.createString(username);
        int sceneId = builder.createString("Test Scene");

        SceneInfo.startSceneInfo(builder);
        SceneInfo.addSceneName(builder, sceneId);
        int sceneInfoId = SceneInfo.endSceneInfo(builder);

        int playerInfoId = createPlayerInfo(builder, playerUsernameId);
        int playersVectorId = WorldStatePacket.createPlayersVector(builder, new int[]{playerInfoId});

        WorldStatePacket.startWorldStatePacket(builder);
        WorldStatePacket.addActiveScene(builder, sceneInfoId);
        WorldStatePacket.addCurrentPlayer(builder, playerInfoId);
        WorldStatePacket.addPlayers(builder, playersVectorId);
        int worldStatePacketId = WorldStatePacket.endWorldStatePacket(builder);

        Packet.startPacket(builder);
        Packet.addPacketType(builder, PacketType.WorldStatePacket);
        Packet.addPacket(builder, worldStatePacketId);
        int packetId = Packet.endPacket(builder);
        builder.finish(packetId);


        final byte[] worldStatePacketData = builder.sizedByteArray();
        final DatagramPacket worldStatePacket = new DatagramPacket(worldStatePacketData, worldStatePacketData.length, packet.getAddress(), packet.getPort());
        datagramSocket.send(worldStatePacket);
    }

    private int createPlayerInfo(FlatBufferBuilder builder, int playerUsernameId) {
        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUserId(builder, 555);
        PlayerInfo.addUsername(builder, playerUsernameId);


        org.joml.Matrix4f randomPositionMatrix = new org.joml.Matrix4f().translate(ThreadLocalRandom.current().nextInt(0, 500),
                ThreadLocalRandom.current().nextInt(0, 500),
                0);

        int playerPositionId = Matrix4f.createMatrix4f(builder,
                randomPositionMatrix.m00(), randomPositionMatrix.m01(), randomPositionMatrix.m02(), randomPositionMatrix.m03(),
                randomPositionMatrix.m10(), randomPositionMatrix.m11(), randomPositionMatrix.m12(), randomPositionMatrix.m13(),
                randomPositionMatrix.m20(), randomPositionMatrix.m21(), randomPositionMatrix.m22(), randomPositionMatrix.m23(),
                randomPositionMatrix.m30(), randomPositionMatrix.m31(), randomPositionMatrix.m32(), randomPositionMatrix.m33());
        PlayerInfo.addTransform(builder, playerPositionId);
        return PlayerInfo.endPlayerInfo(builder);
    }

    public void stop() {
        running = false;
    }
}
