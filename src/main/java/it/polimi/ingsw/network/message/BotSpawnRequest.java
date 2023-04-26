package it.polimi.ingsw.network.message;

import enumerations.MessageContent;
import enumerations.RoomColor;

/**
 * Message class for requesting the bot spawn
 */
public class BotSpawnRequest extends Message {
    private static final long serialVersionUID = 1272585248979532293L;

    private final RoomColor spawnColor;

    public BotSpawnRequest(String username, String token, RoomColor spawnColor) {
        super(username, token, MessageContent.BOT_SPAWN);

        this.spawnColor = spawnColor;
    }

    public RoomColor getSpawnColor() {
        return this.spawnColor;
    }

    @Override
    public String toString() {
        return "TerminatorSpawnRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", spawnColor=" + spawnColor +
                '}';
    }
}
