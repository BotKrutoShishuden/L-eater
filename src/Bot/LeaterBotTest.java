package Bot;

import GameMap.GameCondition;
import GameMap.GameMap;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LeaterBotTest {
    //TODO поставить в humansScore нормальные значения

    private void testBotOnMap(String address, String testName, int humansScore,
                              int bestBotScore) {
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
        if (gameMap.getGameCondition() == GameCondition.ABORTED)
            System.out.println("АБОРТ ЭТО УБИЙСТВО");


    }

    @Test //ЭТО ВООБЩЕ ХУИТА КАКАЯ-ТО, ТУТ БОРОД И БРИТВ НЕТ
    public void beard0() {
        testBotOnMap("maps/beard0.map", "beard0", 0, 906);
    }

    @Test //CONFIRMED
    public void beard1() {
        testBotOnMap("maps/beard1.map", "beard1", 858, 891);
    }

    @Test //CONFIRMED
    public void beard2() {
        testBotOnMap("maps/beard2.map", "beard2", 4497, 2812);
    }

    @Test //TODO технически проходит, но почему-то абортится в двух шагах от лямбды
    public void beard3() {
        testBotOnMap("maps/beard3.map", "beard3", 1162, 954);
    }

    @Test //CONFIRMED
    public void beard4() {
        testBotOnMap("maps/beard4.map", "beard4", 2013, 0);
    }

    @Test //CONFIRMED
    public void beard5() {
        testBotOnMap("maps/beard5.map", "beard5", 657, 0);
    }

    @Test //CONFIRMED только там очков на 100 меньше должно быть
    public void contest1() {
        testBotOnMap("maps/contest1.map", "contest1", 210, 0);
    }

    @Test //CONFIRMED
    public void contest2() {
        testBotOnMap("maps/contest2.map", "contest2", 280, 0);
    }

    @Test //CONFIRMED, опять себе лишних очков нарисовал
    public void contest3() {
        testBotOnMap("maps/contest3.map", "contest3", 275, 524);
    }

    @Test //CONFIRMED, а тут наоборот, меньше дал, чем на самом деле, скромняга
    public void contest4() {
        testBotOnMap("maps/contest4.map", "contest4", 575, 0);
    }

    @Test //CONFIRMED
    public void contest5() {
        testBotOnMap("maps/contest5.map", "contest5", 1297, 0);
    }

    @Test //CONFIRMED
    //Не ест дальнюю лямбду, а надо бы
    public void contest6() {
        testBotOnMap("maps/contest6.map", "contest6", 1177, 712);
    }

    @Test //CONFIRMED, BUT STILL WRONG SCORE (DANEK PRIVET)
    public void contest7() {
        testBotOnMap("maps/contest7.map", "contest7", 869, 0);
    }

    @Test //CONFIRMED
    public void contest8() {
        testBotOnMap("maps/contest8.map", "contest8", 1952, 0);
    }

    @Test //CONFIRMED, НО ОН КАК-ТО ТУПО СДЕЛАЛ (НО ОН ВСЕ РАВНО КРУТОЙ)
    public void contest9() {
        testBotOnMap("maps/contest9.map", "contest9", 3088, 0);
    }

    @Test //CONFIRMED
    public void contest10() {
        testBotOnMap("maps/contest10.map", "contest10", 3594, 0);
    }

    @Test //CONFIRMED, OPYAT OCHKI SCHITAET NE TAK, A MNE LEN' MENYAT' RASKLADKU
    public void ems1() {
        testBotOnMap("maps/ems1.map", "ems1", 334, 377);
    }

    @Test //CONFIRMED
    public void flood1() {
        testBotOnMap("maps/flood1.map", "flood1", 937, 0);
    }

    @Test //TODO а тут робот сдох (утонул лох)
    public void flood2() {
        testBotOnMap("maps/flood2.map", "flood2", 281, 0);
    }

    @Test //TODO опять утонул лох
    public void flood3() {
        testBotOnMap("maps/flood3.map", "flood3", 1293, 0);
    }

    @Test //CONFIRMED NE LOX
    public void flood4() {
        testBotOnMap("maps/flood4.map", "flood4", 826, 0);
    }

    @Test //TODO снова лох, чето с водой походу, он в ней вообще на похуй ходит
    public void flood5() {
        testBotOnMap("maps/flood5.map", "flood5", 567, 0);
    }

    @Test //CONFIRMED
    public void trampoline1() {
        testBotOnMap("maps/trampoline1.map", "trampoline1",407 , 0);
    }

    @Test
    //TODO технически, соответствует оригинальному симулятору, но что-то странное со сбором лямбд, мог бы и выйти в WIN
    public void trampoline2() {
        testBotOnMap("maps/trampoline2.map", "trampoline2", 1724 , 0);
    }

    @Test //CONFIRMED
    public void trampoline3() {
        testBotOnMap("maps/trampoline3.map", "trampoline3", 5467 , 0);
    }

    @Test //TODO говорю же, что-то с водой
    public void horock2() {
        testBotOnMap("maps/horock2.map", "horock2", 735 , 1203);
    }


    //TODO в нормальном симуляторе при текущей выходной последовательности бот ломается под камнем
    //TODO Проверить этот тест после отладки лямбда камней
    @Test //CONFIRMED
    public void horock3() {
        testBotOnMap("maps/horock3.map", "horock3", 2365, 1203);
    }

}