package GameMap;

import Bot.NextStep;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.*;

import static GameMap.GameCondition.*;
import static org.junit.Assert.*;

public class GameMapTest {


    private void makeTestFromFormatedFile(String address) {
        try {


            GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");

            NextStep nextSteps[] = GameMap.cutSteps(address);

            for (NextStep nextStep : nextSteps)
                inputMap.moveAllObjects(nextStep);

            GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");

            assertEquals(outputMap.toString(), inputMap.toString());

        } catch (IOException e) {
            System.out.println("IOOOOO");
        } catch (NullPointerException e) {
            System.out.println("Error in file = " + address);
        }
    }


    //-----------------------------------------------------------------------------------

    private void stepTests(int testNumber, String testName, String address) {
        int numberOfErrorTest = 0;


        try {

            for (int i = 0; i < testNumber; ) {
                makeTestFromFormatedFile(address);
                numberOfErrorTest++;
                i++;
                address = address.replace("" + (i - 1), "" + i);
            }

        } catch (ComparisonFailure e) {
            System.out.println("Error in " + testName + "\nTest number = " + numberOfErrorTest);
            throw e;
        }
    }

    @Test
    public void moveAllObjects() {
        stepTests(5, "left step tests", "maps/testsForMoveLeft/0_test.map");

        stepTests(5, "right step tests", "maps/testsForMoveRight/0_test.map");

        stepTests(5, "down step tests", "maps/testsForMoveDown/0_test.map");

        stepTests(5, "up step tests", "maps/testsForMoveUp/0_test.map");

        stepTests(5, "stay step & stones tests", "maps/testsForMoveStay/0_test.map");

    }
    //-----------------------------------------------------------------------------------

    @Test
    public void difficultTest() {
        try {
            String address = "maps/testsForDifficultIncidents/0_test.map";

            GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");
            inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
            inputMap.setRazors(GameMap.cutParamAfterWord(address, "Razors "));
            inputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding "));


            NextStep nextSteps[] = GameMap.cutSteps(address);

            for (NextStep nextStep : nextSteps)
                inputMap.moveAllObjects(nextStep);

            GameMap outputMap = GameMap.cutMapBetweenStartAndEnd(address, "os", "oe");
            outputMap.setGrowth(GameMap.cutParamAfterWord(address, "F_Growth "));
            outputMap.setRazors(GameMap.cutParamAfterWord(address, "F_Razors "));
            outputMap.setFlooding(GameMap.cutParamAfterWord(address, "F_Flooding "));
            outputMap.setScore(GameMap.cutParamAfterWord(address, "F_Score "));
            outputMap.setAmountOfSteps(GameMap.cutParamAfterWord(address, "F_Moves "));
            outputMap.setLamdasNumber(GameMap.cutParamAfterWord(address, "F_Lambda "));
            outputMap.setMaxLambdasNumber(GameMap.cutParamAfterWord(address, "F_LambdaMax "));
            outputMap.setWaterLevel(GameMap.cutParamAfterWord(address, "F_WaterLevel "));
            outputMap.setGameCondition(RB_DROWNED);

            assertEquals(inputMap.getGrowth(), outputMap.getGrowth());
            assertEquals(inputMap.getRazors(), outputMap.getRazors());
            assertEquals(inputMap.getFlooding(), outputMap.getFlooding());
            assertEquals(inputMap.getMaxX(), outputMap.getMaxX());
            assertEquals(inputMap.getMaxY(), outputMap.getMaxY());
            //            System.out.println("Input\n");
            //            System.out.println(inputMap.toString());
            //            System.out.println("Output\n");
            //            System.out.println(outputMap.toString());
            //            for (int x = 0; x < inputMap.getMaxX(); x++)
            //                for (int y = 0; y < inputMap.getMaxY(); y++)
            //                    assertEquals(inputMap.getMapObjects()[x][y], outputMap.getMapObjects()[x][y]);


            //assertEquals(inputMap.getScore(), outputMap.getScore());
            assertEquals(inputMap.getAmountOfSteps(), outputMap.getAmountOfSteps());
            //assertEquals(inputMap.getLamdasNumber(), outputMap.getLamdasNumber());
            //assertEquals(inputMap.getMaxLambdasNumber(), outputMap.getMaxLambdasNumber());
            //assertEquals(inputMap.getWaterLevel(), outputMap.getWaterLevel());


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
