// automatically generated by the FlatBuffers compiler, do not modify

package com.adam.adventure.lib.flatbuffer.schema.packet;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Packet extends Table {
  public static Packet getRootAsPacket(ByteBuffer _bb) { return getRootAsPacket(_bb, new Packet()); }
  public static Packet getRootAsPacket(ByteBuffer _bb, Packet obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public Packet __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte packetType() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Table packet(Table obj) { int o = __offset(6); return o != 0 ? __union(obj, o) : null; }
  public long packetId() { int o = __offset(8); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long packetTimestamp() { int o = __offset(10); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }

  public static int createPacket(FlatBufferBuilder builder,
      byte packet_type,
      int packetOffset,
      long packetId,
      long packetTimestamp) {
    builder.startObject(4);
    Packet.addPacketTimestamp(builder, packetTimestamp);
    Packet.addPacketId(builder, packetId);
    Packet.addPacket(builder, packetOffset);
    Packet.addPacketType(builder, packet_type);
    return Packet.endPacket(builder);
  }

  public static void startPacket(FlatBufferBuilder builder) { builder.startObject(4); }
  public static void addPacketType(FlatBufferBuilder builder, byte packetType) { builder.addByte(0, packetType, 0); }
  public static void addPacket(FlatBufferBuilder builder, int packetOffset) { builder.addOffset(1, packetOffset, 0); }
  public static void addPacketId(FlatBufferBuilder builder, long packetId) { builder.addLong(2, packetId, 0L); }
  public static void addPacketTimestamp(FlatBufferBuilder builder, long packetTimestamp) { builder.addLong(3, packetTimestamp, 0L); }
  public static int endPacket(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

