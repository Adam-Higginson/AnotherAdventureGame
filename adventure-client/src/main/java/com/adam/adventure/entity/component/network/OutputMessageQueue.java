package com.adam.adventure.entity.component.network;

import com.adam.adventure.domain.message.PacketableMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputMessageQueue {
    private final BlockingQueue<PacketableMessage<?>> messageQueue;

    public OutputMessageQueue() {
        messageQueue = new LinkedBlockingQueue<>();
    }


    public void add(final PacketableMessage<?> message) {
        messageQueue.add(message);
    }

    public List<PacketableMessage<?>> drain() {
        final List<PacketableMessage<?>> messages = new ArrayList<>(messageQueue.size());
        messageQueue.drainTo(messages);
        return messages;
    }
}
