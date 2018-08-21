package Bot;

import GameMap.GameMap;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LeaterBotTest {
    private GameMap gameMap;
    private LeaterBot leaterBot;
    private List<NextStep> nextSteps;


    private List<NextStep> calculateBestSteps(String address) {
        gameMap = GameMap.cutNormalMap(address);
        leaterBot = new LeaterBot(gameMap);
        return leaterBot.calculateBestSteps();

    }

    @Test
    public void beard0() {
        nextSteps = calculateBestSteps("maps/beard0.map");
        for (NextStep nextStep : nextSteps) {
            System.out.print(nextStep.getSymbol());
            gameMap.moveAllObjects(nextStep);
        }

        assertEquals(nextSteps.size(), gameMap.getAmountOfSteps());
        assertEquals(true, gameMap.getScore() > 0);


    }
}