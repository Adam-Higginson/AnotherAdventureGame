// automatically generated by the FlatBuffers compiler, do not modify

package com.adam.adventure.lib.flatbuffer.schema.packet;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class PacketBatch extends Table {
  public static PacketBatch getRootAsPacketBatch(ByteBuffer _bb) { return getRootAsPacketBatch(_bb, new PacketBatch()); }
  public static PacketBatch getRootAsPacketBatch(ByteBuffer _bb, PacketBatch obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public PacketBatch __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public Packet packets(int j) { return packets(new Packet(), j); }
  public Packet packets(Packet obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int packetsLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createPacketBatch(FlatBufferBuilder builder,
      int packetsOffset) {
    builder.startObject(1);
    PacketBatch.addPackets(builder, packetsOffset);
    return PacketBatch.endPacketBatch(builder);
  }

  public static void startPacketBatch(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addPackets(FlatBufferBuilder builder, int packetsOffset) { builder.addOffset(0, packetsOffset, 0); }
  public static int createPacketsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startPacketsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endPacketBatch(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

