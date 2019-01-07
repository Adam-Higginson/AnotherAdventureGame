// automatically generated by the FlatBuffers compiler, do not modify

package com.adam.adventure.lib.flatbuffer.schema.packet;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class SceneInfo extends Table {
  public static SceneInfo getRootAsSceneInfo(ByteBuffer _bb) { return getRootAsSceneInfo(_bb, new SceneInfo()); }
  public static SceneInfo getRootAsSceneInfo(ByteBuffer _bb, SceneInfo obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public SceneInfo __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String sceneName() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer sceneNameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer sceneNameInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }

  public static int createSceneInfo(FlatBufferBuilder builder,
      int sceneNameOffset) {
    builder.startObject(1);
    SceneInfo.addSceneName(builder, sceneNameOffset);
    return SceneInfo.endSceneInfo(builder);
  }

  public static void startSceneInfo(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addSceneName(FlatBufferBuilder builder, int sceneNameOffset) { builder.addOffset(0, sceneNameOffset, 0); }
  public static int endSceneInfo(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

