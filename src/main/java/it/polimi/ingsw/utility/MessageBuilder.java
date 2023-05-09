package it.polimi.ingsw.utility;


import it.polimi.ingsw.model.ObjectCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.ConnectionRequest;
import it.polimi.ingsw.network.message.LobbyMessage;
import it.polimi.ingsw.network.message.NumberOfPlayersMessage;
import it.polimi.ingsw.network.message.ObjectCardRequest;

public class MessageBuilder {


    private MessageBuilder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create a {@link ConnectionRequest ConnectionRequest} object from {@code username}
     *
     * @param username username chosen by the user to request from the server if available
     * @return the {@link ConnectionRequest ConnectionRequest} object to send to the server
     */
    public static ConnectionRequest buildConnectionRequest(String username) {
        return new ConnectionRequest(username);
    }

    /**
     * Create a {@link LobbyMessage LobbyMessage} object from {@code username}
     *
     * @param token         is the user token
     * @param username      is the username under which the user log itself in
     * @param disconnection tell if the user wants to disconnect
     * @return the {@link LobbyMessage LobbyMessage} object to send to the server
     */
    public static LobbyMessage buildAddPlayerToGameMessage(String token, String username, boolean disconnection) {
        return new LobbyMessage(username, token, disconnection);
    }

    public static NumberOfPlayersMessage buildNumberOfPlayerMessage(String token, String username, int numberOfPlayers) {
        return new NumberOfPlayersMessage(username, token, numberOfPlayers);
    }

    public static ObjectCardRequest buildPickObjectCardRequest(Player p, String token, ObjectCard objectCard) {
        if (p == null || objectCard == null)
            throw new NullPointerException("Player and spawnSquare cannot be null");
        
        return new ObjectCardRequest(p.getName(), token, objectCard);
    }
}