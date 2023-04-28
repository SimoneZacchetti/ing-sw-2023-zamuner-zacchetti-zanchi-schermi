package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.message.ConnectionResponse;
import it.polimi.ingsw.network.message.Response;

import java.util.List;

/**
 * This listener is used to let the game progress via user inputs.
 * Methods are called when is received a specific message from the server
 */
interface ClientGameManagerListener {
    /**
     * Handles the response to a connection
     */
    void connectionResponse(ConnectionResponse response);

    /**
     * Handles the response to a connection for a loaded game
     */
    void loadResponse();

    /**
     * Handles the response to the lobby join
     *
     * @param response to the lobby join
     */
    void lobbyJoinResponse(Response response);

    /**
     * Notifies an update of players in the lobby
     *
     * @param users list of users in the lobby
     */
    void playersLobbyUpdate(List<String> users);

    /**
     * Tells the client that is not his turn
     */
    void notYourTurn(String turnOwner);
}
