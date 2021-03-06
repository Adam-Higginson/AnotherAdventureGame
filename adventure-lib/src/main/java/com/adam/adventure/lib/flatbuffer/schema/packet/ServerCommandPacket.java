// automatically generated by the FlatBuffers compiler, do not modify

package com.adam.adventure.lib.flatbuffer.schema.packet;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ServerCommandPacket extends Table {
  public static ServerCommandPacket getRootAsServerCommandPacket(ByteBuffer _bb) { return getRootAsServerCommandPacket(_bb, new ServerCommandPacket()); }
  public static ServerCommandPacket getRootAsServerCommandPacket(ByteBuffer _bb, ServerCommandPacket obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public ServerCommandPacket __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String command() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer commandAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer commandInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }

  public static int createServerCommandPacket(FlatBufferBuilder builder,
      int commandOffset) {
    builder.startObject(1);
    ServerCommandPacket.addCommand(builder, commandOffset);
    return ServerCommandPacket.endServerCommandPacket(builder);
  }

  public static void startServerCommandPacket(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addCommand(FlatBufferBuilder builder, int commandOffset) { builder.addOffset(0, commandOffset, 0); }
  public static int endServerCommandPacket(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

