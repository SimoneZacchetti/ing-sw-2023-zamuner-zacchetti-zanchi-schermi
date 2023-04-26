package it.polimi.ingsw.utility;

import java.util.List;

public class GameConstants {
    public static final String BOT_NAME = "bot";
    public static final String GOD_NAME = "god";
    public static final String CANCEL_KEYWORD = "cancel";
    private static final List<String> FORBIDDEN_USERNAME = List.of(BOT_NAME, GOD_NAME, CANCEL_KEYWORD);

    private GameConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> getForbiddenUsernames() {
        return List.copyOf(FORBIDDEN_USERNAME);
    }
}
