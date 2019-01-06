package com.adam.adventure.event;

public class ConnectionRequestEvent extends Event {
    private final String addressToConnectTo;
    private final int port;

    public ConnectionRequestEvent(final String addressToConnectTo, final int port) {
        this.addressToConnectTo = addressToConnectTo;
        this.port = port;
    }

    public String getAddressToConnectTo() {
        return addressToConnectTo;
    }

    public int getPort() {
        return port;
    }
}
