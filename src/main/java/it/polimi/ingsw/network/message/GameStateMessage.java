package it.polimi.ingsw.network.message;

import enumerations.MessageContent;
import model.GameSerialized;
import utility.GameConstants;

/**
 * Message class to inform the clients about a game state update
 */
public class GameStateMessage extends Message {
    private static final long serialVersionUID = 2725986184174583892L;

    private final GameSerialized gameSerialized;
    private final String turnOwner;
    private boolean grenadeUsage;

    public GameStateMessage(String username, String turnOwner, boolean grenadeUsage) {
        super(GameConstants.GOD_NAME, null, MessageContent.GAME_STATE);
        this.gameSerialized = new GameSerialized(username);
        this.turnOwner = turnOwner;
        this.grenadeUsage = grenadeUsage;
    }

    public GameSerialized getGameSerialized() {
        return gameSerialized;
    }

    public String getTurnOwner() {
        return turnOwner;
    }

    public boolean isGrenadeUsage() {
        return grenadeUsage;
    }
}
