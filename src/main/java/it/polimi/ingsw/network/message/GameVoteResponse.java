package it.polimi.ingsw.network.message;

import enumerations.MessageContent;
import enumerations.MessageStatus;
import utility.GameConstants;

/**
 * Message class to respond to a vote map
 */
public class GameVoteResponse extends Message {
    private static final long serialVersionUID = -6209158395966916144L;

    private final String message;
    private final MessageStatus status;

    public GameVoteResponse(String message, MessageStatus status) {
        super(GameConstants.GOD_NAME, null, MessageContent.VOTE_RESPONSE);
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public MessageStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "GameVoteResponse{" +
                "content=" + getContent() +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
