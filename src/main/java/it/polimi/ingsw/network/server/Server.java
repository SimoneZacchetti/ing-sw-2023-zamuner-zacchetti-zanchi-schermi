package it.polimi.ingsw.network.server;

import it.polimi.ingsw.control.ControllerGame;
import it.polimi.ingsw.enumeration.MessageContent;
import it.polimi.ingsw.enumeration.MessageStatus;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.message.ConnectionResponse;
import it.polimi.ingsw.network.message.DisconnectionMessage;
import it.polimi.ingsw.network.message.Message;
import it.polimi.ingsw.network.message.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

/**
 * This class is the main server class which starts a Socket and a RMI server.
 * It handles all the client regardless of whether they are Sockets or RMI
 */
public class Server implements Runnable {
    public static final Logger LOGGER = Logger.getLogger("Server");
    private static final String DEFAULT_CONF_FILE_PATH = "conf.json";
    private final Object clientsLock = new Object();
    private int socketPort = 2727;
    private int rmiPort;
    private Map<String, Connection> clients;
    private ControllerGame controllerGame;
    private boolean waitForLoad;
    private int startTime;
    private int moveTime;

    private Timer moveTimer;

    public Server() {
        initLogger();
        synchronized (clientsLock) {
            clients = new HashMap<>();
        }
        waitForLoad = false;

        startServers();

        controllerGame = new ControllerGame();

        Thread pingThread = new Thread(this);
        pingThread.start();

        moveTimer = new Timer();
    }

    public static void main(String[] args) {
        new Server();
    }

    private void initLogger() {
        Date date = GregorianCalendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM_HH.mm.ss");

        try {
            FileHandler fh = new FileHandler("log/server-" + dateFormat.format(date) + ".log");
            fh.setFormatter(new SimpleFormatter());

            LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private void startServers() {
        SocketServer serverSocket = new SocketServer(this, socketPort);
        serverSocket.startServer();

        LOGGER.info("Socket Server Started");

//        RMIServer rmiServer = new RMIServer(this, rmiPort);
//        rmiServer.startServer();

//        LOGGER.info("RMI Server Started");
    }

    /**
     * Reserves server slots for player loaded from the game save
     *
     * @param loadedPlayers from the game save
     */
    private void reserveSlots(List<Player> loadedPlayers) {
        synchronized (clientsLock) {
            for (Player player : loadedPlayers) {
                clients.put(player.getName(), null);
            }
        }
    }

    /**
     * Adds or reconnects a player to the server
     *
     * @param username   username of the player
     * @param connection connection of the client
     */
    void login(String username, Connection connection) {
        try {
            synchronized (clientsLock) {
                if (clients.containsKey(username)) {
//                    knownPlayerLogin(username, connection);
                } else {
                    newPlayerLogin(username, connection);
                }
            }
        } catch (IOException e) {
            connection.disconnect();
        }
    }

    /**
     * Handles a known player login
     *
     * @param username   username of the player who is trying to login
     * @param connection connection of the client
     * @throws IOException when send message fails
     */
//    private void knownPlayerLogin(String username, Connection connection) throws IOException {
//        if (clients.get(username) == null || !clients.get(username).isConnected()) { // Player Reconnection
//            clients.replace(username, connection);
//
//            String token = UUID.randomUUID().toString();
//            connection.setToken(token);
//
//            if (waitForLoad) {// Game in lobby state for load a game
//                connection.sendMessage(
//                        new GameLoadResponse("Successfully reconnected", token,
//                                controllerGame.getUserPlayerState(username), controllerGame.getGame().isBotPresent())
//                );
//                checkLoadReady();
//            } else {
//                if (controllerGame.getGameState() == PossibleGameState.GAME_ROOM) { // Game in lobby state
//                    connection.sendMessage(
//                            new ConnectionResponse("Successfully reconnected", token, MessageStatus.OK)
//                    );
//                } else { // Game started
//                    connection.sendMessage(
//                            controllerGame.onConnectionMessage(new LobbyMessage(username, token, null, false))
//                    );
//                }
//            }
//
//            LOGGER.log(Level.INFO, "{0} reconnected to server!", username);
//        } else { // Player already connected
//            connection.sendMessage(
//                    new ConnectionResponse("Player already connected", null, MessageStatus.ERROR)
//            );
//
//            connection.disconnect();
//            LOGGER.log(Level.INFO, "{0} already connected to server!", username);
//        }
//    }

    /**
     * Handles a new player login
     *
     * @param username   username of the player who is trying to login
     * @param connection connection of the client
     * @throws IOException when send message fails
     */
    private void newPlayerLogin(String username, Connection connection) throws IOException {
        if (controllerGame.getGame().isHasStarted()) { // Game Started
            connection.sendMessage(
                    new ConnectionResponse("Game is already started!", null, MessageStatus.ERROR)
            );

            connection.disconnect();
            LOGGER.log(Level.INFO, "{0} attempted to connect!", username);
        } else {
            clients.put(username, connection);

            String token = UUID.randomUUID().toString();
            connection.setToken(token);

            connection.sendMessage(
                    new ConnectionResponse("Successfully connected", token, MessageStatus.OK)
            );

            LOGGER.log(Level.INFO, "{0} connected to server!", username);
        }
    }

    /**
     * Process a message sent to server
     *
     * @param message message sent to server
     */
    void onMessage(Message message) {
        if (message != null && message.getSenderUsername() != null && (message.getToken() != null || message.getSenderUsername().equals("god"))) {
            if (message.getContent().equals(MessageContent.SHOOT)) {
                String messageString = message.toString();
                LOGGER.log(Level.INFO, messageString);
            } else {
                LOGGER.log(Level.INFO, "Received: {0}", message);
            }

            String msgToken = message.getToken();
            Connection conn;

            synchronized (clientsLock) {
                conn = clients.get(message.getSenderUsername());
            }

            if (conn == null) {
                LOGGER.log(Level.INFO, "Message Request {0} - Unknown username {1}", new Object[]{message.getContent().name(), message.getSenderUsername()});
            } else if (msgToken.equals(conn.getToken())) { // Checks that sender is the real player
//                Message response = controllerGame.onMessage(message);
                Message response = new Response("MEESSAGGGIOOOOO", MessageStatus.ERROR);
                // send message to client
                sendMessage(message.getSenderUsername(), response);
            }
        }
    }

    /**
     * Called when a player disconnects
     *
     * @param playerConnection connection of the player that just disconnected
     */
    void onDisconnect(Connection playerConnection) {
        String username = getUsernameByConnection(playerConnection);

        if (username != null) {
            LOGGER.log(Level.INFO, "{0} disconnected from server!", username);

//            if (controllerGame.getGameState() == PossibleGameState.GAME_ROOM) {
//                synchronized (clientsLock) {
//                    clients.remove(username);
//                }
//                controllerGame.onMessage(new LobbyMessage(username, null, true));
//            LOGGER.log(Level.INFO, "{0} removed from client list!", username);
//            }
//        else {
//                controllerGame.onConnectionMessage(new LobbyMessage(username, null, true));
            sendMessageToAll(new DisconnectionMessage(username));
//            }
        }
    }

    /**
     * Sends a message to all clients
     *
     * @param message message to send
     */
    public void sendMessageToAll(Message message) {
        for (Map.Entry<String, Connection> client : clients.entrySet()) {
            if (client.getValue() != null && client.getValue().isConnected()) {
                try {
                    client.getValue().sendMessage(message);
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }
        LOGGER.log(Level.INFO, "Send to all: {0}", message);
    }

    /**
     * Sends a message to a client
     *
     * @param username username of the client who will receive the message
     * @param message  message to send
     */
    public void sendMessage(String username, Message message) {
        synchronized (clientsLock) {
            for (Map.Entry<String, Connection> client : clients.entrySet()) {
                if (client.getKey().equals(username) && client.getValue() != null && client.getValue().isConnected()) {
                    try {
                        client.getValue().sendMessage(message);
                    } catch (IOException e) {
                        LOGGER.severe(e.getMessage());
                    }
                    break;
                }
            }
        }

        LOGGER.log(Level.INFO, "Send: {0}, {1}", new Object[]{message.getSenderUsername(), message});
    }

    /**
     * Returns the username of the connection owner
     *
     * @param connection connection to check
     * @return the username
     */
    private String getUsernameByConnection(Connection connection) {
        Set<String> usernameList;
        synchronized (clientsLock) {
            usernameList = clients.entrySet()
                    .stream()
                    .filter(entry -> connection.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
        }
        if (usernameList.isEmpty()) {
            return null;
        } else {
            return usernameList.iterator().next();
        }
    }

    /**
     * Process that pings all the clients to check if they are still connected
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (clientsLock) {
                for (Map.Entry<String, Connection> client : clients.entrySet()) {
                    if (client.getValue() != null && client.getValue().isConnected()) {
                        client.getValue().ping();
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
