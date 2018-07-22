package GameMap;

import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static Bot.NextStep.LEFT;
import static Bot.NextStep.RIGHT;
import static org.junit.Assert.*;

public class GameMapTest {
    private final int mapsDigit = 5;
    private GameMap gameMaps[] = new GameMap[mapsDigit];


    @Before
    public void buildMaps() {
        String address = "..\\L-eater\\maps\\0_test.map";
        gameMaps[0] = new GameMap(address);
        for (int i = 1; i < mapsDigit; i++) {
            address = address.replace(i - 1 + "", i + "");
            gameMaps[i] = new GameMap(address);
        }

    }


    @Test
    public void constructor() {
        assertEquals(true, gameMaps[0].getMaxX() == 7);
        assertEquals(true, gameMaps[0].getMaxY() == 4);

    }

    private void leftStep() {
        int numberOfErrorTest = 0;
        try {

            //0----------------------------------------------------------------------------------
            assertEquals(
                    "#######\n" +
                            "# *R* #\n" +
                            "#     #\n" +
                            "#######\n", gameMaps[0].toString());

            gameMaps[0].moveAllObjects(LEFT);

            assertEquals(
                    "#######\n" +
                            "# R * #\n" +
                            "#*    #\n" +
                            "#######\n", gameMaps[0].toString());

            numberOfErrorTest++;

            //1----------------------------------------------------------------------------------
            assertEquals(
                    "#######\n" +
                            "# *R* #\n" +
                            "#.   ##\n" +
                            "#######\n", gameMaps[1].toString());

            gameMaps[1].moveAllObjects(LEFT);

            assertEquals(
                    "#######\n" +
                            "#*R * #\n" +
                            "#.   ##\n" +
                            "#######\n", gameMaps[1].toString());


            numberOfErrorTest++;
            //2----------------------------------------------------------------------------------
            assertEquals(
                    "#####\n" +
                            "#*R*#\n" +
                            "#   #\n" +
                            "#####\n", gameMaps[2].toString());

            gameMaps[2].moveAllObjects(LEFT);

            assertEquals(
                    "#####\n" +
                            "#*R*#\n" +
                            "#   #\n" +
                            "#####\n", gameMaps[2].toString());

            numberOfErrorTest++;

            //3----------------------------------------------------------------------------------
            assertEquals(
                    "###\n" +
                            "#R#\n" +
                            "# #\n" +
                            "###\n", gameMaps[3].toString());

            gameMaps[3].moveAllObjects(LEFT);

            assertEquals(
                    "###\n" +
                            "#R#\n" +
                            "# #\n" +
                            "###\n", gameMaps[3].toString());


            numberOfErrorTest++;

            //4----------------------------------------------------------------------------------
            assertEquals(
                    "#########\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#   R   #\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#########\n", gameMaps[4].toString());


            gameMaps[4].moveAllObjects(LEFT);


            assertEquals(
                    "#########\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#  R    #\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#########\n", gameMaps[4].toString());

            gameMaps[4].moveAllObjects(LEFT);
            gameMaps[4].moveAllObjects(LEFT);

            assertEquals(
                    "#########\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#R      #\n" +
                            "#       #\n" +
                            "#       #\n" +
                            "#########\n", gameMaps[4].toString());


        } catch (ComparisonFailure e) {
            System.out.println("Error in left step\nTest number = " + numberOfErrorTest);
            throw e;
        }
    }

    private void rightStep() {
        try {


            assertEquals(
                    "#######\n" +
                            "# *R* #\n" +
                            "#     #\n" +
                            "#######\n", gameMaps[0].toString());

            gameMaps[0].moveAllObjects(RIGHT);

            assertEquals(
                    "#######\n" +
                            "# * R #\n" +
                            "#    *#\n" +
                            "#######\n", gameMaps[0].toString());


            assertEquals(
                    "#######\n" +
                            "# *R* #\n" +
                            "#.   ##\n" +
                            "#######\n", gameMaps[1].toString());

            gameMaps[1].moveAllObjects(RIGHT);

            assertEquals(
                    "#######\n" +
                            "# * R*#\n" +
                            "#.   ##\n" +
                            "#######\n", gameMaps[1].toString());


            assertEquals(
                    "#####\n" +
                            "#*R*#\n" +
                            "#   #\n" +
                            "#####", gameMaps[2].toString());

            gameMaps[2].moveAllObjects(RIGHT);

            assertEquals(
                    "#####\n" +
                            "#*R*#\n" +
                            "#   #\n" +
                            "#####", gameMaps[2].toString());

            assertEquals(
                    "###\n" +
                            "#R#\n" +
                            "# #\n" +
                            "###\n", gameMaps[3].toString());

            gameMaps[2].moveAllObjects(RIGHT);

            assertEquals(
                    "###\n" +
                            "#R#\n" +
                            "# #\n" +
                            "###\n", gameMaps[3].toString());


        } catch (ComparisonFailure e) {
            System.out.println("Error in left step");
            throw e;
        }

    }

    @Test
    public void moveAllObjects() {
        leftStep();


    }
}