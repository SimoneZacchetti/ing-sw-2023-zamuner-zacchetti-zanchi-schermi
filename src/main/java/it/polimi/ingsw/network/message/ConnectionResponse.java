package it.polimi.ingsw.network.message;

import it.polimi.ingsw.enumeration.MessageContent;
import it.polimi.ingsw.enumeration.MessageStatus;

/**
 * Message class of response after a connection request
 */
public class ConnectionResponse extends Message {
    private static final long serialVersionUID = 8971780730242420173L;

    private final String newToken;
    private final String message;
    private final MessageStatus status;

    public ConnectionResponse(String message, String newToken, MessageStatus status) {
        super("ServerUser", null, MessageContent.CONNECTION_RESPONSE);
        this.message = message;
        this.newToken = newToken;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getNewToken() {
        return newToken;
    }

    public MessageStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ConnectionResponse{" +
                "content=" + getContent() +
                ", newToken='" + newToken + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
