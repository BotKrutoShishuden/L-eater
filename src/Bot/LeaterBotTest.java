package Bot;

import GameMap.GameCondition;
import GameMap.GameMap;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LeaterBotTest {
    //TODO поставить в humansScore нормальные значения

    private void testBotOnMap(String address, String testName, int humansScore, int bestBotScore) {
        System.out.println(testName);
        GameMap gameMap = GameMap.cutNormalMap(address);
        LeaterBot leaterBot = new LeaterBot(gameMap);
        List<NextStep> bestWay = leaterBot.calculateBestSteps();

        System.out.println("Steps");
        for (NextStep nextStep : bestWay) {
            System.out.print(nextStep.getSymbol());
            gameMap.moveAllObjects(nextStep);
        }

        System.out.println("\nCurrent = " + gameMap.getScore() +
                "| Humans = " + humansScore +
                "| BotBest = " + bestBotScore + "|");
        System.out.println(gameMap.getGameCondition());
        System.out.println("------------------------------------");

        assertEquals(bestWay.size(), gameMap.getAmountOfSteps());
        assertTrue(gameMap.getScore() > 0);
        assertTrue(gameMap.getGameCondition() == GameCondition.WIN ||
                gameMap.getGameCondition() == GameCondition.ABORTED);


    }

    @Test
    public void beard0() {
        testBotOnMap("maps/beard0.map", "beard0", 0, 906);
    }

    @Test
    public void beard1() {
        testBotOnMap("maps/beard1.map", "beard1", 0, 891);
    }

    @Test
    public void beard2() {
        testBotOnMap("maps/beard2.map", "beard2", 0, 2812);
    }

    @Test
    public void beard3() {
        testBotOnMap("maps/beard3.map", "beard3", 0, 954);
    }

    @Test
    public void beard4() {
        testBotOnMap("maps/beard4.map", "beard4", 0, 0);
    }

    @Test
    public void beard5() {
        testBotOnMap("maps/beard5.map", "beard4", 0, 0);
    }

    @Test
    public void contest1() {
        testBotOnMap("maps/contest1.map", "contest1", 0, 0);
    }

    @Test
    public void contest2() {
        testBotOnMap("maps/contest2.map", "contest2", 0, 0);
    }

    @Test
    public void contest3() {
        testBotOnMap("maps/contest3.map", "contest3", 0, 524);
    }
    
    @Test
    public void contest4() {
        testBotOnMap("maps/contest4.map", "contest4", 0, 0);
    }

    @Test
    public void contest5() {
        testBotOnMap("maps/contest5.map", "contest5", 0, 0);
    }
    
    @Test
    //Не ест дальнюю лямбду, а надо бы
    public void contest6() {
        testBotOnMap("maps/contest6.map", "contest6", 0, 712);
    }

    @Test
    public void contest7() {
        testBotOnMap("maps/contest7.map", "contest7", 0, 0);
    }

    @Test
    public void contest8() {
        testBotOnMap("maps/contest8.map", "contest8", 0, 0);
    }

    @Test
    public void contest9() {
        testBotOnMap("maps/contest9.map", "contest9", 0, 0);
    }

    @Test
    public void contest10() {
        testBotOnMap("maps/contest10.map", "contest10", 0, 0);
    }
    
    @Test
    public void custom0() {
        testBotOnMap("maps/custom0.map", "custom0", 0, 0);
    }
    
    @Test
    public void ems1() {
        testBotOnMap("maps/ems1.map", "ems1", 0, 377);
    }

    @Test
    public void flood1() {
        testBotOnMap("maps/flood1.map", "flood1", 0, 0);
    }

    @Test
    public void flood2() {
        testBotOnMap("maps/flood2.map", "flood2", 0, 0);
    }

    @Test
    public void flood3() {
        testBotOnMap("maps/flood3.map", "flood3", 0, 0);
    }

    @Test
    public void flood4() {
        testBotOnMap("maps/flood4.map", "flood4", 0, 0);
    }

    @Test
    public void flood5() {
        testBotOnMap("maps/flood5.map", "flood5", 0, 0);
    }

    @Test
    public void trampoline1() {
        testBotOnMap("maps/trampoline1.map", "trampoline1", 0, 0);
    }

    @Test
    public void trampoline2() {
        testBotOnMap("maps/trampoline2.map", "trampoline2", 0, 0);
    }

    @Test
    public void trampoline3() {
        testBotOnMap("maps/trampoline3.map", "trampoline3", 0, 0);
    }
    
    @Test
    public void horock2() {
        testBotOnMap("maps/horock2.map", "horock2", 0, 1203);
    }


    //TODO в нормальном симуляторе при текущей выходной последовательности бот ломается под камнем
    //TODO Проверить этот тест после отладки лямбда камней
    @Test
    public void horock3() {
        testBotOnMap("maps/horock3.map", "horock3", 0, 1203);
    }

}