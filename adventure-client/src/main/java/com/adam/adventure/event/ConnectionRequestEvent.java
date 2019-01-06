package com.adam.adventure.event;

public class ConnectionRequestEvent extends Event {
    private final String addressToConnectTo;

    public ConnectionRequestEvent(final String addressToConnectTo) {
        this.addressToConnectTo = addressToConnectTo;
    }

    public String getAddressToConnectTo() {
        return addressToConnectTo;
    }
}
