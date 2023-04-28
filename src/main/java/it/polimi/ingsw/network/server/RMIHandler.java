package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.client.RMIClientConnection;
import it.polimi.ingsw.network.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is the one sent to the RMI client enabling him to send messages to server
 */
public interface RMIHandler extends Remote {
    /**
     * Tries to execute the login with the server
     *
     * @param username username used for the login
     * @param client   client connection
     */
    void login(String username, RMIClientConnection client) throws RemoteException;

    /**
     * Sends a message to the server
     *
     * @param message message sent to server
     */
    void onMessage(Message message) throws RemoteException;

    /**
     * Disconnects the client from the server
     */
    void disconnectMe() throws RemoteException;
}
