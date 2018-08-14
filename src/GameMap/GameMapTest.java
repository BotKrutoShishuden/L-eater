package GameMap;

import Bot.NextStep;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.*;

import static GameMap.GameCondition.*;
import static org.junit.Assert.*;

public class GameMapTest {

    //Методы для автоматизации и облегчения тестирования
    private void makeTestFromFormattedFile(String address) throws IOException, NullPointerException {


        GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");

        NextStep nextSteps[] = GameMap.cutSteps(address);

        inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
        inputMap.setRazors(GameMap.cutParamAfterWord(address, "Razors "));
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

        makeTestFromFormattedDirectory(5, "stay step & stones tests", "maps/B_moveStoneTests/0_test.map");

    }

    @Test
    public void growBeard() {
        makeTestFromFormattedDirectory(5, "grow simple beard, obstacles and cutting", "maps/growBeardTests/0_test.map");
    }

    //-----------------------------------------------------------------------------------

    @Test
    public void moveAllObjects() {
        try {

            String address = "maps/testsForDifficultIncidents/0_test.map";
            int testNumber = 4;
            for (int i = 0; i < testNumber; ) {

                GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");
                inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
                inputMap.setRazors(GameMap.cutParamAfterWord(address, "Razors "));
                inputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding "));

                NextStep nextSteps[] = GameMap.cutSteps(address);

                //TestingOf GameMap.moveToLastCondition()
                for (NextStep nextStep : nextSteps)
                    inputMap.moveAllObjects(nextStep);

                for (NextStep nextStep1 : nextSteps)
                    inputMap.moveAllObjects(NextStep.BACK);

                for (NextStep nextStep : nextSteps)
                    inputMap.moveAllObjects(nextStep);
                //TestingOf GameMap.moveToLastCondition()


                GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");
                outputMap.setGrowth(GameMap.cutParamAfterWord(address, "F_Growth "));
                outputMap.setRazors(GameMap.cutParamAfterWord(address, "F_Razors "));
                outputMap.setFlooding(GameMap.cutParamAfterWord(address, "F_Flooding "));
                outputMap.setScore(GameMap.cutParamAfterWord(address, "F_Score "));
                outputMap.setAmountOfSteps(GameMap.cutParamAfterWord(address, "F_Moves "));
                outputMap.setLamdasNumber(GameMap.cutParamAfterWord(address, "F_Lambda "));
                outputMap.setMaxLambdasNumber(GameMap.cutParamAfterWord(address, "F_LambdaMax "));
                outputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "F_WaterLevel "));
                outputMap.setGameCondition(GameMap.cutConditionAfterWord(address, "F_GameCondition "));

                assertEquals(outputMap.getGrowth(), inputMap.getGrowth());
                assertEquals(outputMap.getRazors(), inputMap.getRazors());
                assertEquals(outputMap.getFlooding(), inputMap.getFlooding());
                assertEquals(outputMap.getMaxX(), inputMap.getMaxX());
                assertEquals(outputMap.getMaxY(), inputMap.getMaxY());
                assertEquals(outputMap.getScore(), inputMap.getScore());
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
        }
    }

    @Test
    public void cutNormalMap() {
        GameMap gameMap = GameMap.cutNormalMap("maps/beard1.map");

        assertEquals(10, gameMap.getMaxX());
        assertEquals(10, gameMap.getMaxY());
        assertEquals(15, gameMap.getGrowth());
        assertEquals(0, gameMap.getRazors());

    }
}
