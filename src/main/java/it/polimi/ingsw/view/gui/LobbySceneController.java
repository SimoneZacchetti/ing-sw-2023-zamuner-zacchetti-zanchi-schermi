package it.polimi.ingsw.view.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

import static it.polimi.ingsw.network.client.ClientGameManager.LOGGER;

/**
 * Class LobbySceneController represents the controller of the lobby scene, the scene where the player waits for the
 * game to start
 */

public class LobbySceneController {

    private GuiManager guiManager;

    @FXML
    private Pane mainPane;
    @FXML
    private ImageView exitButton;
    @FXML
    private ImageView backButton;
    @FXML
    private VBox lobbyLabelsBox;

    /**
     * Initializes the scene
     */
    @FXML
    public void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setLobbySceneController(this);
        bindEvents();
    }

    /**
     * Binds click events
     */
    private void bindEvents() {
        backButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onBackButtonClick());
        exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));
    }

    /**
     * Handles the back button click
     */
    private void onBackButtonClick() {
        guiManager.closeConnection();
        GuiManager.setLayout(mainPane.getScene(), "fxml/connectionScene.fxml");
    }

    /**
     * Handles the game start when the lobby is full
     */
    void onGameStart() {
        GameSceneController gameSceneController =
                GuiManager.setLayout(mainPane.getScene(), "fxml/gameScene.fxml");

        //LOGGER.severe("GameSceneController: " + gameSceneController);

        if (gameSceneController != null) {
            gameSceneController.setupGame(guiManager.getGameSerialized());
        }
    }

    /**
     * Updates the lobby list
     */
    void updateLobbyList() {
        List<String> users = guiManager.getLobbyPlayers();
        ObservableList<Node> childrens = lobbyLabelsBox.getChildren();
        childrens.clear();

        for (String user : users) {
            Label lbl = new Label();
            lbl.setText(user);
            lbl.getStyleClass().add("playerRow");

            childrens.add(lbl);
        }
    }

    /**
     * Handles an error occurrence
     *
     * @param error message of error
     */
    void onError(String error) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, error);
    }

    /**
     * Handles the server disconnection
     */
    void onDisconnection() {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", "You were disconnected from the server");
    }
}