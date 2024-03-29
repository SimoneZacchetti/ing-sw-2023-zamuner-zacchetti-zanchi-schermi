package it.polimi.ingsw.control;

import it.polimi.ingsw.enumeration.MessageStatus;
import it.polimi.ingsw.enumeration.PossibleGameState;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.message.*;
import it.polimi.ingsw.network.server.Server;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControllerGameTest extends TestCase {
    private ControllerGame cg;
    private PersonalGoalCard pg;
    private Shelf shelf;

    private Server server;

    @BeforeEach
    public void setUp() {
        server = new Server("GameConstant.json");
        cg = new ControllerGame(server);
        cg.setGame(new Game());
        cg.getGame().setNumberOfPlayers(2);
        //cg.fillBoard();

    }

    @Test
    public void testGetId() {
        assertTrue(cg.getId() instanceof UUID);
    }

    @Test
    void testGetKeysAsArrayList() {
        Map<Coordinate, ObjectCard> map = new HashMap<>();
        map.put(new Coordinate(1, 1), new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        map.put(new Coordinate(2, 2), new ObjectCard(ObjectCardType.randomObjectCardType(), "01"));
        map.put(new Coordinate(3, 3), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));

        ArrayList<Coordinate> keys = cg.getKeysAsArrayList(map);

        assertEquals(3, keys.size());

        assertTrue(keys.contains(new Coordinate(1, 1)));
        assertTrue(keys.contains(new Coordinate(2, 2)));
        assertTrue(keys.contains(new Coordinate(3, 3)));
    }

    @Test
    public void testOnMessageGameEnded() {
        cg.setGameState(PossibleGameState.GAME_ENDED);
        Response response = (Response) cg.onMessage(
                new ObjectCardRequest("federica", null, null));
        assertEquals(MessageStatus.ERROR, response.getStatus());
    }

    @Test
    public void testOnMessage() {
        Response response = (Response) cg.onMessage(
                new ObjectCardRequest("federica", null, null));
        assertEquals(MessageStatus.ERROR, response.getStatus());
        response = (Response) cg.onMessage(
                new ReorderLimboRequest("federica", null, null));
        assertEquals(MessageStatus.ERROR, response.getStatus());
    }

    @Test
    public void testGameSetupHandler() {
        cg.getGame().setNumberOfPlayers(4);
        assertFalse(cg.getIsLobbyFull());

        cg.gameSetupHandler();

        assertFalse(cg.getIsLobbyFull());
    }

    @Test
    public void testGameSetupHandler2() {
        cg.getGame().setNumberOfPlayers(2);
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));

        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        cg.gameSetupHandler();

        assertTrue(cg.getIsLobbyFull());
    }

    @Test
    public void testUsernameNull() {
        assertThrows(NullPointerException.class, () -> {
            cg.isUsernameAvailable(null);
        });
    }

    @Test
    void testLoadShelfHandler() {
        LoadShelfRequest request = new LoadShelfRequest("Pescheria", null, 1);

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Pescheria", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().addPlayer(currentPlayer);
        Game.getInstanceMap().put("Pescheria", cg.getGame());
        cg.getGame().setCurrentPlayer(currentPlayer);

        LinkedHashMap<Coordinate, ObjectCard> limbo = new LinkedHashMap<>();
        limbo.put(new Coordinate(-3, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-3, 1), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-2, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        cg.getGame().setLimbo(limbo);

        cg.setMakeMoveTimer();
        Response response = cg.loadShelfHandler(request);

        assertEquals("Cards moved", response.getMessage());
        assertEquals(MessageStatus.OK, response.getStatus());
        assertEquals(0, cg.getGame().getLimbo().size());
    }

    @Test
    public void testLoadShelfHandler2() {
        LoadShelfRequest request = new LoadShelfRequest("Armando", null, 1);

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Armando", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().setCurrentPlayer(currentPlayer);
        currentPlayer.getShelf().setFull(true);
        assertEquals(new Response("Game has ended.", MessageStatus.GAME_ENDED).getStatus(), cg.loadShelfHandler(request).getStatus());
    }


    @Test
    public void testRefillBoardEmptyBoard() {
        cg.getGame().setNumberOfPlayers(2);
        cg.refillBoard();
        assertEquals(29, cg.getGame().getBoard().getGrid().size());
    }

    @Test
    public void testRefillBoardFewTilesLeft() {
        cg.getGame().setNumberOfPlayers(2);
        cg.getGame().getBoard().getGrid().put(new Coordinate(3, -1), new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getBoard().getGrid().put(new Coordinate(-3, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.refillBoard();
        assertEquals(29, cg.getGame().getBoard().getGrid().size());
    }

    @Test
    void testDeleteLimboHandler() {
        DeleteLimboRequest request = new DeleteLimboRequest("Mollica", null);

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Mollica", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().addPlayer(currentPlayer);
        Game.getInstanceMap().put("Mollica", cg.getGame());
        cg.getGame().setCurrentPlayer(currentPlayer);

        LinkedHashMap<Coordinate, ObjectCard> limbo = new LinkedHashMap<>();
        limbo.put(new Coordinate(-3, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-3, 1), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-2, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        cg.getGame().setLimbo(limbo);

        Response response = cg.deleteLimboHandler(request);

        assertEquals("Limbo deleted", response.getMessage());
        assertEquals(MessageStatus.OK, response.getStatus());
        assertEquals(0, cg.getGame().getLimbo().size());
    }

    @Test
    public void testReorderLimboHandler() {
        ArrayList newLimbo = new ArrayList();
        newLimbo.add(3);
        newLimbo.add(2);
        newLimbo.add(1);
        ReorderLimboRequest request = new ReorderLimboRequest("Number1", null, newLimbo);

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Number1", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().addPlayer(currentPlayer);
        Game.getInstanceMap().put("Number1", cg.getGame());
        cg.getGame().setCurrentPlayer(currentPlayer);

        LinkedHashMap<Coordinate, ObjectCard> limbo = new LinkedHashMap<>();
        limbo.put(new Coordinate(-3, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-3, 1), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        limbo.put(new Coordinate(-2, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "02"));
        cg.getGame().setLimbo(limbo);

        Response response = cg.reorderLimboHandler(request);

        assertEquals("Limbo reordered", response.getMessage());
        assertEquals(MessageStatus.PRINT_LIMBO, response.getStatus());
    }

    @Test
    public void testPickObjectCardHandlerValidCard() {
        cg.fillBoard();
        ObjectCardRequest request = new ObjectCardRequest("Billy", null, new Coordinate(-3, 0));

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Billy", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().addPlayer(currentPlayer);
        cg.getGame().setCurrentPlayer(currentPlayer);
        Game.getInstanceMap().put("Billy", cg.getGame());

        Response response = cg.pickObjectCardHandler(request);

        assertEquals("Valid card :)", response.getMessage());
        assertEquals(MessageStatus.PRINT_LIMBO, response.getStatus());
    }

    @Test
    public void testPickObjectCardHandlerInvalidCard() {
        cg.fillBoard();
        ObjectCardRequest request = new ObjectCardRequest("Billy", null, new Coordinate(0, 0));

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Player currentPlayer = new Player("Billy", new Shelf(), new PersonalGoalCard(goals, "1"));
        cg.getGame().addPlayer(currentPlayer);
        cg.getGame().setCurrentPlayer(currentPlayer);
        Game.getInstanceMap().put("Billy", cg.getGame());

        Response response = cg.pickObjectCardHandler(request);

        assertEquals("Invalid card :(", response.getMessage());
        assertEquals(MessageStatus.NOT_VALID_CARD, response.getStatus());
    }


    @Test
    public void testnumberOfPlayersMessageHandler() {
        NumberOfPlayersMessage request = new NumberOfPlayersMessage("Billy", null, 2, "Gioco1");

        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        Response response = cg.numberOfPlayersMessageHandler(request);

        assertEquals("Number of players set", response.getMessage());
        assertEquals(MessageStatus.OK, response.getStatus());
    }

    @Test
    public void testGetGameState() {
        assertEquals(PossibleGameState.GAME_ROOM, cg.getGameState());
    }

    @Test
    public void testCheckLobby() {
        cg.getGame().setNumberOfPlayers(2);
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));

        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        Response response = cg.checkLobby();

        assertEquals(MessageStatus.OK, response.getStatus());
    }

    @Test
    public void testCheckLobby2() {
        cg.getGame().setNumberOfPlayers(1);
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));

        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        Response response = cg.checkLobby();

        assertEquals(MessageStatus.OK, response.getStatus());
    }

    @Test
    public void testIsUsernameAvailable() {
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));

        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        assertFalse(cg.isUsernameAvailable("federica"));
        assertTrue(cg.isUsernameAvailable("giulia"));
    }

    @Test
    public void testCheckIfRefill() {
        cg.getGame().setNumberOfPlayers(2);
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));

        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        cg.getGame().getBoard().getGrid().put(new Coordinate(0, 0), new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));

        assertTrue(cg.checkIfRefill());
    }

    @Test
    public void testCheckIfRefillTrue() {
        cg.getGame().setNumberOfPlayers(2);
        cg.fillBoard();

        assertFalse(cg.checkIfRefill());
    }


    @Test
    public void testLobbyMessageHandler() {
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));
        LobbyMessage request = new LobbyMessage("federica", null, false);

        cg.getGame().addPlayer(new Player("federica", new Shelf(), new PersonalGoalCard(goals, "1")));
        cg.getGame().addPlayer(new Player("matteo", new Shelf(), new PersonalGoalCard(goals, "2")));
        Game.getInstanceMap().put("federica", cg.getGame());
        Game.getInstanceMap().put("matteo", cg.getGame());

        Response response = cg.lobbyMessageHandler(request);

        assertEquals(MessageStatus.ERROR, response.getStatus());
    }

    @Test
    public void testObjectCardAvailableNoLimboCards() {
        cg.fillBoard();
        cg.getGame().setLimbo(new LinkedHashMap<>());
        Coordinate coordinate = new Coordinate(-3, 0);
        boolean result = cg.isObjectCardAvailable(coordinate);
        assertTrue(result);
    }

    @Test
    public void testObjectCardAvailableOneLimboCardCloseToCoordinate() {
        cg.fillBoard();
        cg.getGame().setLimbo(new LinkedHashMap<>());
        Coordinate limboCardCoordinate = new Coordinate(-3, 0);
        cg.getGame().getLimbo().put(limboCardCoordinate, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getBoard().removeObjectCard(limboCardCoordinate);
        Coordinate coordinate = new Coordinate(-3, 1);
        boolean result = cg.isObjectCardAvailable(coordinate);
        assertTrue(result);
    }

    @Test
    public void testObjectCardAvailableOneLimboCardNotCloseToCoordinate() {
        cg.fillBoard();
        cg.getGame().setLimbo(new LinkedHashMap<>());
        Coordinate limboCardCoordinate = new Coordinate(2, 2);
        cg.getGame().getLimbo().put(limboCardCoordinate, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getBoard().removeObjectCard(limboCardCoordinate);
        Coordinate coordinate = new Coordinate(1, 1);
        boolean result = cg.isObjectCardAvailable(coordinate);
        assertFalse(result);
    }

    @Test
    public void testObjectCardAvailableTwoLimboCardsInLineWitchCoordinate() {
        cg.fillBoard();
        cg.getGame().setLimbo(new LinkedHashMap<>());
        cg.getGame().getBoard().removeObjectCard(new Coordinate(-3, 0));
        cg.getGame().getBoard().removeObjectCard(new Coordinate(-3, 1));

        Coordinate limboCard1 = new Coordinate(-2, -1);
        Coordinate limboCard2 = new Coordinate(-2, 0);
        cg.getGame().getLimbo().put(limboCard1, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getBoard().removeObjectCard(limboCard1);
        cg.getGame().getLimbo().put(limboCard2, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getBoard().removeObjectCard(limboCard2);
        Coordinate coordinate = new Coordinate(-2, 1);
        boolean result = cg.isObjectCardAvailable(coordinate);
        assertTrue(result);
    }

    @Test
    public void testObjectCardAvailableTwoLimboCardsNotInLineWithCoordinate() {
        cg.fillBoard();
        cg.getGame().setLimbo(new LinkedHashMap<>());
        cg.getGame().getBoard().removeObjectCard(new Coordinate(-3, 0));
        cg.getGame().getBoard().removeObjectCard(new Coordinate(-3, 1));
        Coordinate limboCard1 = new Coordinate(-2, -1);
        Coordinate limboCard2 = new Coordinate(-2, 0);
        cg.getGame().getLimbo().put(limboCard1, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        cg.getGame().getLimbo().put(limboCard2, new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));
        Coordinate coordinate = new Coordinate(-1, 1);
        boolean result = cg.isObjectCardAvailable(coordinate);
        assertFalse(result);
    }

    @Test
    public void testIsObjectCardAvailableOneDirectionFull() {
        Coordinate c = new Coordinate(0, 3);
        cg.fillBoard();
        assertTrue(cg.isObjectCardAvailable(c));
    }

    @Test
    public void testIsObjectCardAvailableAllDirectiosnFull() {
        Player p = new Player("Becky", this.shelf, this.pg);
        cg.getGame().addPlayer(p);
        Coordinate c = new Coordinate(0, 0);

        cg.fillBoard();
        assertFalse(cg.isObjectCardAvailable(c));
    }


    @Test
    public void testCalculateWinner() {
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        Player player1 = new Player("Player 1", new Shelf(), new PersonalGoalCard(goals, "1"));
        Player player2 = new Player("Player 2", new Shelf(), new PersonalGoalCard(goals, "2"));
        Player player3 = new Player("Player 3", new Shelf(), new PersonalGoalCard(goals, "3"));

        cg.getGame().addPlayer(player1);
        cg.getGame().addPlayer(player2);
        cg.getGame().addPlayer(player3);

        player1.setCurrentPoints(5);
        player2.setCurrentPoints(10);
        player3.setCurrentPoints(15);

        System.out.println(player1.getCurrentPoints());
        System.out.println(player2.getCurrentPoints());
        System.out.println(player3.getCurrentPoints());

        cg.calculateWinner();

        assertTrue(player3.isWinner());
        assertFalse(player2.isWinner());
        assertFalse(player1.isWinner());
    }

    @Test
    public void testReorderList() {
        List<Coordinate> list1 = new ArrayList<>(Arrays.asList(
                new Coordinate(1, 1),
                new Coordinate(2, 2),
                new Coordinate(3, 3),
                new Coordinate(4, 4)
        ));
        List<Integer> list2 = new ArrayList<>(Arrays.asList(2, 0, 3, 1));

        cg.reorderList(list1, list2);

        assertEquals(new Coordinate(3, 3), list1.get(0));

        assertEquals(new Coordinate(1, 1), list1.get(1));

        assertEquals(new Coordinate(4, 4), list1.get(2));

        assertEquals(new Coordinate(2, 2), list1.get(3));
    }

    @Test
    public void testReorderMap() {
        LinkedHashMap<Coordinate, ObjectCard> map = new LinkedHashMap<>();
        map.put(new Coordinate(1, 1), new ObjectCard(ObjectCardType.randomObjectCardType(), "10"));
        map.put(new Coordinate(2, 2), new ObjectCard(ObjectCardType.randomObjectCardType(), "20"));
        map.put(new Coordinate(3, 3), new ObjectCard(ObjectCardType.randomObjectCardType(), "30"));
        map.put(new Coordinate(4, 4), new ObjectCard(ObjectCardType.randomObjectCardType(), "00"));

        List<Coordinate> order = new ArrayList<>(Arrays.asList(
                new Coordinate(3, 3),
                new Coordinate(1, 1),
                new Coordinate(4, 4),
                new Coordinate(2, 2)
        ));
        LinkedHashMap<Coordinate, ObjectCard> orderedMap = cg.reorderMap(map, order);
        assertEquals("30", orderedMap.get(new Coordinate(3, 3)).getId());
        assertEquals("10", orderedMap.get(new Coordinate(1, 1)).getId());
        assertEquals("00", orderedMap.get(new Coordinate(4, 4)).getId());
        assertEquals("20", orderedMap.get(new Coordinate(2, 2)).getId());

    }

    @Test
    public void testPointsCalculatorNoCompletedRows() {
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        this.pg = new PersonalGoalCard(goals, "personalGoalCard-1");
        this.shelf = new Shelf();
        Player p = new Player("Alice", this.shelf, this.pg);
        cg.getGame().addPlayer(p);
        cg.getGame().setCurrentPlayer(p);

        assertEquals(0, cg.pointsCalculator());
    }

    @Test
    public void testPointsCalculatorCommonGoals() {
        ArrayList<PersonalGoal> goals = new ArrayList<>();
        goals.add(new PersonalGoal(1, 1, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(2, 3, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(4, 5, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(5, 2, ObjectCardType.randomObjectCardType()));
        goals.add(new PersonalGoal(3, 6, ObjectCardType.randomObjectCardType()));

        this.pg = new PersonalGoalCard(goals, "personalGoalCard-1");
        this.shelf = new Shelf();

        cg.getGame().getCommonGoals().clear();

        cg.getGame().getCommonGoals().add(new CommonGoalType3());

        Player p = new Player("Wejdene", this.shelf, this.pg);
        cg.getGame().addPlayer(p);
        cg.getGame().setCurrentPlayer(p);

        ObjectCardType type = ObjectCardType.randomObjectCardType();
        ObjectCard oc = new ObjectCard(type, "00");

        cg.getGame().getCurrentPlayer().getShelf().getGrid().put(new Coordinate(0, 0), oc);
        cg.getGame().getCurrentPlayer().getShelf().getGrid().put(new Coordinate(5, 0), oc);
        cg.getGame().getCurrentPlayer().getShelf().getGrid().put(new Coordinate(5, 4), oc);
        cg.getGame().getCurrentPlayer().getShelf().getGrid().put(new Coordinate(0, 4), oc);


        assertEquals(8, cg.pointsCalculator());
        assertEquals(8, cg.pointsCalculator());
    }

    @Test
    void testReconnectionHandler() {
        // Arrange
        LobbyMessage lobbyMessage = new LobbyMessage("messia", "Token", false);
        cg.setGame(Game.getInstance(lobbyMessage.getSenderUsername()));
        Player player = new Player("messia", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.getGame().setStarted(true);

        Message result = cg.reconnectionHandler(lobbyMessage);

        assertNotNull(result);
        assertTrue(result instanceof ReconnectionRequest);
        assertEquals("Reconnection request", ((ReconnectionRequest) result).getMessage());
        assertEquals("Token", ((ReconnectionRequest) result).getToken());
        assertTrue(player.isConnected());
    }

    @Test
    void testReconnectionHandlerGameNotStarted() {
        LobbyMessage lobbyMessage = new LobbyMessage("messia", "Token", false);
        cg.setGame(Game.getInstance(lobbyMessage.getSenderUsername()));
        Player player = new Player("messia", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.getGame().setStarted(false);

        Message result = cg.reconnectionHandler(lobbyMessage);

        assertNotNull(result);
        assertTrue(result instanceof Message);
        assertEquals("Game is ended.", ((Response) result).getMessage());
        assertEquals(MessageStatus.ERROR, ((Response) result).getStatus());
    }

    @Test
    void testReconnectionHandlerPlayerWasConnected() {
        LobbyMessage lobbyMessage = new LobbyMessage("messia", "Token", false);
        cg.setGame(Game.getInstance(lobbyMessage.getSenderUsername()));
        Player player = new Player("messia", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.getGame().setStarted(true);
        cg.setTimer();

        Message result = cg.reconnectionHandler(lobbyMessage);

        assertNotNull(result);
        assertTrue(result instanceof ReconnectionRequest);
        assertEquals("Reconnection request", ((ReconnectionRequest) result).getMessage());
    }

    @Test
    void testReconnectionHandlerPlayerWasNotConnected() {
        LobbyMessage lobbyMessage = new LobbyMessage("Franco", "Token", false);
        cg.setGame(Game.getInstance(lobbyMessage.getSenderUsername()));
        Player player = new Player("Pier Giovanni", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.getGame().setStarted(true);
        cg.setTimer();

        Message result = cg.reconnectionHandler(lobbyMessage);

        assertNotNull(result);
        assertTrue(result instanceof Response);
        assertEquals("Reconnection message from already in lobby Player", ((Response) result).getMessage());
    }

    @Test
    public void testOnConnectionMessage_GameEnded() {
        cg.setGame(Game.getInstance("Matteo sei un leone"));
        Player player = new Player("Matteo sei un leone", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.setGameState(PossibleGameState.GAME_ENDED);

        Message connectionMessage = new EndGameMessage("Matteo sei un leone", "GameConstant.json");

        Message response = cg.onConnectionMessage(connectionMessage);

        assertEquals("GAME ENDED", ((Response) response).getMessage());
        assertEquals(MessageStatus.ERROR, ((Response) response).getStatus());
    }

    @Test
    public void testOnConnectionMessage_Invalid1() {
        cg.setGame(Game.getInstance("Matteo sei un leone"));
        Player player = new Player("Matteo sei un leone", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.setGameState(PossibleGameState.GAME_STARTED);

        cg.getGame().setStarted(false);

        Message connectionMessage = new LobbyMessage("Matteo sei un leone", null, false);

        Message response = cg.onConnectionMessage(connectionMessage);

        assertNotNull(response);
        assertTrue(response instanceof Response);
        assertEquals("Game is ended.", ((Response) response).getMessage());
        assertEquals(MessageStatus.ERROR, ((Response) response).getStatus());
    }

    @Test
    public void testOnConnectionMessage_Invalid2() {
        cg.setGame(Game.getInstance("Matteo sei un leone"));
        Player player = new Player("Matteo sei un leone", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player);
        cg.getGame().setCurrentPlayer(player);
        cg.setGameState(PossibleGameState.GAME_ROOM);

        Message connectionMessage = new LobbyMessage("Matteo sei un leone", null, false);

        Message response = cg.onConnectionMessage(connectionMessage);

        assertNotNull(response);
        assertTrue(response instanceof Response);
        assertEquals("Invalid message", ((Response) response).getMessage());
        assertEquals(MessageStatus.ERROR, ((Response) response).getStatus());
    }

    @Test
    public void testSendEndGame() {
        cg.setGame(Game.getInstance("Player1"));
        Player player1 = new Player("Player1", new Shelf(), new PersonalGoalCard(new ArrayList<>(), "1"));
        cg.getGame().addPlayer(player1);
        cg.getGame().setCurrentPlayer(player1);

        server.getPlayersGame().put(player1.getName(), cg);

        cg.sendEndGame();

        assertTrue(server.getControllerGames().isEmpty());
    }
}