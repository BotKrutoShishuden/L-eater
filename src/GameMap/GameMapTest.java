package GameMap;

import Bot.NextStep;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class GameMapTest {

    //Методы для автоматизации и облегчения тестирования
    private void makeTestFromFormattedFile(String address) throws IOException, NullPointerException {


        GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");

        NextStep nextSteps[] = GameMap.cutSteps(address);

        inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
        inputMap.setRazorsNumber(GameMap.cutParamAfterWord(address, "Razors "));
        inputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding "));

        for (NextStep nextStep : nextSteps)
            inputMap.moveAllObjects(nextStep);

        GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");

        assertEquals(outputMap.toString(), inputMap.toString());


    }


    private void makeTestFromFormattedDirectory(int testNumber, String testName, String address) {
        int numberOfErrorTest = 0;


        try {

            for (int i = 0; i < testNumber; ) {
                makeTestFromFormattedFile(address);
                numberOfErrorTest++;
                i++;
                address = address.replace("" + (i - 1), "" + i);
            }

        } catch (ComparisonFailure e) {
            System.out.println("Error in " + testName + "\nTest number = " + numberOfErrorTest);
            throw e;
        } catch (IOException e) {
            System.out.println("Error in Path " + testName + "\nTest number = " + numberOfErrorTest);
        } catch (NullPointerException n) {
            System.out.println("Error in Formatting " + testName + "\nTest number = " + numberOfErrorTest);
            throw new ComparisonFailure
                    ("PROBLEMS WITH FORMATTING", "FORMATTING TEST", "NOT FORMATTING TEST");
        }
    }


    private void makeDifficultTestFromFormattedDirectory(int testNumber, String testName,
                                                         String address) {
        int i = 0;
        try {

            for (i = 0; i < testNumber; ) {

                GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");
                inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
                inputMap.setRazorsNumber(GameMap.cutParamAfterWord(address, "Razors "));
                inputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding "));

                NextStep nextSteps[] = GameMap.cutSteps(address);

                int j = 0;
                for (NextStep nextStep : nextSteps) {
                    j++;
                    inputMap.moveAllObjects(nextStep);
                }


                //TestingOf GameMap.back()
                if (GameMap.STORAGE_PREVIOUS_MAP) {
                    for (NextStep nextStep : nextSteps)
                        inputMap.moveAllObjects(NextStep.BACK);

                    for (NextStep nextStep : nextSteps)
                        inputMap.moveAllObjects(nextStep);
                }
                //


                //Эталонная карта
                GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");
                outputMap.setGrowth(GameMap.cutParamAfterWord(address, "F_Growth "));
                outputMap.setRazorsNumber(GameMap.cutParamAfterWord(address, "F_Razors "));
                outputMap.setFlooding(GameMap.cutParamAfterWord(address, "F_Flooding "));
                outputMap.setScore(GameMap.cutParamAfterWord(address, "F_Score "));
                outputMap.setAmountOfSteps(GameMap.cutParamAfterWord(address, "F_Moves "));
                outputMap.setLamdasNumber(GameMap.cutParamAfterWord(address, "F_Lambda "));
                outputMap.setMaxLambdasNumber(GameMap.cutParamAfterWord(address, "F_LambdaMax "));
                outputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "F_WaterLevel "));
                outputMap.setGameCondition(GameMap.cutConditionAfterWord(address, "F_GameCondition "));

                //Сравнение
                assertEquals(outputMap.getGrowth(), inputMap.getGrowth());
                assertEquals(outputMap.getRazorsNumber(), inputMap.getRazorsNumber());
                assertEquals(outputMap.getFlooding(), inputMap.getFlooding());
                assertEquals(outputMap.getMaxX(), inputMap.getMaxX());
                assertEquals(outputMap.getMaxY(), inputMap.getMaxY());
                assertEquals(inputMap.getLamdasNumber(), inputMap.getCollectedLambdas().length);
                assertEquals(outputMap.getScore(), inputMap.getScore());//TODO Какого
                assertEquals(outputMap.getAmountOfSteps(), inputMap.getAmountOfSteps());
                assertEquals(outputMap.getLamdasNumber(), inputMap.getLamdasNumber());
                assertEquals(outputMap.getMaxLambdasNumber(), inputMap.getMaxLambdasNumber());
                assertEquals(outputMap.getWaterLevel(), inputMap.getWaterLevel());
                assertEquals(outputMap.toString(), inputMap.toString());


                i++;
                address = address.replace((i - 1) + "", i + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            System.out.println("Comparison failure in " + testName);
            System.out.println("Test number = " + (i));
            throw e;
        } catch (NullPointerException n) {
            System.out.println("Null pointer exception in " + testName);
            System.out.println("Test number = " + (i));
            throw n;
        }

    }
    //-----------------------------------------------------------------------------------

    @Test
    public void moveBot() {
        makeTestFromFormattedDirectory(5, "left step tests", "maps/A_moveBotTests/Left/0_test.map");

        makeTestFromFormattedDirectory(5, "right step tests", "maps/A_moveBotTests/Right/0_test.map");

        makeTestFromFormattedDirectory(5, "down step tests", "maps/A_moveBotTests/Down/0_test.map");

        makeTestFromFormattedDirectory(5, "up step tests", "maps/A_moveBotTests/Up/0_test.map");


    }

    @Test
    public void moveStones() {

        makeTestFromFormattedDirectory(8, "stay step & stones tests", "maps/B_moveStoneTests/0_test.map");

    }

    @Test
    public void moveLambdaStones() {

        makeTestFromFormattedDirectory(2, "lambdas collapse", "maps/С_lambdaStoneTests/0_test.map");

    }

    @Test
    public void growBeard() {
        makeTestFromFormattedDirectory(5, "grow simple beard, obstacles and cutting", "" +
                "maps/growBeardTests/0_test.map");
    }
    //-----------------------------------------------------------------------------------

    @Test
    public void conditionTest() {
        makeDifficultTestFromFormattedDirectory
                (3, "condtition test", "maps/conditionTests/0_test.map");
    }

    @Test
    public void difficultTest() {
        makeDifficultTestFromFormattedDirectory(3, "difficult test", "maps/testsForDifficultIncidents/0_test.map");

    }

    @Test
    public void cutNormalMap() {
        GameMap gameMap = GameMap.cutNormalMap("maps/beard1.map");

        assertEquals(10, gameMap.getMaxX());
        assertEquals(10, gameMap.getMaxY());
        assertEquals(15, gameMap.getGrowth());
        assertEquals(0, gameMap.getRazorsNumber());

    }

    @Test
    public void copy() {
        GameMap gameMap = GameMap.cutNormalMap("maps/horock3.map");
        GameMap copiedMap = gameMap.copy();


    }
}
