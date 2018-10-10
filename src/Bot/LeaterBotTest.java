package Bot;

import GameMap.GameCondition;
import GameMap.GameMap;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;
import java.util.*;


import static org.junit.Assert.*;

public final class LeaterBotTest {
    private static String address = "src/Bot/LResults.txt";
    private static Map<String, Integer> oldResultMap = parseOldResults(address);
    private static Map<String, Integer> resultsMap = new HashMap<>();
    private static Map<String, String> reportMap = new HashMap<>();
    private static Map<String, String> stepsMap = new HashMap<>();
    private static Map<String, GameCondition> conditionMap = new HashMap<>();

    private static Map<String, Integer> parseOldResults(String address) {
        try {
            Map<String, Integer> returnedOldResultMap = new HashMap<>();
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
            int c;
            while ((c = bufferedReader.read()) != -1)
                stringBuilder.append((char) c);

            String lines[] = stringBuilder.toString().split("\n");
            for (int i = 0; i < lines.length; i++)
                if (lines[i].contains("Score = "))
                    returnedOldResultMap.put(lines[i - 1].trim(), Integer.valueOf(lines[i].replace("Score = ", "").trim()));

            return returnedOldResultMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO сравнить счет из LResults.txt с результатами в симуляторе

    @AfterClass
    public static void reWriteOfLeaterResults() {
        try {


            Iterator<Map.Entry<String, Integer>> resultsSet = resultsMap.entrySet().iterator();
            Iterator<Map.Entry<String, String>> reportSet = reportMap.entrySet().iterator();
            Iterator<Map.Entry<String, String>> stepsSet = stepsMap.entrySet().iterator();
            Iterator<Map.Entry<String, GameCondition>> conditionSet = conditionMap.entrySet().iterator();

            StringBuilder stringBuilder = new StringBuilder();

            while (resultsSet.hasNext() && reportSet.hasNext() && stepsSet.hasNext() && conditionSet.hasNext()) {
                Map.Entry<String, Integer> resultEntry = resultsSet.next();
                Map.Entry<String, String> reportEntry = reportSet.next();
                Map.Entry<String, String> stepsEntry = stepsSet.next();
                Map.Entry<String, GameCondition> conditionEntry = conditionSet.next();
                stringBuilder.append(resultEntry.getKey()).append("\nScore = ").
                        append(resultEntry.getValue()).append("\nReport = ").
                        append(reportEntry.getValue()).append("\nSteps = ").
                        append(stepsEntry.getValue()).append("\n").
                        append(conditionEntry.getValue()).append("\n").
                        append("-----------------------------------------\n");
            }


            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(address));
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void testBotOnMap(String address, String testName, int humansScore) {
        GameMap gameMap = GameMap.cutNormalMap(address);
        LeaterBot leaterBot = new LeaterBot(gameMap);
        List<NextStep> bestWay = leaterBot.calculateBestSteps();

        StringBuilder stepsBuilder = new StringBuilder();
        for (NextStep nextStep : bestWay) {
            gameMap.moveAllObjects(nextStep);
            stepsBuilder.append(nextStep.getSymbol());
        }
        conditionMap.put(testName, gameMap.getGameCondition());

        resultsMap.put(testName, gameMap.getScore());

        try {
            if (gameMap.getScore() > oldResultMap.get(testName))
                reportMap.put(testName, "BETTER");
            else if (gameMap.getScore() < oldResultMap.get(testName))
                reportMap.put(testName, "WORSE");
            else
                reportMap.put(testName, "");
        } catch (NullPointerException e) {
            reportMap.put(testName, "");
        }

        stepsMap.put(testName, stepsBuilder.toString());

        System.out.println(stepsBuilder.toString());
        System.out.println(gameMap.getGameCondition());


        //assertEquals(bestWay.size(), gameMap.getAmountOfSteps());
        assertTrue(gameMap.getScore() > 0);
        assertTrue(gameMap.getGameCondition() == GameCondition.WIN ||
                gameMap.getGameCondition() == GameCondition.ABORTED);

    }

    //CONFIRMED
    @Test
    public void beard0() {
        testBotOnMap("maps/beard0.map", "beard0", 858);
    }

    //CONFIRMED
    @Test
    public void beard1() {
        testBotOnMap("maps/beard1.map", "beard1", 858);
    }

    //CONFIRMED
    @Test
    public void beard2() {
        testBotOnMap("maps/beard2.map", "beard2", 4497);
    }

    //CONFIRMED
    @Test
    public void beard3() {
        testBotOnMap("maps/beard3.map", "beard3", 1162);
    }

    //CONFIRMED
    @Test
    public void beard4() {
        testBotOnMap("maps/beard4.map", "beard4", 2013);
    }

    //CONFIRMED
    @Test
    public void beard5() {
        testBotOnMap("maps/beard5.map", "beard5", 657);
    }

    //CONFIRMED
    @Test
    public void contest1() {
        testBotOnMap("maps/contest1.map", "contest1", 210);
    }

    //CONFIRMED
    @Test
    public void contest2() {
        testBotOnMap("maps/contest2.map", "contest2", 280);
    }

    //CONFIRMED
    @Test
    public void contest3() {
        testBotOnMap("maps/contest3.map", "contest3", 275);
    }

    //CONFIRMED
    @Test
    public void contest4() {
        testBotOnMap("maps/contest4.map", "contest4", 575);
    }

    //CONFIRMED
    @Test
    public void contest5() {
        testBotOnMap("maps/contest5.map", "contest5", 1297);
    }

    //CONFIRMED
    @Test
    //Не ест дальнюю лямбду, а надо бы
    public void contest6() {
        testBotOnMap("maps/contest6.map", "contest6", 1177);
    }

    //CONFIRMED
    @Test
    public void contest7() {
        testBotOnMap("maps/contest7.map", "contest7", 869);
    }

    //Мало шагов
    @Test
    public void contest8() {
        testBotOnMap("maps/contest8.map", "contest8", 1952);
    }

    //Мало шагов
    @Test
    public void contest9() {
        testBotOnMap("maps/contest9.map", "contest9", 3088);
    }

    //Мало шагов
    @Test
    public void contest10() {
        testBotOnMap("maps/contest10.map", "contest10", 3594);
    }

    @Test
    public void ems1() {
        testBotOnMap("maps/ems1.map", "ems1", 334);
    }

    //TODO Шаг влево и было бы на лямбду лучше
    @Test
    public void flood1() {
        testBotOnMap("maps/flood1.map", "flood1", 937);
    }

    //CONFIRMED
    @Test
    public void flood2() {
        testBotOnMap("maps/flood2.map", "flood2", 281);
    }

    //CONFIRMED
    @Test
    public void flood3() {
        testBotOnMap("maps/flood3.map", "flood3", 1293);
    }

    //Мало шагов
    @Test
    public void flood4() {
        testBotOnMap("maps/flood4.map", "flood4", 826);
    }

    //TODO не хочет искать открытый лифт
    @Test
    public void flood5() {
        testBotOnMap("maps/flood5.map", "flood5", 567);
    }


    //CONFIRMED
    @Test
    public void trampoline1() {
        testBotOnMap("maps/trampoline1.map", "trampoline1", 407);
    }

    //CONFIRMED
    @Test
    public void trampoline2() {
        testBotOnMap("maps/trampoline2.map", "trampoline2", 1724);
    }

    //CONFIRMED
    @Test
    public void trampoline3() {
        testBotOnMap("maps/trampoline3.map", "trampoline3", 5467);
    }

    //CONFIRMED
    @Test
    public void horock1() {
        testBotOnMap("maps/horock1.map", "horock1", 735);
    }

    //CONFIRMED
    @Test
    public void horock2() {
        testBotOnMap("maps/horock2.map", "horock2", 735);
    }

    //CONFIRMED
    @Test
    public void horock3() {
        testBotOnMap("maps/horock3.map", "horock3", 2365);
    }


}