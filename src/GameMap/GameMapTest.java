package GameMap;

import Bot.NextStep;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class GameMapTest {


    private void makeTestFromFormatedFile(String address) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(address));

            GameMap inputMap = GameMap.cutMap(bufferedReader, 'i', 'i');

            NextStep nextSteps[] = GameMap.cutSteps(bufferedReader);

            for (NextStep nextStep : nextSteps)
                inputMap.moveAllObjects(nextStep);

            GameMap outputMap = GameMap.cutMap(bufferedReader, 'o', 'o');
            bufferedReader.close();

            assertEquals(outputMap.toString(), inputMap.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test//TODO
    public void constructor() {

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

}
