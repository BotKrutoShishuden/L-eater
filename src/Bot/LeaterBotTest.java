package Bot;

import GameMap.GameMap;
import org.junit.Test;

import static org.junit.Assert.*;

public class LeaterBotTest {

    @Test
    public void calculateBestSteps() {
    }

    @Test
    public void beard0() {
        GameMap gameMap = GameMap.cutNormalMap("maps/beard0.map");
        LeaterBot leaterBot = new LeaterBot(gameMap);
        for (NextStep nextStep : leaterBot.calculateBestSteps())
            System.out.print(nextStep.getSymbol());

    }
}