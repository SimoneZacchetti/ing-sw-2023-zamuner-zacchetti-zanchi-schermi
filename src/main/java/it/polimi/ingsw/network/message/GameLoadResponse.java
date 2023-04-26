package it.polimi.ingsw.network.message;

import enumerations.MessageContent;
import enumerations.UserPlayerState;
import utility.GameConstants;

/**
 * Message class that is sent when a load game is completed successfully
 */
public class GameLoadResponse extends Message {
    private static final long serialVersionUID = 4880522547664967982L;

    private final String newToken;
    private final String message;
    private final UserPlayerState userPlayerState;
    private final Boolean botPresent;

    public GameLoadResponse(String message, String newToken, UserPlayerState userPlayerState, Boolean botPresent) {
        super(GameConstants.GOD_NAME, null, MessageContent.GAME_LOAD);
        this.message = message;
        this.newToken = newToken;
        this.userPlayerState = userPlayerState;
        this.botPresent = botPresent;
    }

    public String getMessage() {
        return message;
    }

    public String getNewToken() {
        return newToken;
    }

    public UserPlayerState getUserPlayerState() {
        return userPlayerState;
    }

    public Boolean isBotPresent() {
        return botPresent;
    }
}
