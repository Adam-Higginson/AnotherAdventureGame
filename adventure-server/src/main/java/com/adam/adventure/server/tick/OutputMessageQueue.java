package com.adam.adventure.server.tick;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class OutputMessageQueue {

    private final Queue<OutputMessage> outputMessages;

    public OutputMessageQueue() {
        this.outputMessages = new LinkedList<>();
    }

    public void addOutputMessage(final OutputMessage outputMessage) {
        outputMessages.add(outputMessage);
    }

    void pollEach(final Consumer<OutputMessage> outputMessageConsumer) {
        OutputMessage outputMessage = outputMessages.poll();
        while (outputMessage != null) {
            outputMessageConsumer.accept(outputMessage);
            outputMessage = outputMessages.poll();
        }
    }
}
