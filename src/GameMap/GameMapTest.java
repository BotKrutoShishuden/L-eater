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
        inputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "Water "));
        inputMap.setMaxMovesUnderWater(GameMap.cutParamAfterWord(address, "Waterproof "));

        for (NextStep nextStep : nextSteps)
            inputMap.moveAllObjects(nextStep);

        GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");
        assertEquals(outputMap.toString(), inputMap.toString());
        if (address.equals("maps/G_scoreTests/0_test.map")) {
            outputMap.setScore(GameMap.cutParamAfterWord(address, "Score_F "));
            assertEquals(outputMap.getScore(), inputMap.getScore());
        }
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
                inputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "Water "));
                inputMap.setMaxMovesUnderWater(GameMap.cutParamAfterWord(address, "Waterproof "));
                NextStep nextSteps[] = GameMap.cutSteps(address);


                for (NextStep nextStep : nextSteps)
                    inputMap.moveAllObjects(nextStep);


                //TestingOf GameMap.back()
                if (GameMap.STORAGE_PREVIOUS_MAP) {
                    for (NextStep nextStep : nextSteps)
                        inputMap.moveAllObjects(NextStep.BACK);

                    for (NextStep nextStep : nextSteps)
                        inputMap.moveAllObjects(nextStep);
                }


                //Эталонная карта
                GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");
                outputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth_F "));
                outputMap.setRazorsNumber(GameMap.cutParamAfterWord(address, "Razors_F "));
                outputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding_F "));
                outputMap.setScore(GameMap.cutParamAfterWord(address, "Score_F "));
                outputMap.setAmountOfSteps(GameMap.cutParamAfterWord(address, "Moves_F "));
                outputMap.setLambdasNumber(GameMap.cutParamAfterWord(address, "Lambda_F "));
                outputMap.setMaxLambdasNumber(GameMap.cutParamAfterWord(address, "LambdaMax_F "));
                outputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "WaterLevel_F "));
                outputMap.setMovesUnderWater(GameMap.cutParamAfterWord(address, "CurMovesUnderWater_F "));
                outputMap.setMaxMovesUnderWater(GameMap.cutParamAfterWord(address, "MaxMovesUnderWater_F "));
                outputMap.setGameCondition(GameMap.cutConditionAfterWord(address, "GameCondition_F "));

                //Сравнение
                assertEquals(outputMap.getGrowth(), inputMap.getGrowth());
                assertEquals(outputMap.getFlooding(), inputMap.getFlooding());
                assertEquals(outputMap.getWaterLevel(), inputMap.getWaterLevel());
                assertEquals(outputMap.getRazorsNumber(), inputMap.getRazorsNumber());
                assertEquals(outputMap.getMaxMovesUnderWater(), inputMap.getMaxMovesUnderWater());
                assertEquals(outputMap.getMaxX(), inputMap.getMaxX());
                assertEquals(outputMap.getMaxY(), inputMap.getMaxY());
                assertEquals(inputMap.getLambdasNumber(), inputMap.getCollectedLambdasNumber());
                assertEquals(outputMap.getMovesUnderWater(), inputMap.getMovesUnderWater());
                assertEquals(outputMap.getScore(), inputMap.getScore());
                assertEquals(outputMap.getGameCondition(), inputMap.getGameCondition());
                assertEquals(outputMap.getAmountOfSteps(), inputMap.getAmountOfSteps());
                assertEquals(outputMap.getMaxLambdasNumber(), inputMap.getMaxLambdasNumber());
                assertEquals(outputMap.getLambdasNumber(), inputMap.getLambdasNumber());
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

        makeTestFromFormattedDirectory(8, "move stone tests", "maps/B_moveStoneTests/0_test.map");

    }

    @Test
    public void moveLambdaStones() {

        makeTestFromFormattedDirectory(2, "lambdas stone tests", "maps/C_lambdaStoneTests/0_test.map");

    }

    @Test
    public void growBeard() {
        makeTestFromFormattedDirectory(5, "grow beard tests",
                "maps/D_growBeardTests/0_test.map");
    }
    //-----------------------------------------------------------------------------------

    @Test
    public void conditionTest() {
        makeDifficultTestFromFormattedDirectory
                (3, "condtition test", "maps/E_conditionTests/0_test.map");
    }

    @Test
    public void waterLevel() {
        makeTestFromFormattedDirectory(4, "rising water level",
                "maps/H_waterTests/0_test.map");
    }

    @Test
    public void difficultTest() {
        makeDifficultTestFromFormattedDirectory(32, "difficult test", "maps/F_testsForDifficultIncidents/0_test.map");

    }

    @Test
    public void scoreTest() {
        makeTestFromFormattedDirectory(3, "score tests",
                "maps/G_scoreTests/0_test.map");
    }

    //-----------------------------------------------------------------------------------

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
