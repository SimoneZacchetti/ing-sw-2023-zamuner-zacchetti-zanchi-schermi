package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonGoalType6Test {


    @Test
    public void testCheckGoalNotEligibleShelf(){
        Shelf shelf = new Shelf();
        assertFalse(new CommonGoalType6().checkGoal(shelf));

    }

    @Test
    public void testCheckGoalReturnsTrue(){
        Shelf shelf = new Shelf();

        shelf.getGrid().put(new Coordinate(0,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(1,0), new ObjectCard(ObjectCardType.plant, "00"));
        shelf.getGrid().put(new Coordinate(2,0), new ObjectCard(ObjectCardType.frame, "00"));
        shelf.getGrid().put(new Coordinate(3,0), new ObjectCard(ObjectCardType.game, "00"));
        shelf.getGrid().put(new Coordinate(4,0), new ObjectCard(ObjectCardType.book, "00"));
        shelf.getGrid().put(new Coordinate(5,0), new ObjectCard(ObjectCardType.trophy, "00"));

        shelf.getGrid().put(new Coordinate(0,1), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(1,1), new ObjectCard(ObjectCardType.plant, "00"));
        shelf.getGrid().put(new Coordinate(2,1), new ObjectCard(ObjectCardType.frame, "00"));
        shelf.getGrid().put(new Coordinate(3,1), new ObjectCard(ObjectCardType.book, "00"));
        shelf.getGrid().put(new Coordinate(4,1), new ObjectCard(ObjectCardType.trophy, "00"));
        shelf.getGrid().put(new Coordinate(5,1), new ObjectCard(ObjectCardType.game, "00"));

        assertTrue(new CommonGoalType6().checkGoal(shelf));

    }

    @Test
    public void testCheckGoalInvalidColumnsReturnsFalse(){
        Shelf shelf = new Shelf();

        shelf.getGrid().put(new Coordinate(0,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(1,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(2,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(3,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(4,0), new ObjectCard(ObjectCardType.book, "00"));
        shelf.getGrid().put(new Coordinate(5,0), new ObjectCard(ObjectCardType.trophy, "00"));

        shelf.getGrid().put(new Coordinate(0,1), new ObjectCard(ObjectCardType.plant, "00"));
        shelf.getGrid().put(new Coordinate(1,1), new ObjectCard(ObjectCardType.plant, "00"));
        shelf.getGrid().put(new Coordinate(2,1), new ObjectCard(ObjectCardType.frame, "00"));
        shelf.getGrid().put(new Coordinate(3,1), new ObjectCard(ObjectCardType.book, "00"));
        shelf.getGrid().put(new Coordinate(4,1), new ObjectCard(ObjectCardType.trophy, "00"));
        shelf.getGrid().put(new Coordinate(5,1), new ObjectCard(ObjectCardType.game, "00"));

        assertFalse(new CommonGoalType6().checkGoal(shelf));

    }

    @Test
    public void testCheckGoalOnlyOneValidColumnReturnsFalse(){
        Shelf shelf = new Shelf();

        shelf.getGrid().put(new Coordinate(0,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(1,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(2,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(3,0), new ObjectCard(ObjectCardType.cat, "00"));
        shelf.getGrid().put(new Coordinate(4,0), new ObjectCard(ObjectCardType.book, "00"));
        shelf.getGrid().put(new Coordinate(5,0), new ObjectCard(ObjectCardType.trophy, "00"));

        assertFalse(new CommonGoalType6().checkGoal(shelf));

    }


}