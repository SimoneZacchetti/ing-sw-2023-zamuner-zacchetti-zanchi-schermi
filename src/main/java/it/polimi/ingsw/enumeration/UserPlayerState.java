package it.polimi.ingsw.enumeration;

/**
 * Enumeration representing all the possible states that the client round manager can have.
 * These states are mapped to the ones in the controller thanks to a mapping function
 * used to reload a game in persistency
 */
public enum UserPlayerState {
    // TODO cambiare con gli stati delle azioni di game
    DISCONNECTED,
    PICK_CARD_BOARD,
    AFTER_FIRST_PICK, // dopo aver pescato la prima carta
    FIRST_ACTION,
    ENDING_PHASE,
    END,
    DELETE_LIMBO,
    LOADING_SHELF,
    GAME_ENDED
}
