package Bot;

import GameMap.GameCondition;
import GameMap.GameMap;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LeaterBotTest {


    private void testBotOnMap(String address) {
        GameMap gameMap = GameMap.cutNormalMap(address);
        LeaterBot leaterBot = new LeaterBot(gameMap);
        List<NextStep> bestWay = leaterBot.calculateBestSteps();

        for (NextStep nextStep : bestWay) {
            System.out.print(nextStep.getSymbol());
            gameMap.moveAllObjects(nextStep);


        }
        assertEquals(bestWay.size(), gameMap.getAmountOfSteps());
        assertEquals(true, gameMap.getScore() > 0);
        assertEquals(GameCondition.WIN, gameMap.getGameCondition());

    }

    @Test
    public void beard0() {
        testBotOnMap("maps/beard0.map");
    }

    @Test
    public void beard1() {
        testBotOnMap("maps/beard1.map");
    }
}