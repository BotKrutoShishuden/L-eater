package GameMap;

import Bot.NextStep;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.*;

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

    }
    //-----------------------------------------------------------------------------------

    @Test
    public void cutNormalMap() {
        GameMap gameMap = GameMap.cutNormalMap("maps/beard1.map");

        assertEquals(10, gameMap.getMaxX());
        assertEquals(10, gameMap.getMaxY());
        assertEquals(15, gameMap.getGrowth());
        assertEquals(0, gameMap.getRazors());

    }
}
