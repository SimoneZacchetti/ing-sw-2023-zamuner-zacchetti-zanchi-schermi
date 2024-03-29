package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.RMIClientConnection;
import it.polimi.ingsw.network.message.Message;

import java.rmi.RemoteException;

/**
 * This class represents a RMI Connection with a client
 */
public class RMIConnection extends Connection {
    private final Server server;
    private final RMIClientConnection clientSession;

    private boolean connected = true;

    /**
     * Construct a connection between the server and a RMI client
     *
     * @param server        server where the client is connected
     * @param clientSession RMI client connected to the server
     */
    RMIConnection(Server server, RMIClientConnection clientSession) {
        this.server = server;
        this.clientSession = clientSession;
    }

    /**
     * Returns the connection status
     * @return the connection status
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Send a message to the client
     *
     * @param message message to send to the client
     * @throws RemoteException in case of problems with communication with client
     */
    @Override
    public void sendMessage(Message message) throws RemoteException {
        clientSession.onMessage(message);
    }

    /**
     * Disconnects from the client
     */
    @Override
    public void disconnect() {
        if (connected) {
            connected = false;

            try {
                clientSession.disconnectMe();
            } catch (RemoteException e) {
            }

            server.onDisconnect(this);
        }
    }

    /**
     * Sends a ping message to client
     */
    @Override
    public void ping() {
        try {
            clientSession.ping();
        } catch (RemoteException e) {
            disconnect();
        }
    }
}