package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.enumeration.PossibleAction;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.utility.MessageBuilder;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for the graphical interface of the game
 */
public class GameSceneController {
    private static final String USERNAME_PROPERTY = "username";
    private static final String POINTS_PROPERTY = "Points: ";
    private static final String TRANSPARENT_IMAGEVIEW_ID = "transparent";
    private static final String CSS_BUTTON = "button";
    private static final String CSS_SHELF = "shelf";
    private static final String CSS_SHELF_GRIDPANE = "shelfGridPane";
    private static final String CSS_SHELF_LABEL = "shelfLabel";
    private static final String CSS_PLAYERINFO_LABEL = "playerInfo";
    private static final String CSS_PLAYERINFO_SEPARATOR = "labelSeparator";
    private static final String CSS_LIMBO_HBOX = "limboHBoxArea";
    private static final double COMMONGOAL_CARD_WIDTH = 138.5;
    private static final double COMMONGOAL_CARD_HEIGHT = 91.3;
    private static final double COMMONGOAL_POINTS_WIDTH = 39.5;
    private static final double COMMONGOAL_POINTS_HEIGHT = 39.5;
    private static final double COMMONGOAL_POINTS_ROTATE = -8.0;
    private static final double COMMONGOAL_POINTS_TRANSLATE_X = 32.2;
    private static final double COMMONGOAL_POINTS_TRANSLATE_Y = -3.5;
    private static final double BOARD_OBJECT_CARD_WIDTH = 60.0;
    private static final double BOARD_OBJECT_CARD_HEIGHT = 60.0;
    private static final double SHELF_OBJECT_CARD_WIDTH = 47.0;
    private static final double SHELF_OBJECT_CARD_HEIGHT = 47.0;
    private static final double PERSONALGOAL_CARD_WIDTH = 137.0;
    private static final double PERSONALGOAL_CARD_HEIGHT = 207.9;
    private static final double PERSONALGOAL_CARD_TRANSLATE_Y = 51.5;
    private static final double SHELF_WIDTH = 400.0;
    private static final double SHELF_HEIGHT = 400.0;
    private static final double SHELF_GRIDPANE_HGAP = 17.0;
    private static final double SHELF_GRIDPANE_VGAP = 9.0;
    private static final double SHELF_GRIDPANE_MAXHEIGHT = 324.0;
    private static final double SHELF_GRIDPANE_MAXWIDTH = 304.0;
    private static final double SHELF_GRIDPANE_TRANSLATE_Y = -11.0;
    private static final double LIMBO_OBJECT_CARD_WIDTH = 75.0;
    private static final double LIMBO_OBJECT_CARD_HEIGHT = 75.0;
    private static final double MYSHELFIE_LOGO_WIDTH = 300;
    private static final double MYSHELFIE_LOGO_HEIGHT = 100;
    private static final double MYSHELFIE_LOGO_TRANSLATE_X = 48;
    private static final double ACTION_BUTTON_WIDTH = 190.0;
    private static final double ACTION_BUTTON_HEIGHT = 70.0;
    private static final String SHELF_PATH = "/img/board_shelf/shelf_orth.png";
    private static final String MYSHELFIE_LOGO_PATH = "/img/logos/nanoTitleV1.png";

    @FXML
    Pane mainPane;
    @FXML
    StackPane gameStackPaneArea;
    @FXML
    HBox boardShelfHBoxArea;
    @FXML
    VBox boardCommonGoalCardsVBoxArea;
    @FXML
    StackPane boardStackPaneArea;
    @FXML
    ImageView boardImage;
    @FXML
    GridPane boardGridPane;
    @FXML
    ImageView endGameTokenImage;
    @FXML
    HBox commonGoalCardsHBoxArea;
    @FXML
    StackPane commonGoalCard1StackPane;
    @FXML
    StackPane commonGoalCard2StackPane;
    @FXML
    VBox shelfLimboVBoxArea;
    @FXML
    HBox limboHBoxArea;
    @FXML
    AnchorPane columnArrowAnchorPane;
    @FXML
    ImageView arrowShelf0;
    @FXML
    ImageView arrowShelf1;
    @FXML
    ImageView arrowShelf2;
    @FXML
    ImageView arrowShelf3;
    @FXML
    ImageView arrowShelf4;
    @FXML
    ScrollPane shelfScrollPane;
    @FXML
    HBox shelfHBoxImages;
    @FXML
    StackPane myStackPane;
    @FXML
    StackPane shelfStackPane2;
    @FXML
    StackPane shelfStackPane3;
    @FXML
    StackPane shelfStackPane4;
    @FXML
    StackPane actionListStackPane;
    @FXML
    VBox playersInfoPersonalGoalCardVBox;
    @FXML
    VBox playersInfoVBox;
    @FXML
    StackPane personalGoalCardPane;
    @FXML
    FlowPane commonGoalCardInfoPanel;

    private GuiManager guiManager;
    private Map<String, ImageView> objectCards;
    private Map<String, ImageView> commonGoalCards;
    private Map<String, ImageView> personalGoalCards;
    private Map<String, ImageView> commonGoalPoints;
    private List<GridPane> shelvesGridPane;
    private List<StackPane> shelvesStackPane;
    private ArrayList<Integer> orderLimboObjectCards;
    private Set<ImageView> imageViewsWithListener;

    /**
     * Method to initialize the game scene
     */
    @FXML
    private void initialize() {
        guiManager = GuiManager.getInstance();
        guiManager.setGameSceneController(this);

        objectCards = new HashMap<>();
        commonGoalCards = new HashMap<>();
        personalGoalCards = new HashMap<>();
        commonGoalPoints = new HashMap<>();
        shelvesGridPane = new ArrayList<>();
        shelvesStackPane = List.of(shelfStackPane2, shelfStackPane3, shelfStackPane4);
        orderLimboObjectCards = new ArrayList<>();
        imageViewsWithListener = new HashSet<>();

        limboHBoxArea.setVisible(true);
        arrowShelf0.setMouseTransparent(true);
        arrowShelf1.setMouseTransparent(true);
        arrowShelf2.setMouseTransparent(true);
        arrowShelf3.setMouseTransparent(true);
        arrowShelf4.setMouseTransparent(true);

        loadObjectCards();
        loadCommonGoalCards();
        loadPersonalGoalCards();
        loadCommonGoalPoints();
    }

    /**
     * Setups the board and binds all the events
     *
     * @param gameSerialized state of the game at the time of the join
     */
    void setupGame(GameSerialized gameSerialized) {
        bindChooseColumnArrows();

        setShelves(gameSerialized);
        setPersonalGoalCard(gameSerialized);
        setCommonGoalCards(gameSerialized);
//        setInfoLabel(gameSerialized);
        setPlayerInfo(gameSerialized);
        updateGameArea(gameSerialized);
    }

    /**
     * Binds the arrow buttons to the corresponding event handlers that invoke the onChooseColumnButtonClick method.
     */
    private void bindChooseColumnArrows() {
        arrowShelf0.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onChooseColumnButtonClick(0));
        arrowShelf1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onChooseColumnButtonClick(1));
        arrowShelf2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onChooseColumnButtonClick(2));
        arrowShelf3.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onChooseColumnButtonClick(3));
        arrowShelf4.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onChooseColumnButtonClick(4));
    }

    /**
     * Updates the elements of the board
     */
    void onStateUpdate() {
        updateGameArea(guiManager.getGameSerialized());
    }

    /**
     * Updates element on the game area
     *
     * @param gameSerialized game update
     */
    private void updateGameArea(GameSerialized gameSerialized) {
        updateBoard(gameSerialized);
        updateShelves(gameSerialized);
        updateLimbo(gameSerialized);
        updateCommonGoalCards(gameSerialized);
        updatePlayersInfo(gameSerialized);
    }

    /**
     * Updates element on the board
     * @param gameSerialized game update
     */
    private void updateBoard(GameSerialized gameSerialized) {
        ObservableList<Node> children = boardGridPane.getChildren();
        children.clear();
        setBoard(gameSerialized);
    }

    /**
     * Updates element on the shelves
     * @param gameSerialized game update
     */
    private void updateShelves(GameSerialized gameSerialized) {
        for (GridPane shelfGrid : shelvesGridPane) {
            ObservableList<Node> children = shelfGrid.getChildren();
            children.clear();
            String shelfOwner = (String) shelfGrid.getProperties().get(USERNAME_PROPERTY);
            updateShelfGrid(gameSerialized, shelfGrid, shelfOwner);
        }
    }

    /**
     * Updates the list of object cards selected
     * @param gameSerialized game update
     */
    private void updateLimbo(GameSerialized gameSerialized) {
        setLimbo(gameSerialized);
    }

    /**
     * Updates the common goal cards and their points
     * @param gameSerialized game update
     */
    private void updateCommonGoalCards(GameSerialized gameSerialized) {
        setCommonGoalCards(gameSerialized);
    }

    /**
     * Updates players' info and their scores
     * @param gameSerialized game update
     */
    private void updatePlayersInfo(GameSerialized gameSerialized) {
        setPlayerInfo(gameSerialized);
    }

    /**
     * Loads the images for object cards and adds them to the objectCards map through the addObjectCardImagesToMap method
     */
    private void loadObjectCards() {
        List<String> types = Arrays.stream(ObjectCardType.values())
                .map(Enum::name)
                .toList();

        for (int i = 0; i < ObjectCardType.SIZE; i++) {
            addObjectCardImagesToMap(types.get(i), "0", 7);
            addObjectCardImagesToMap(types.get(i), "1", 7);
            addObjectCardImagesToMap(types.get(i), "2", 8);
        }
    }

    /**
     * Adds the specified number of object card images to the objectCards map.
     *
     * @param type  the type of the object card
     * @param ID    the ID of the object card
     * @param count the number of images to add
     */
    private void addObjectCardImagesToMap(String type, String ID, int count) {
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView();
            String id = Character.toUpperCase(type.charAt(0)) + type.substring(1) + "-" + ID + i;
            imageView.getStyleClass().add(CSS_BUTTON);
            imageView.setId(id);
            objectCards.put(imageView.getId(), imageView);
        }
    }

    /**
     * Loads the common goal cards and adds them to the commonGoalCards map.
     */
    private void loadCommonGoalCards() {
        for (int i = 1; i <= 12; i++) {
            ImageView imageView = new ImageView();
            String id = "commonGoalCard-" + i;
            imageView.getStyleClass().add(CSS_BUTTON);
            imageView.setId(id);
            commonGoalCards.put(imageView.getId(), imageView);
        }
    }

    /**
     * Loads the personal goal cards and adds them to the personalGoalCards map.
     */
    private void loadPersonalGoalCards() {
        for (int i = 1; i <= 12; i++) {
            ImageView imageView = new ImageView();
            String id = "personalGoalCard-" + i;
            imageView.getStyleClass().add(CSS_BUTTON);
            imageView.setId(id);
            personalGoalCards.put(imageView.getId(), imageView);
        }
    }

    /**
     * Loads the common goal points images and adds them to the commonGoalPoints map.
     */
    private void loadCommonGoalPoints() {
        int[] points = {2, 4, 6, 8};
        for (int i = 0; i < 4; i++) {
            ImageView imageView1 = new ImageView();
            String id1 = "scoring-" + points[i] + "0";
            imageView1.setId(id1);
            commonGoalPoints.put(imageView1.getId(), imageView1);

            ImageView imageView2 = new ImageView();
            String id2 = "scoring-" + points[i] + "1";
            imageView2.setId(id2);
            commonGoalPoints.put(imageView2.getId(), imageView2);
        }
    }

    /**
     * Set and update the board grid with the new state of the game
     *
     * @param gameSerialized state of the game at the time of the join
     */
    private void setBoard(GameSerialized gameSerialized) {
        Board board = gameSerialized.getBoard();
        int numRows = 9;
        int numCols = 9;

        ObjectCard objectCard;
        int[][] boardMatrix = gameSerialized.getBoardMatrix();

        for (int i = 0; i < boardMatrix.length / 2; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                if (boardMatrix[i][j] == 1) {
                    objectCard = board.getGrid().get(new Coordinate(4 - i, j - 4));
                    if (objectCard != null) {
                        String cardTypeText = objectCard.getType().getText();
                        String cardNameType = cardTypeText + "-" + objectCard.getId();

                        ImageView imageView = objectCards.get(cardNameType);
                        if (imageView != null) {
                            imageView.setFitWidth(BOARD_OBJECT_CARD_WIDTH);
                            imageView.setFitHeight(BOARD_OBJECT_CARD_HEIGHT);
                            imageView.setPreserveRatio(true);
                            imageView.setPickOnBounds(true);

                            int row = numRows / 2 - (4 - i);
                            int col = (j - 4) + numCols / 2;
                            boardGridPane.add(imageView, col, row);
                            int finalI = i;
                            int finalJ = j;

                            if (!imageViewsWithListener.contains(imageView)) {
                                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onObjectCardClick(4 - finalI, finalJ - 4));
                                imageViewsWithListener.add(imageView);
                            }
                        }
                    }
                } else if (boardMatrix[i][j] == 0) {
                    ImageView transparentImageView = new ImageView();
                    transparentImageView.setFitWidth(BOARD_OBJECT_CARD_WIDTH);
                    transparentImageView.setFitHeight(BOARD_OBJECT_CARD_HEIGHT);
                    transparentImageView.setPreserveRatio(true);
                    transparentImageView.setPickOnBounds(true);
                    transparentImageView.setId(TRANSPARENT_IMAGEVIEW_ID);

                    int row = numRows / 2 - (4 - i);
                    int col = (j - 4) + numCols / 2;
                    boardGridPane.add(transparentImageView, col, row);
                }
            }
        }

        for (int i = boardMatrix.length / 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                if (boardMatrix[i][j] == 1) {
                    objectCard = board.getGrid().get(new Coordinate(4 - i, j - 4));
                    if (objectCard != null) {
                        String cardTypeText = objectCard.getType().getText();
                        String cardNameType = cardTypeText + "-" + objectCard.getId();


                        ImageView imageView = objectCards.get(cardNameType);
                        if (imageView != null) {
                            imageView.setFitWidth(BOARD_OBJECT_CARD_WIDTH);
                            imageView.setFitHeight(BOARD_OBJECT_CARD_HEIGHT);
                            imageView.setPreserveRatio(true);
                            imageView.setPickOnBounds(true);

                            int row = numRows / 2 - (4 - i);
                            int col = (j - 4) + numCols / 2;
                            boardGridPane.add(imageView, col, row);
                            int finalI = i;
                            int finalJ = j;

                            if (!imageViewsWithListener.contains(imageView)) {
                                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onObjectCardClick(4 - finalI, finalJ - 4));
                                imageViewsWithListener.add(imageView);
                            }
                        }
                    }
                } else if (boardMatrix[i][j] == 0) {
                    ImageView transparentImageView = new ImageView();
                    transparentImageView.setFitWidth(BOARD_OBJECT_CARD_WIDTH);
                    transparentImageView.setFitHeight(BOARD_OBJECT_CARD_HEIGHT);
                    transparentImageView.setPreserveRatio(true);
                    transparentImageView.setPickOnBounds(true);
                    transparentImageView.setId(TRANSPARENT_IMAGEVIEW_ID);

                    int row = numRows / 2 - (4 - i);
                    int col = (j - 4) + numCols / 2;
                    boardGridPane.add(transparentImageView, col, row);
                }
            }
        }
    }

    /**
     * Set shelves of the players
     *
     * @param gameSerialized state of the game at the time of the join
     */
    void setShelves(GameSerialized gameSerialized) {
        int i = 0;
        List<Player> players = gameSerialized.getAllPlayers();
        String myName = guiManager.getUsername();

        ImageView myShelf = new ImageView(SHELF_PATH);
        myShelf.setId("myShelfImageView");
        myShelf.setFitWidth(SHELF_WIDTH);
        myShelf.setFitHeight(SHELF_HEIGHT);
        myShelf.getStyleClass().add(CSS_SHELF);
        myShelf.setPreserveRatio(true);
        myShelf.setPickOnBounds(true);
        StackPane.setAlignment(myShelf, Pos.CENTER);
        myStackPane.getChildren().add(myShelf);

        GridPane myShelfGridPane = new GridPane();
        myShelfGridPane.setId("myShelfGridPane");
        myShelfGridPane.setHgap(SHELF_GRIDPANE_HGAP);
        myShelfGridPane.setVgap(SHELF_GRIDPANE_VGAP);
        myShelfGridPane.setMaxHeight(SHELF_GRIDPANE_MAXHEIGHT);
        myShelfGridPane.setMaxWidth(SHELF_GRIDPANE_MAXWIDTH);
        myShelfGridPane.setTranslateY(SHELF_GRIDPANE_TRANSLATE_Y);
        myShelfGridPane.getStyleClass().add(CSS_SHELF_GRIDPANE);
        myShelfGridPane.getProperties().put(USERNAME_PROPERTY, myName);
        myStackPane.getChildren().add(myShelfGridPane);
        myShelfGridPane.toFront();

        Label myNameLabel = new Label(myName);
        myNameLabel.setId("myNameLabel");
        myNameLabel.getStyleClass().add(CSS_SHELF_LABEL);
        StackPane.setAlignment(myNameLabel, Pos.BOTTOM_CENTER);
        myStackPane.getChildren().add(myNameLabel);
        myNameLabel.toFront();

        shelvesGridPane.add(myShelfGridPane);

        for (Player player : players) {
            if (!player.getName().equals(myName)) {
                StackPane playerStackPane = shelvesStackPane.get(i);

                ImageView imageView = new ImageView(SHELF_PATH);
                imageView.setId("shelfImageView" + i);
                imageView.setFitWidth(SHELF_WIDTH);
                imageView.setFitHeight(SHELF_HEIGHT);
                imageView.getStyleClass().add(CSS_SHELF);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);
                StackPane.setAlignment(imageView, Pos.CENTER);
                playerStackPane.getChildren().add(imageView);

                GridPane gridPane = new GridPane();
                gridPane.setId("shelfGridPane" + i);
                gridPane.setHgap(SHELF_GRIDPANE_HGAP);
                gridPane.setVgap(SHELF_GRIDPANE_VGAP);
                gridPane.setMaxHeight(SHELF_GRIDPANE_MAXHEIGHT);
                gridPane.setMaxWidth(SHELF_GRIDPANE_MAXWIDTH);
                gridPane.setTranslateY(SHELF_GRIDPANE_TRANSLATE_Y);
                gridPane.getStyleClass().add(CSS_SHELF_GRIDPANE);
                gridPane.getProperties().put(USERNAME_PROPERTY, player.getName());
                playerStackPane.getChildren().add(gridPane);
                gridPane.toFront();

                Label playerNameLabel = new Label(player.getName());
                playerNameLabel.setId("playerNameLabel" + i);
                playerNameLabel.getStyleClass().add(CSS_SHELF_LABEL);
                StackPane.setAlignment(playerNameLabel, Pos.BOTTOM_CENTER);
                playerStackPane.getChildren().add(playerNameLabel);
                playerNameLabel.toFront();

                shelvesGridPane.add(gridPane);

                i++;
            }
        }
    }

    /**
     * Updates the shelf grid with the new state of the game
     *
     * @param gameSerialized state of the game at the time of the join
     * @param shelfGrid shelfGrid grid to update
     * @param shelfOwner owner of the shelf
     */
    private void updateShelfGrid(GameSerialized gameSerialized, GridPane shelfGrid, String shelfOwner) {
        List<Player> players = gameSerialized.getAllPlayers();
        Shelf onWorkShelf = players.stream()
                .filter(player -> player.getName().equals(shelfOwner))
                .map(Player::getShelf)
                .findFirst()
                .orElse(null);

        for (int row = Shelf.ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < Shelf.COLUMNS; col++) {
                Coordinate coord = new Coordinate(row, col);
                ObjectCard objectCard = onWorkShelf.getObjectCard(coord);
                if (objectCard == null) {
                    ImageView transparentImageView = new ImageView();
                    transparentImageView.setFitWidth(SHELF_OBJECT_CARD_WIDTH);
                    transparentImageView.setFitHeight(SHELF_OBJECT_CARD_HEIGHT);
                    transparentImageView.setPreserveRatio(true);
                    transparentImageView.setPickOnBounds(true);
                    transparentImageView.setId(TRANSPARENT_IMAGEVIEW_ID);

                    shelfGrid.add(transparentImageView, col, Shelf.ROWS - 1 - row);
                } else {
                    String cardTypeText = objectCard.getType().getText();
                    String cardNameType = cardTypeText + "-" + objectCard.getId();

                    ImageView imageView = objectCards.get(cardNameType);
                    if (imageView != null) {
                        imageView.setFitWidth(SHELF_OBJECT_CARD_WIDTH);
                        imageView.setFitHeight(SHELF_OBJECT_CARD_HEIGHT);
                        imageView.setPreserveRatio(true);
                        imageView.setPickOnBounds(true);
                        imageView.setMouseTransparent(true);

                        shelfGrid.add(imageView, col, Shelf.ROWS - 1 - row);
                    }
                }
            }
        }
    }

    /**
     * Set and update common goals and their points with the new state of the game
     *
     * @param gameSerialized state of the game at the time of the join
     */
    private void setCommonGoalCards(GameSerialized gameSerialized) {
        List<CommonGoal> commonGoals = gameSerialized.getCommonGoals();
        commonGoalCard1StackPane.getChildren().clear();
        commonGoalCard2StackPane.getChildren().clear();

        for (int i = 0; i < commonGoals.size(); i++) {
            String cardTypeText = commonGoals.get(i).toString();
            ImageView imageView = commonGoalCards.get(cardTypeText);

            if (imageView != null) {
                imageView.setFitWidth(COMMONGOAL_CARD_WIDTH);
                imageView.setFitHeight(COMMONGOAL_CARD_HEIGHT);
                imageView.setPreserveRatio(true);
                imageView.setPickOnBounds(true);

                if (i == 0) {
                    commonGoalCard1StackPane.getChildren().add(imageView);
                    imageView.setMouseTransparent(true);
                } else if (i == 1) {
                    commonGoalCard2StackPane.getChildren().add(imageView);
                    imageView.setMouseTransparent(true);
                }
            }

            int commonGoalCurrentPointsInt = commonGoals.get(i).getCurrentPoints();
            String commonGoalCurrentPointsString;
            if (commonGoalCurrentPointsInt == 0) {
                commonGoalCurrentPointsString = "scoring-0" + i;
            } else {
                commonGoalCurrentPointsString = "scoring-" + commonGoalCurrentPointsInt + i;
            }
            ImageView pointsImageView = commonGoalPoints.get(commonGoalCurrentPointsString);

            if (pointsImageView != null) {
                pointsImageView.setFitWidth(COMMONGOAL_POINTS_WIDTH);
                pointsImageView.setFitHeight(COMMONGOAL_POINTS_HEIGHT);
                pointsImageView.setRotate(COMMONGOAL_POINTS_ROTATE);
                pointsImageView.setTranslateX(COMMONGOAL_POINTS_TRANSLATE_X);
                pointsImageView.setTranslateY(COMMONGOAL_POINTS_TRANSLATE_Y);
                pointsImageView.setPreserveRatio(true);

                if (i == 0) {
                    commonGoalCard1StackPane.getChildren().add(pointsImageView);
                    pointsImageView.toFront();
                } else if (i == 1) {
                    commonGoalCard2StackPane.getChildren().add(pointsImageView);
                    pointsImageView.toFront();
                }
            }
        }
    }

    /**
     * Set personal goal
     *
     * @param gameSerialized state of the game at the time of the join
     */
    private void setPersonalGoalCard(GameSerialized gameSerialized) {
        PersonalGoalCard personalGoalCard = gameSerialized.getPersonalGoalCard();
        String cardTypeText = personalGoalCard.getID();
        ImageView imageView = personalGoalCards.get(cardTypeText);

        if (imageView != null) {
            imageView.setFitWidth(PERSONALGOAL_CARD_WIDTH);
            imageView.setFitHeight(PERSONALGOAL_CARD_HEIGHT);
            imageView.setTranslateY(PERSONALGOAL_CARD_TRANSLATE_Y);
            imageView.setPreserveRatio(true);
            imageView.setPickOnBounds(true);
            StackPane.setAlignment(imageView, Pos.CENTER);
            imageView.setMouseTransparent(true);

            personalGoalCardPane.getChildren().add(imageView);
        }
    }

    /**
     * Set and update the list of chosen object cards with the new state of the game
     *
     * @param gameSerialized state of the game at the time of the join
     */
    void setLimbo(GameSerialized gameSerialized) {
        List<ObjectCard> limboCards = gameSerialized.getAllLimboCards();
        orderLimboObjectCards.clear();
        limboHBoxArea.getChildren().clear();

        if (limboCards.isEmpty()) {
            ImageView imageView = new ImageView(MYSHELFIE_LOGO_PATH);
            imageView.setFitHeight(MYSHELFIE_LOGO_HEIGHT);
            imageView.setFitWidth(MYSHELFIE_LOGO_WIDTH);
            imageView.setTranslateX(MYSHELFIE_LOGO_TRANSLATE_X);
            imageView.setId("MyShelfieLogo");
            imageView.setPreserveRatio(true);

            limboHBoxArea.getStyleClass().clear();
            limboHBoxArea.getChildren().add(imageView);
        } else {
            limboHBoxArea.getStyleClass().add(CSS_LIMBO_HBOX);

            if (!limboCards.isEmpty()) {
                for (int i = 0; i < limboCards.size(); i++) {
                    ObjectCard objectCard = limboCards.get(i);
                    if (objectCard != null) {
                        String cardTypeText = objectCard.getType().getText();
                        String cardNameType = cardTypeText + "-" + objectCard.getId();
                        ImageView imageView = objectCards.get(cardNameType);

                        if (imageView != null) {
                            ImageView imageViewCopy = new ImageView(imageView.getImage());
                            imageViewCopy.getStyleClass().addAll(imageView.getStyleClass());

                            imageViewCopy.setFitWidth(LIMBO_OBJECT_CARD_WIDTH);
                            imageViewCopy.setFitHeight(LIMBO_OBJECT_CARD_HEIGHT);
                            imageViewCopy.setPreserveRatio(true);
                            imageViewCopy.setPickOnBounds(true);

                            limboHBoxArea.getChildren().add(imageViewCopy);

                            int finalI = i;
                            imageViewCopy.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onObjectCardInLimboClick(finalI, limboCards));
                        }
                    }
                }
            }
        }
    }

    /**
     * Set and update the player and points info with the new state of the game
     *
     * @param gameSerialized state of the game at the time of the join
     */
    void setPlayerInfo(GameSerialized gameSerialized) {
        playersInfoVBox.getChildren().clear();

        int i = 0;
        List<Player> players = gameSerialized.getAllPlayers();
        String myName = guiManager.getUsername();
        String myPoints = POINTS_PROPERTY + gameSerialized.getPoints();

        Separator upSeparator = new Separator();
        upSeparator.getStyleClass().add(CSS_PLAYERINFO_SEPARATOR);
        playersInfoVBox.getChildren().add(upSeparator);

        Label myNameLabel;
        if (myName.equals(gameSerialized.getCurrentPlayer().getName())) {
            myNameLabel = new Label(myName + " ★");
        } else {
            myNameLabel = new Label(myName);
        }
        myNameLabel.setId("myNameLabel");
        myNameLabel.getStyleClass().add(CSS_PLAYERINFO_LABEL);
        playersInfoVBox.getChildren().add(myNameLabel);

        Label myPointsLabel = new Label(myPoints);
        myPointsLabel.setId("myPointsLabel");
        myPointsLabel.getStyleClass().add(CSS_PLAYERINFO_LABEL);
        playersInfoVBox.getChildren().add(myPointsLabel);

        Separator downSeparator = new Separator();
        downSeparator.getStyleClass().add(CSS_PLAYERINFO_SEPARATOR);
        playersInfoVBox.getChildren().add(downSeparator);

        for (Player player : players) {
            if (!player.getName().equals(myName)) {
                String playerName = player.getName();
                String playerPoints = POINTS_PROPERTY + player.getCurrentPoints();

                Label playerNameLabel;
                if (playerName.equals(gameSerialized.getCurrentPlayer().getName())) {
                    playerNameLabel = new Label(playerName + " ★");
                } else {
                    playerNameLabel = new Label(playerName);
                }
                playerNameLabel.setId("playerNameLabel" + i);
                playerNameLabel.getStyleClass().add(CSS_PLAYERINFO_LABEL);
                playersInfoVBox.getChildren().add(playerNameLabel);

                Label playerPointsLabel = new Label(playerPoints);
                playerPointsLabel.setId("playerPointsLabel" + i);
                playerPointsLabel.getStyleClass().add(CSS_PLAYERINFO_LABEL);
                playersInfoVBox.getChildren().add(playerPointsLabel);

                Separator separator2 = new Separator();
                separator2.getStyleClass().add(CSS_PLAYERINFO_SEPARATOR);
                playersInfoVBox.getChildren().add(separator2);

                i++;
            }
        }
    }

    /**
     * Manages what the player can click during his turn
     *
     * @param possibleActions possible actions
     */
    void displayAction(List<PossibleAction> possibleActions) {
        boolean isBoardPickCardActionPresent = false;
        boolean isLoadShelfActionPresent = false;
        boolean isReorderLimboActionPresent = false;

        for (PossibleAction possibleAction : possibleActions) {
            String actionID = getActionIDFromPossibleAction(possibleAction);

            switch (actionID) {
                case "boardPickCard" -> {
                    setObjectsCardAvailability(false);
                    isBoardPickCardActionPresent = true;
                }
                case "loadShelf" -> {
                    setShelfArrowsAvailability(false);
                    isLoadShelfActionPresent = true;
                }
                case "reorderLimbo" -> {
                    setLimboAvailability(false);
                    isReorderLimboActionPresent = true;
                }
                default -> {
                }
            }
        }

        if (!isBoardPickCardActionPresent) {
            setObjectsCardAvailability(true);
        }

        if (!isLoadShelfActionPresent) {
            setShelfArrowsAvailability(true);
        }

        if (!isReorderLimboActionPresent) {
            setLimboAvailability(true);
        }

        actionListStackPane.getChildren().clear();
        for (PossibleAction possibleAction : possibleActions) {
            if (possibleAction.equals(PossibleAction.DELETE_LIMBO)) {
                ImageView imageView = new ImageView();
                imageView.setId(getActionIDFromPossibleAction(possibleAction));
                imageView.setFitHeight(ACTION_BUTTON_HEIGHT);
                imageView.setFitWidth(ACTION_BUTTON_WIDTH);
                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> guiManager.doAction(possibleAction));
                imageView.getStyleClass().add(CSS_BUTTON);

                actionListStackPane.getChildren().add(imageView);
            }
        }
    }

    /**
     * Returns the CSS ID of the action based on the PossibleAction
     *
     * @param possibleAction possible action passed
     * @return the CSS ID
     */
    private String getActionIDFromPossibleAction(PossibleAction possibleAction) {
        switch (possibleAction) {
            case JOIN_GAME:
            case CREATE_GAME:
            case BOARD_PICK_CARD:
                return "boardPickCard";
            case LOAD_SHELF:
                return "loadShelf";
            case REORDER_LIMBO:
                return "reorderLimbo";
            case DELETE_LIMBO:
                return "deleteLimbo";
            default:
                return null;
        }
    }

    /**
     * Sets the availability of object cards on the board.
     *
     * @param isAvailable A boolean indicating whether the object cards should be available or not.
     */
    private void setObjectsCardAvailability(boolean isAvailable) {
        boardGridPane.setMouseTransparent(isAvailable);
    }

    /**
     * Sets the availability of the shelf arrows.
     *
     * @param isAvailable A boolean indicating whether the shelf arrows should be available or not.
     */
    private void setShelfArrowsAvailability(boolean isAvailable) {
        arrowShelf0.setMouseTransparent(isAvailable);
        arrowShelf1.setMouseTransparent(isAvailable);
        arrowShelf2.setMouseTransparent(isAvailable);
        arrowShelf3.setMouseTransparent(isAvailable);
        arrowShelf4.setMouseTransparent(isAvailable);
    }

    /**
     * Sets the availability of the limbo area.
     *
     * @param isAvailable A boolean indicating whether the limbo area should be available or not.
     */
    private void setLimboAvailability(boolean isAvailable) {
        limboHBoxArea.setMouseTransparent(isAvailable);
    }

    /**
     * Handles the game scene when it is not the player's turn
     */
    void notYourTurn() {
        actionListStackPane.getChildren().clear();
        arrowShelf0.setMouseTransparent(true);
        arrowShelf1.setMouseTransparent(true);
        arrowShelf2.setMouseTransparent(true);
        arrowShelf3.setMouseTransparent(true);
        arrowShelf4.setMouseTransparent(true);
        boardGridPane.setMouseTransparent(true);
        myStackPane.setMouseTransparent(true);
        shelfStackPane2.setMouseTransparent(true);
        shelfStackPane3.setMouseTransparent(true);
        shelfStackPane4.setMouseTransparent(true);
        limboHBoxArea.setMouseTransparent(true);
    }

    /**
     * Handles the event when an object card is clicked on the board.
     *
     * @param row The row index of the clicked card.
     * @param col The column index of the clicked card.
     */
    private void onObjectCardClick(int row, int col) {
        Coordinate coordinate = new Coordinate(row, col);

        if (!guiManager.sendRequest(MessageBuilder.buildPickObjectCardRequest(guiManager.getPlayer(), guiManager.getClientToken(), coordinate))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the object card button click in the list of object cards selected
     * @param index index of the object card in the list of object cards selected
     * @param limboCards list of object cards selected
     */
    private void onObjectCardInLimboClick(int index, List<ObjectCard> limboCards) {
        orderLimboObjectCards.add(index);

        if (orderLimboObjectCards.size() == limboCards.size()) {
            onReorderLimboRequest();
            orderLimboObjectCards.clear();
        }
    }

    /**
     * Handles the arrow button click for the choice of shelf column
     * @param column column selected of the shelf
     */
    private void onChooseColumnButtonClick(int column) {
        if (!guiManager.sendRequest(MessageBuilder.buildLoadShelfRequest(guiManager.getClientToken(), guiManager.getUsername(), column))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the reorder limbo request.
     */
    private void onReorderLimboRequest() {
        if (!guiManager.sendRequest(MessageBuilder.buildReorderLimboRequest(guiManager.getUsername(), guiManager.getClientToken(), orderLimboObjectCards))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);
        }
    }

    /**
     * Handles the delete limbo request.
     */
    void onDeleteLimboRequest() {
        if (!guiManager.sendRequest(MessageBuilder.buildDeleteLimboRequest(guiManager.getUsername(), guiManager.getClientToken()))) {
            GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE,
                    GuiManager.SEND_ERROR);
        }
    }

    /**
     * Communicates the disconnection of a player
     *
     * @param player username of a player who disconnected
     */
    void onPlayerDisconnection(String player) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", player + " disconnected from the server");
    }

    /**
     * Communicates the reconnection of a player
     * @param message message to be displayed
     */
    void onPlayerReconnection(String message) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Reconnection", message);
    }

    /**
     * Handles the disconnection
     */
    void onDisconnection() {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), "Disconnection", "You were disconnected from the server");
    }

    /**
     * Handles the game end
     *
     * @param players player of the leaderboard
     */
    void onGameEnd(List<Player> players) {
        EndGameSceneController endGameSceneController = GuiManager.setLayout(mainPane.getScene(), "fxml/endGameScene.fxml");

        if (endGameSceneController != null) {
            endGameSceneController.setData(players);
        }
    }

    /**
     * Called when an error occurs. Displays an alert with the error message
     *
     * @param error message of the error
     */
    void onError(String error) {
        GuiManager.showDialog((Stage) mainPane.getScene().getWindow(), GuiManager.ERROR_DIALOG_TITLE, error);
    }
}