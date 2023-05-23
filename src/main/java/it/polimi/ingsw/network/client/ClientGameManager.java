package it.polimi.ingsw.network.client;

import it.polimi.ingsw.enumeration.MessageStatus;
import it.polimi.ingsw.enumeration.PossibleAction;
import it.polimi.ingsw.enumeration.UserPlayerState;
import it.polimi.ingsw.model.CommonGoal;
import it.polimi.ingsw.model.GameSerialized;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class is used to manage the game on the client side
 */
public abstract class ClientGameManager implements ClientGameManagerListener, ClientUpdateListener, Runnable {
    public static final Logger LOGGER = Logger.getLogger("my_shelfie_client");

    public static final String ERROR_DIALOG_TITLE = "Error";
    public static final String SEND_ERROR = "Error while sending the request";
    protected static final String INVALID_STRING = "Invalid String!";
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Object gameSerializedLock = new Object();
    private Client client;
    private boolean joinedLobby;
    private List<String> lobbyPlayers;
    private String firstPlayer = null;
    private String turnOwner;
    private boolean firstTurn;
    private boolean yourTurn;
    private boolean turnOwnerChanged;
    private ClientTurnManager turnManager; // manage the rounds of this client
    private ClientUpdater clientUpdater;
    private boolean gameEnded = false;
    private GameSerialized gameSerialized;

    /**
     * constructor of the class
     */
    public ClientGameManager() {
        firstTurn = true;
        joinedLobby = false;
        turnOwnerChanged = false;

        Date date = GregorianCalendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-mm_HH.mm.ss");

        try {
            FileHandler fh = new FileHandler("log/client-" + dateFormat.format(date) + ".log");
            fh.setFormatter(new SimpleFormatter());
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                queue.take().run();
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * @return the username of the client
     */
    public String getUsername() {
        return client.getUsername();
    }

    /**
     * @return the client token
     */
    public String getClientToken() {
        return client.getToken();
    }

    /**
     * method called when a message is received from the server
     * @param message the received message from the server
     */
    @Override
    public void onUpdate(Message message) {
        LOGGER.log(Level.INFO, "Received: {0}", message);

        if (gameEnded) {
            return;
        }

        switch (message.getContent()) {
            case CONNECTION_RESPONSE:
                handleConnectionResponse((ConnectionResponse) message);
                break;

            case PLAYERS_IN_LOBBY:
                handlePlayersInLobby((LobbyPlayersResponse) message);
                break;

            case RESPONSE:
                handleResponse((Response) message);
                break;

            case GAME_STATE:
                handleGameStateResponse((GameStateResponse) message);
                break;

            case READY:
                handleGameStartMessage((GameStartMessage) message);
                break;

            case GAME_ENDED:
                handleGameEnded((EndGameMessage) message);
                break;

            case DISCONNECTION:
//                handleDisconnection((DisconnectionMessage) message);
                break;

            default:
        }
    }

    /**
     * @return the game serialized
     */
    public GameSerialized getGameSerialized() {
        synchronized (gameSerializedLock) {
            return gameSerialized;
        }
    }

    /**
     * method called when the game is going to start, it set the turn owner
     * @param gameStartMessage the message received from the server
     */
    private void handleGameStartMessage(GameStartMessage gameStartMessage) {
        synchronized (gameSerializedLock) {
            firstPlayer = gameStartMessage.getFirstPlayer();
            gameSerialized = gameStartMessage.getGameSerialized();
        }

        turnOwner = gameStartMessage.getFirstPlayer();
        startGame(gameStartMessage.getCommonGoals());
    }

    /**
     * method used to send a message to the server
     * @param message the message to send
     * @return true if the message is sent, false otherwise
     */
    public boolean sendRequest(Message message) {
        if (turnManager != null) {
            // TODO da usare?
//            checkChangeStateRequest(message);
        }

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Handles the update of the game state
     *
     * @param gameStateMessage game state update received
     */
    // il game state lo mandiamo all'inizio di ogni turno? o anche durante le fasi di gioco in un turno?
    private void handleGameStateResponse(GameStateResponse gameStateMessage) {
        synchronized (gameSerializedLock) {
            gameSerialized = gameStateMessage.getGameSerialized();
        }

        queue.add(this::gameStateUpdate);

        checkTurnChange(gameStateMessage);
    }

    /**
     * Checks if the turn owner is changed
     *
     * @param stateMessage game state message received
     */
    private void checkTurnChange(GameStateResponse stateMessage) {
        if (!firstTurn) {
            System.out.println(getGameSerialized().getCurrentPlayer() + " " + turnOwner);
            if (!getGameSerialized().getCurrentPlayer().getName().equals(turnOwner)) {
                turnOwner = stateMessage.getTurnOwner();
                turnOwnerChanged = true;
            }

            if (!yourTurn) { // If you are not the turn owner you don't need to wait a response
                turnOwnerChanged = false;

                if (turnOwner.equals(getUsername())) {
                    yourTurn = true;
                }

                System.out.println("CHECK TURN CHANGE");
                newTurn();
            }
        }
    }

    /**
     * Handles the response to the server connection
     *
     * @param connectionResponse response received
     */
    private void handleConnectionResponse(ConnectionResponse connectionResponse) {
        if (connectionResponse.getStatus().equals(MessageStatus.OK)) {
            client.setToken(connectionResponse.getNewToken());
        } else {
            client.pingTimer.cancel();
            closeConnection();
        }

        queue.add(() -> connectionResponse(connectionResponse));
    }

    /**
     * method called when the players in the lobby are received
     * @param message the message received from the server
     */
    private void handlePlayersInLobby(LobbyPlayersResponse message) {
        lobbyPlayers = message.getUsers();
        queue.add(() -> playersWaitingUpdate(message.getUsers()));
    }

    /**
     * method called when a response is received from the server
     * @param response the response received from the server
     */
    private void handleResponse(Response response) {
        if (!joinedLobby) {
            joinedLobby = response.getStatus() == MessageStatus.OK;

            if (lobbyPlayers.size() == 1) queue.add(() -> numberOfPlayersRequest(response));
            queue.add(() -> lobbyJoinResponse(response));
        } else {
            if (response.getStatus() == MessageStatus.ERROR) {
                queue.add(() -> responseError(response.getMessage()));
            } else {
                onPositiveResponse(response);
            }
        }
        if (firstPlayer != null) checkNextAction();
    }

    /**
     * method called when the game is ended
     * @param message the message received from the server
     */
    private void handleGameEnded(EndGameMessage message) {
        gameEnded = true;
        synchronized (gameSerializedLock) {
            gameSerialized = message.getGameSerialized();
        }
        queue.add(() -> printWinner(gameSerialized));
    }

    /**
     * @return the user player state
     */
    public UserPlayerState getUserPlayerState() {
        return turnManager.getUserPlayerState();
    }

    /**
     * Check what is the next action for the client
     */
    private void checkNextAction() {
        if (turnManager.getUserPlayerState() != UserPlayerState.ENDING_PHASE) {
            makeMove();
        } else {
            turnManager.endTurn();
        }

        System.out.println(yourTurn + "  -  " + turnOwnerChanged);
        if (yourTurn && turnOwnerChanged) {
            turnOwnerChanged = false;
            yourTurn = false;

            System.out.println("CHECK NEXT ACTION");
            newTurn();
        }
    }

    /**
     * method called when a positive response is received from the server
     * @param response the response received from the server
     */
    private void onPositiveResponse(Response response) {
        if (response.getStatus() == MessageStatus.PRINT_LIMBO) {
            queue.add(this::printLimbo);
        }
        if (response.getStatus() == MessageStatus.GAME_ENDED) {
            gameEnded = true;
            queue.add(() -> printEndGame(response.getMessage()));
        }
        if (turnManager != null) {
            turnManager.nextState();
        }
    }

    /**
     * method called to create the connection with the server
     * @param connection the type of connection to create (RMI or Socket)
     * @param username the username of the player
     * @param address the ip address of the server
     * @param port the port of the server
     * @param disconnectionListener the listener for the disconnection
     * @throws Exception
     */
    public void createConnection(int connection, String username, String address, int port, DisconnectionListener disconnectionListener) throws Exception {
        if (connection == 0) {
            client = new ClientSocket(username, address, port, disconnectionListener);
        } else {
            client = new ClientRMI(username, address, port, disconnectionListener);
        }

        client.startConnection();
        startUpdater();
    }

    public void closeConnection() {
        if (clientUpdater != null) {
            clientUpdater.stop();
            clientUpdater = null;
        }

        try {
            client.close();
        } catch (Exception e) {
            // No issues
        }
        client = null;
    }

    /**
     * start a client updater
     */
    private void startUpdater() {
        clientUpdater = new ClientUpdater(client, this);
    }

    /**
     * Returns a player based on the provided username
     *
     * @return the player of the client
     */
    public Player getPlayer() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayers().stream().filter(p -> p.getName().equals(getUsername())).findFirst().orElse(null);
        }
    }

    /**
     * @return a list of all the players
     */
    public List<Player> getPlayers() {
        synchronized (gameSerializedLock) {
            return gameSerialized.getPlayers();
        }
    }

    /**
     * method used to start the game
     * @param cg the list of common goals
     */
    private void startGame(List<CommonGoal> cg) {
        turnManager = new ClientTurnManager();

        if (firstTurn) {
            if (firstPlayer.equals(getUsername())) { // First player to play
                yourTurn = true;
            }

            queue.add(() -> firstPlayerCommunication(firstPlayer, cg));
            queue.add(this::gameStateUpdate);
            // TODO cosi stampa prima inizio gioco poi chiede lo stato ma problema di sincro con la ricezione messaggi
//            queue.add(() -> gameStateRequest(getUsername(), getClientToken()));

            firstTurn = false;
        }

        newTurn();
    }

    /**
     * Called when a change of turn owner happen
     */
    private void newTurn() {
        if (yourTurn) {
            turnManager.startTurn();
            makeMove();
        } else {
            queue.add(() -> notYourTurn(turnOwner));
        }
    }

    /**
     * Show the client all the possible actions
     */
    public void makeMove() {
        if (getUsername().equals(turnOwner)) {
            queue.add(() -> displayActions(getPossibleActions()));
        }
    }

    /**
     * @return a list of possible actions based on the current state of the player
     */
    private List<PossibleAction> getPossibleActions() {
        switch (turnManager.getUserPlayerState()) {
            case PICK_CARD_BOARD:
                return List.of(PossibleAction.BOARD_PICK_CARD);

            case AFTER_FIRST_PICK: {
                if (gameSerialized.getLimbo().size() == 3) {
                    return List.of(PossibleAction.LOAD_SHELF, PossibleAction.REORDER_LIMBO, PossibleAction.DELETE_LIMBO);
                } else if (gameSerialized.getLimbo().size() > 1) {
                    return List.of(PossibleAction.BOARD_PICK_CARD, PossibleAction.LOAD_SHELF, PossibleAction.REORDER_LIMBO, PossibleAction.DELETE_LIMBO);
                } else {
                    return List.of(PossibleAction.BOARD_PICK_CARD, PossibleAction.LOAD_SHELF, PossibleAction.DELETE_LIMBO);
                }
            }

            case LOADING_SHELF:
                return List.of(PossibleAction.LOAD_SHELF);

            case DELETE_LIMBO: // non ci dovrebbe mai entrare perche viene risettato lo stato a pick_card_board


//            case DEAD:
//                return List.of(PossibleAction.CHOOSE_RESPAWN);

            default:
                return null;
//                throw new ClientRoundManagerException("Cannot be here: " + roundManager.getUserPlayerState().name());
        }
    }

    /**
     * Executes the action chosen
     *
     * @param chosenAction action chosen by the user
     */
    public void doAction(PossibleAction chosenAction) {
        Runnable action = null;

        switch (chosenAction) {
            case BOARD_PICK_CARD:
                System.out.println("SCEGLI CARTA");
                action = this::pickBoardCard;
                break;
            case LOAD_SHELF:
                System.out.println("CARICA SHELF");
                turnManager.loadingShelf();
                action = this::chooseColumn;
                break;
            case REORDER_LIMBO:
                System.out.println("SCEGLI ORDINE");
                action = this::reorderLimbo;
                break;
            case DELETE_LIMBO:
                System.out.println("ELIMINO LIMBO");
                turnManager.deleteLimbo();
                action = this::deleteLimbo;
                break;

            default:
//                throw new ClientRoundManagerException("Invalid Action");
        }

        queue.add(action);
    }

    /**
     * @return the players in lobby
     */
    public List<String> getLobbyPlayers() {
        return lobbyPlayers;
    }
}
