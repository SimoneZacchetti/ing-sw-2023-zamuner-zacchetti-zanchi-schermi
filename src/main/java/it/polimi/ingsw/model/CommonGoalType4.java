package it.polimi.ingsw.model;

// Quattro righe formate ciascuna da 5 tessere di uno, due o tre tipi differenti.
// Righe diverse possono avere combinazioni diverse di tipi di tessere.

import java.util.HashMap;
import java.util.Map;

public class CommonGoalType4 extends CommonGoal {

    public CommonGoalType4() {
    }

    @Override
    public boolean checkGoal(Shelf shelf) {
        Map<Coordinate, ObjectCard> grid = shelf.getGrid();
        int validRowCount = 0;

        for (int row = 0; row < shelf.ROWS; row++) {
            Map<ObjectCardType, Integer> rowTypeCount = new HashMap<>();

            for (int col = 0; col < shelf.COLUMNS; col++) {
                Coordinate coordinate = new Coordinate(col, row);
                ObjectCard objectCard = grid.get(coordinate);
                ObjectCardType cardType = objectCard.getType();

                if (objectCard != null) {
                    rowTypeCount.put(cardType, rowTypeCount.getOrDefault(cardType, 0) + 1);
                }
            }

            if (rowTypeCount.size() >= 1 && rowTypeCount.size() <= 3) {
                int totalCardsInRow = rowTypeCount.values().stream().mapToInt(Integer::intValue).sum();
                if (totalCardsInRow == shelf.COLUMNS) {
                    validRowCount++;
                }
            }
        }

        return validRowCount == 4;
    }

}
