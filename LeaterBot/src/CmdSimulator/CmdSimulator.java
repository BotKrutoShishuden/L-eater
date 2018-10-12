package CmdSimulator;

import Bot.NextStep;
import GameMap.GameMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdSimulator {


    public static void main(String[] args) {
        try {
            String address = "maps/beard1.map";


            GameMap inputMap = GameMap.cutNormalMap(address);


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
                System.out.println("Game Condition " + inputMap.getGameCondition());
                System.out.println("Score = " + inputMap.getScore());
                System.out.println("Lambdas " + inputMap.getLambdasNumber() + "/" + inputMap.getMaxLambdasNumber());
                System.out.println("Lambda list " + inputMap.getLambdas());
                System.out.println("Flooding " + inputMap.getFlooding());
                System.out.println("Water level " + inputMap.getWaterLevel());
                System.out.println("Moves under water " + inputMap.getMovesUnderWater());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
