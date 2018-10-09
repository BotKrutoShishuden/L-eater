package Bot;

import GameMap.GameCondition;
import GameMap.GameMap;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;
import java.util.*;


import static org.junit.Assert.*;

public class LeaterBotTest {
    private static String address = "src/Bot/LResults.txt";
    private static Map<String, Integer> oldResultMap = parseOldResults(address);
    private static Map<String, Integer> resultsMap = new HashMap<>();
    private static Map<String, String> reportMap = new HashMap<>();
    private static Map<String, String> stepsMap = new HashMap<>();

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
                    returnedOldResultMap.put(lines[i - 1].trim(),
                            Integer.valueOf(lines[i].replace("Score = ", "")));

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

            StringBuilder stringBuilder = new StringBuilder();

            while (resultsSet.hasNext() && reportSet.hasNext() && stepsSet.hasNext()) {
                Map.Entry<String, Integer> resultEntry = resultsSet.next();
                Map.Entry<String, String> reportEntry = reportSet.next();
                Map.Entry<String, String> stepsEntry = stepsSet.next();
                stringBuilder.append(resultEntry.getKey()).append("\nScore = ").
                        append(resultEntry.getValue()).append("\nReport = ").
                        append(reportEntry.getValue()).append("\nSteps = ").append(stepsEntry.getValue()).append("\n").
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

        resultsMap.put(testName, gameMap.getScore());

        if (gameMap.getScore() > oldResultMap.get(testName))
            reportMap.put(testName, "BETTER");
        else if (gameMap.getScore() < oldResultMap.get(testName))
            reportMap.put(testName, "WORSE");
        else
            reportMap.put(testName, "");

        stepsMap.put(testName, stepsBuilder.toString());


        //assertEquals(bestWay.size(), gameMap.getAmountOfSteps());
        assertTrue(gameMap.getScore() > 0);
        assertTrue(gameMap.getGameCondition() == GameCondition.WIN ||
                gameMap.getGameCondition() == GameCondition.ABORTED);

    }

    @Test //ЭТО ВООБЩЕ ХУИТА КАКАЯ-ТО, ТУТ БОРОД И БРИТВ НЕТ
    public void beard0() {
        testBotOnMap("maps/beard0.map", "beard0", 0);
    }

    @Test //CONFIRMED
    public void beard1() {
        testBotOnMap("maps/beard1.map", "beard1", 858);
    }

    @Test //CONFIRMED
    public void beard2() {
        testBotOnMap("maps/beard2.map", "beard2", 4497);
    }

    //TODO технически проходит, но почему-то абортится в двух шагах от лямбды
    @Test
    public void beard3() {
        testBotOnMap("maps/beard3.map", "beard3", 1162);
    }

    @Test //CONFIRMED
    public void beard4() {
        testBotOnMap("maps/beard4.map", "beard4", 2013);
    }

    @Test //CONFIRMED
    public void beard5() {
        testBotOnMap("maps/beard5.map", "beard5", 657);
    }

    @Test //CONFIRMED только там очков на 100 меньше должно быть
    public void contest1() {
        testBotOnMap("maps/contest1.map", "contest1", 210);
    }

    @Test //CONFIRMED
    public void contest2() {
        testBotOnMap("maps/contest2.map", "contest2", 280);
    }

    @Test //CONFIRMED, опять себе лишних очков нарисовал
    public void contest3() {
        testBotOnMap("maps/contest3.map", "contest3", 275);
    }

    @Test //CONFIRMED, а тут наоборот, меньше дал, чем на самом деле, скромняга
    public void contest4() {
        testBotOnMap("maps/contest4.map", "contest4", 575);
    }

    @Test //CONFIRMED
    public void contest5() {
        testBotOnMap("maps/contest5.map", "contest5", 1297);
    }

    @Test //CONFIRMED
    //Не ест дальнюю лямбду, а надо бы
    public void contest6() {
        testBotOnMap("maps/contest6.map", "contest6", 1177);
    }

    @Test //CONFIRMED, BUT STILL WRONG SCORE (DANEK PRIVET)
    public void contest7() {
        testBotOnMap("maps/contest7.map", "contest7", 869);
    }

    @Test //CONFIRMED
    public void contest8() {
        testBotOnMap("maps/contest8.map", "contest8", 1952);
    }

    @Test //CONFIRMED, НО ОН КАК-ТО ТУПО СДЕЛАЛ (НО ОН ВСЕ РАВНО КРУТОЙ)
    public void contest9() {
        testBotOnMap("maps/contest9.map", "contest9", 3088);
    }

    @Test //CONFIRMED
    public void contest10() {
        testBotOnMap("maps/contest10.map", "contest10", 3594);
    }

    @Test //CONFIRMED, OPYAT OCHKI SCHITAET NE TAK, A MNE LEN' MENYAT' RASKLADKU
    public void ems1() {
        testBotOnMap("maps/ems1.map", "ems1", 334);
    }

    @Test //CONFIRMED
    public void flood1() {
        testBotOnMap("maps/flood1.map", "flood1", 937);
    }

    //TODO а тут робот сдох (утонул лох)
    @Test
    public void flood2() {
        testBotOnMap("maps/flood2.map", "flood2", 281);
    }

    //TODO опять утонул лох
    @Test
    public void flood3() {
        testBotOnMap("maps/flood3.map", "flood3", 1293);
    }

    @Test //CONFIRMED NE LOX
    public void flood4() {
        testBotOnMap("maps/flood4.map", "flood4", 826);
    }

    //TODO снова лох, чето с водой походу, он в ней вообще на похуй ходит
    @Test
    public void flood5() {
        testBotOnMap("maps/flood5.map", "flood5", 567);
    }

    @Test //CONFIRMED
    public void trampoline1() {
        testBotOnMap("maps/trampoline1.map", "trampoline1", 407);
    }

    //TODO технически, соответствует оригинальному симулятору, но что-то странное со сбором лямбд, мог бы и выйти в WIN
    @Test
    public void trampoline2() {
        testBotOnMap("maps/trampoline2.map", "trampoline2", 1724);
    }

    @Test //CONFIRMED
    public void trampoline3() {
        testBotOnMap("maps/trampoline3.map", "trampoline3", 5467);
    }

    @Test
    public void horock1() {
        testBotOnMap("maps/horock1.map", "horock1", 735);
    }

    //TODO говорю же, что-то с водой
    @Test
    public void horock2() {
        testBotOnMap("maps/horock2.map", "horock2", 735);
    }


    //TODO в нормальном симуляторе при текущей выходной последовательности бот ломается под камнем
    //TODO Проверить этот тест после отладки лямбда камней
    @Test //CONFIRMED
    public void horock3() {
        testBotOnMap("maps/horock3.map", "horock3", 2365);
    }


}