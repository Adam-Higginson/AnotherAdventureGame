// automatically generated by the FlatBuffers compiler, do not modify

package com.adam.adventure.lib.flatbuffer.schema.packet;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ClientReadyPacket extends Table {
  public static ClientReadyPacket getRootAsClientReadyPacket(ByteBuffer _bb) { return getRootAsClientReadyPacket(_bb, new ClientReadyPacket()); }
  public static ClientReadyPacket getRootAsClientReadyPacket(ByteBuffer _bb, ClientReadyPacket obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public ClientReadyPacket __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public PlayerInfo player() { return player(new PlayerInfo()); }
  public PlayerInfo player(PlayerInfo obj) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createClientReadyPacket(FlatBufferBuilder builder,
      int playerOffset) {
    builder.startObject(1);
    ClientReadyPacket.addPlayer(builder, playerOffset);
    return ClientReadyPacket.endClientReadyPacket(builder);
  }

  public static void startClientReadyPacket(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addPlayer(FlatBufferBuilder builder, int playerOffset) { builder.addOffset(0, playerOffset, 0); }
  public static int endClientReadyPacket(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
