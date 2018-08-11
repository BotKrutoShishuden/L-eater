package CmdSimulator;

import Bot.NextStep;
import GameMap.GameMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdSimulator {


    public static void main(String[] args) {
        try {
            String address = "maps/testsForDifficultIncidents/0_test.map";



            GameMap inputMap = GameMap.cutMapBetweenStartAndEnd(address, "is", "ie");
            inputMap.setGrowth(GameMap.cutParamAfterWord(address, "Growth "));
            inputMap.setRazors(GameMap.cutParamAfterWord(address, "Razors "));
            inputMap.setFlooding(GameMap.cutParamAfterWord(address, "Flooding "));


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String str;
            System.out.println(inputMap.toString());


            while (true) {
                System.out.println("EnterSteps\n");
                str = bufferedReader.readLine();
                if (str.equals("break"))
                    break;
                NextStep nextSteps[] = GameMap.cutStepsFromString(str);
                for (NextStep nextstep : nextSteps)
                    inputMap.moveAllObjects(nextstep);
                System.out.println(inputMap.toString() + "\n");
                System.out.println("Score = " + inputMap.getScore());
                System.out.println("Lambdas " + inputMap.getLamdasNumber() + "/" + inputMap.getMaxLambdasNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
