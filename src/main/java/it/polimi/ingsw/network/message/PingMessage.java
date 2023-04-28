package it.polimi.ingsw.network.message;

import it.polimi.ingsw.utility.GameConstants;
import it.polimi.ingsw.utility.MessageContent;

/**
 * Empty message used as a ping message
 */
public class PingMessage extends Message {
    private static final long serialVersionUID = 8092508198825773159L;

    public PingMessage() {
        super(GameConstants.GOD_NAME, null, MessageContent.PING);
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                "}";
    }
}
