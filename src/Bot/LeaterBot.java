package Bot;

import GameMap.*;
import MapObject.*;

import java.util.*;


public class LeaterBot extends MapObject {
    private GameMap gameMap;
    private List<SmallBot> smallBots;
    private int maxSmallBotSize = 1000;
    private ArrayList<NextStep> steps;

    private class SmallBot implements Comparable<SmallBot> {
        private GameMap gameMap;
        private ArrayList<NextStep> steps;

        SmallBot(GameMap oldMap, List<NextStep> oldSteps, NextStep nextStep) {
            steps = new ArrayList<>();
            steps.addAll(oldSteps);
            steps.add(nextStep);

            gameMap = oldMap.copy();
            gameMap.moveAllObjects(nextStep);
        }

        private GameMap getGameMap() {
            return gameMap;
        }

        private List<NextStep> getSteps() {
            return steps;
        }

        private int getScore() {
            return gameMap.getScore();
        }

        private GameCondition getGameCondition() {
            return gameMap.getGameCondition();
        }

        @Override
        public String toString() {
            for (NextStep nextStep : steps)
                System.out.print(nextStep.getSymbol());
            System.out.println();
            return "" + getScore();
        }

        @Override
        public int compareTo(SmallBot o) {
            if (getScore() > o.getScore())
                return 1;
            else if (getScore() < o.getScore())
                return -1;
            else return Integer.compare(getSteps().size(), o.getScore());

        }
    }

    LeaterBot(GameMap gameMap) {
        this.gameMap = gameMap;
        smallBots = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public List<NextStep> calculateBestSteps() {
        smallBots.add(new SmallBot(gameMap, steps, NextStep.LEFT));
        smallBots.add(new SmallBot(gameMap, steps, NextStep.RIGHT));
        smallBots.add(new SmallBot(gameMap, steps, NextStep.UP));
        smallBots.add(new SmallBot(gameMap, steps, NextStep.DOWN));
        smallBots.add(new SmallBot(gameMap, steps, NextStep.WAIT));

        for (SmallBot smallBot : smallBots)
            if (smallBot.getGameCondition() == GameCondition.RB_DROWNED ||
                    smallBot.getGameCondition() == GameCondition.RB_CRUSHED)
                smallBots.remove(smallBot);


        while (true) {
            List<SmallBot> newSmallBots = new ArrayList<>();
            for (SmallBot smallBot : smallBots) {
                newSmallBots.add(new SmallBot(smallBot.getGameMap(), smallBot.getSteps(), NextStep.LEFT));
                newSmallBots.add(new SmallBot(smallBot.getGameMap(), smallBot.getSteps(), NextStep.RIGHT));
                newSmallBots.add(new SmallBot(smallBot.getGameMap(), smallBot.getSteps(), NextStep.UP));
                newSmallBots.add(new SmallBot(smallBot.getGameMap(), smallBot.getSteps(), NextStep.DOWN));
                newSmallBots.add(new SmallBot(smallBot.getGameMap(), smallBot.getSteps(), NextStep.WAIT));
            }


            for (SmallBot newSmallBot : newSmallBots)
                if (!(newSmallBot.getGameCondition() == GameCondition.RB_CRUSHED || newSmallBot.getGameCondition() == GameCondition.RB_DROWNED))
                    smallBots.add(newSmallBot);
                else if (newSmallBot.getGameCondition() == GameCondition.WIN)
                    return newSmallBot.getSteps();

            Collections.sort(smallBots);
            Collections.reverse(smallBots);

            if (calculateAmountsOfBotsWithDifferentCoordinatesAndDifferentStepsDigit() < maxSmallBotSize / 10)
                reduceAmountsOfRepeatingElements(2);

            if (smallBots.size() > maxSmallBotSize) {
                int deletedElementsDigit = smallBots.size() - maxSmallBotSize;
                for (int i = 0; i < deletedElementsDigit; i++)
                    smallBots.remove(smallBots.size() - 1);

            }


//            System.out.println("Best");
//            System.out.println(smallBots.get(0).toString());
//            System.out.println("Worse");
//            System.out.println(smallBots.get(smallBots.size() - 1).toString());
//            System.out.println();


        }
    }

    private int calculateAmountsOfBotsWithDifferentCoordinatesAndDifferentStepsDigit() {
        int differentBotDigit = 0;
        SmallBot diffBot = smallBots.get(0);
        for (SmallBot smallBot : smallBots)
            if (diffBot.getScore() != smallBot.getScore() && (
                    diffBot.getGameMap().getBot().getX() != smallBot.getGameMap().getBot().getX() ||
                            diffBot.getGameMap().getBot().getY() != smallBot.getGameMap().getBot().getY())) {
                diffBot = smallBot;
                differentBotDigit++;
            }
        return differentBotDigit;

    }

    private void reduceAmountsOfRepeatingElements(int numberOfRepeatingElements) {

    }

}