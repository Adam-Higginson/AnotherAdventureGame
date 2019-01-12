package com.adam.adventure.event;

/**
 * An event which specifies we want to connect to a given server. Curently this is intercepted by
 * the {@link com.adam.adventure.entity.component.network.NetworkManagerComponent} and we attempt to connect to the
 * server.
 */
public class RequestConnectionToServerEvent extends Event {
    private final String username;
    private final String addressToConnectTo;
    private final int port;

    public RequestConnectionToServerEvent(final String username, final String addressToConnectTo, final int port) {
        this.username = username;
        this.addressToConnectTo = addressToConnectTo;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getAddressToConnectTo() {
        return addressToConnectTo;
    }

    public int getPort() {
        return port;
    }
}
