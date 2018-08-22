package Bot;
/*TODO добавить коэфициент выживаемости SmallBot,уваеличивающийся, при ислледовании новых клеток*/
import GameMap.*;
import MapObject.*;

import java.util.*;


public class LeaterBot extends MapObject {
    private GameMap gameMap;
    private List<SmallBot> smallBots;
    private int maxSmallBotSize = 1000;
    private SmallBot bestSmallBotEver;

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

        private void setCondtitions(SmallBot smallBot) {
            gameMap = smallBot.gameMap.copy();

            steps.clear();
            steps.addAll(smallBot.getSteps());
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

        private int getX() {
            return gameMap.getBot().getX();
        }

        private int getY() {
            return gameMap.getBot().getY();
        }

        private List<NextStep> getStepsOfRange(int from, int to) {
            List<NextStep> result = new ArrayList<>();
            for (int i = from; i < to; i++)
                result.add(steps.get(i));
            return result;

        }

        @Override
        public String toString() {
            return "Score " + getScore() + ", Steps " + getSteps().size() + "\n"
                    + toStringStepsSequence();
        }

        public String toStringStepsSequence() {
            StringBuilder stringBuilder = new StringBuilder();

            for (NextStep step : steps)
                stringBuilder.append(step.getSymbol());

            return stringBuilder.toString();
        }


        @Override
        public int compareTo(SmallBot o) {
            if (getScore() > o.getScore())
                return 1;
            else if (getScore() < o.getScore())
                return -1;
            else
                return Integer.compare(o.getSteps().size(), getSteps().size());

        }
    }

    LeaterBot(GameMap gameMap) {
        this.gameMap = gameMap;
        smallBots = new ArrayList<>();
    }

    //Контроль ненужных добавлений (типо ботов идущих в стену)
    //-----------------------------------------------------------------------------------
    private static boolean SpeciesIsAcceptable(Species species) {
        return species != Species.PORTAL_OUT && species != Species.WALL && species != Species.BEARD;
    }

    private static boolean UsingOfRazorIsAcceptable(GameMap gameMap, int botX, int botY) {

        return gameMap.getMapObjects()[botX - 1][botY].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX - 1][botY - 1].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX][botY - 1].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX + 1][botY - 1].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX + 1][botY].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX + 1][botY + 1].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX][botY + 1].getSpecies() == Species.BEARD ||
                gameMap.getMapObjects()[botX - 1][botY + 1].getSpecies() == Species.BEARD;

    }

    private boolean smartAdd(List<SmallBot> smallBots,
                             GameMap gameMap, List<NextStep> oldSteps, NextStep nextStep) {
        if (gameMap.getGameCondition() == GameCondition.ABORTED)
            return false;
        int oldListSize = smallBots.size();
        int botX = gameMap.getBot().getX(),
                botY = gameMap.getBot().getY();


        switch (nextStep) {
            case UP:
                if (SpeciesIsAcceptable(gameMap.getMapObjects()[botX][botY - 1].getSpecies()))
                    smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case DOWN:
                if (SpeciesIsAcceptable(gameMap.getMapObjects()[botX][botY + 1].getSpecies()))
                    smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case LEFT:
                if (SpeciesIsAcceptable(gameMap.getMapObjects()[botX - 1][botY].getSpecies()))
                    smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case RIGHT:
                if (SpeciesIsAcceptable(gameMap.getMapObjects()[botX + 1][botY].getSpecies()))
                    smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case WAIT:
                smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case USE_RAZOR:
                if (UsingOfRazorIsAcceptable(gameMap, botX, botY))
                    smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;
            case ABORT:
                smallBots.add(new SmallBot(gameMap, oldSteps, nextStep));
                break;

        }
        return oldListSize != smallBots.size();

    }
    //-----------------------------------------------------------------------------------

    //Контроль похожих ботов
    //List<SmallBot> должен быть предварительно отсоритрован
    //-----------------------------------------------------------------------------------
    private int calculateAmountsOfSimilarBots() {
        int differentBotDigit = 1;
        SmallBot diffBot = smallBots.get(0);
        for (SmallBot smallBot : smallBots)
            if (diffBot.compareTo(smallBot) != 0) {
                diffBot = smallBot;
                differentBotDigit++;
            }
        return differentBotDigit;

    }


    private void controlOfSimilarSmallBots(int maxSimilarBots) {
        ArrayList<SmallBot> reducedListOfSmallBots = new ArrayList<>();
        SmallBot indexBot = smallBots.get(0);
        int numberOfSavedSimilarBots = 0;
        for (SmallBot smallBot : smallBots) {
            if (indexBot.compareTo(smallBot) == 0) {
                if (numberOfSavedSimilarBots < maxSimilarBots) {
                    reducedListOfSmallBots.add(smallBot);
                    numberOfSavedSimilarBots++;
                }
            } else {
                indexBot = smallBot;
                numberOfSavedSimilarBots = 0;
            }

        }
        smallBots = reducedListOfSmallBots;

    }
    //-----------------------------------------------------------------------------------

    private int calculateAmountsOfBotsByStep(List<NextStep> stepSequence) {
        int digit = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.steps.size() >= stepSequence.size())
                if (smallBot.getStepsOfRange(0, stepSequence.size()).equals(stepSequence))
                    digit++;

        return digit;
    }

    private List<NextStep> stringToListOfSteps(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        List<NextStep> steps = new ArrayList<>();
        for (int i = 0; i < stringBuilder.length(); i++) {
            switch (stringBuilder.charAt(i)) {
                case 'R':
                    steps.add(NextStep.RIGHT);
                    break;
                case 'L':
                    steps.add(NextStep.LEFT);
                    break;
                case 'D':
                    steps.add(NextStep.DOWN);
                    break;
                case 'U':
                    steps.add(NextStep.UP);
                    break;
                case 'W':
                    steps.add(NextStep.WAIT);
                    break;
                case 'S':
                    steps.add(NextStep.USE_RAZOR);
                    break;
                default:
                    System.out.println("Case undefined");
                    break;
            }

        }
        return steps;

    }

    private int calculateOldestBot() {
        int maxStepsLength = 0;
        for (SmallBot smallBot : smallBots)
            if (maxStepsLength < smallBot.steps.size())
                maxStepsLength = smallBot.steps.size();

        return maxStepsLength;
    }

    private int calculateBotWithGenerationNumber(int number) {
        int result = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getSteps().size() == number)
                result++;

        return result;
    }

    //Главный метод
    //-----------------------------------------------------------------------------------
    public List<NextStep> calculateBestSteps() {
        bestSmallBotEver = new SmallBot(gameMap, new ArrayList<>(), NextStep.ABORT);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.LEFT);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.RIGHT);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.UP);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.DOWN);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.WAIT);
        smartAdd(smallBots, gameMap, new ArrayList<>(), NextStep.USE_RAZOR);

        for (SmallBot smallBot : smallBots)
            if (smallBot.getGameCondition() == GameCondition.RB_DROWNED ||
                    smallBot.getGameCondition() == GameCondition.RB_CRUSHED)
                smallBots.remove(smallBot);

        int generationDigit = 1;
        SmallBot bestBotInGeneration=null;
        while (generationDigit < 1000) {
            List<SmallBot> newSmallBots = new ArrayList<>();
            for (SmallBot smallBot : smallBots) {
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.ABORT);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.LEFT);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.RIGHT);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.UP);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.DOWN);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.WAIT);
                smartAdd(newSmallBots, smallBot.getGameMap(), smallBot.getSteps(), NextStep.USE_RAZOR);
            }
            for (SmallBot newSmallBot : newSmallBots)
                if (!(newSmallBot.getGameCondition() == GameCondition.RB_CRUSHED || newSmallBot.getGameCondition() == GameCondition.RB_DROWNED))
                    smallBots.add(newSmallBot);
                else if (newSmallBot.getGameCondition() == GameCondition.WIN)
                    return newSmallBot.getSteps();


            //Проблемы с compareTo у SmartBota
            try {
                Collections.sort(smallBots);
                Collections.reverse(smallBots);
            } catch (IllegalArgumentException e) {
                System.out.println("Я вылетел из-за плохого compareTo");
                SmallBot bestSmallBot = smallBots.get(0);
                for (SmallBot smallBot : smallBots) {
                    if (bestSmallBot.compareTo(smallBot) < 0)
                        bestSmallBot = smallBot;
                    System.out.println(smallBot.toString());
                }
                System.out.println("\nBestSmallBot");
                System.out.println(bestSmallBot.toString());
                return bestSmallBot.getSteps();

            }

            //Контроль похожих ботов
            if (generationDigit > 4)
                controlOfSimilarSmallBots(10);


//            if (smallBots.size() > maxSmallBotSize) {
//                int deletedBotsDigit = smallBots.size() - maxSmallBotSize;
//                for (int i = 0; i < deletedBotsDigit; i++)
//                    smallBots.remove(smallBots.size() - 1);
//            }

            generationDigit++;

            //Ищем лучшего бота
            bestBotInGeneration = smallBots.get(0);
            if (bestSmallBotEver.compareTo(bestBotInGeneration) < 0)
                bestSmallBotEver.setCondtitions(bestBotInGeneration);


            //Удаляем старое поколение
            List<SmallBot> smallBotsCopy = new ArrayList<>(smallBots);
            smallBots.clear();
            for (SmallBot copyBot : smallBotsCopy)
                if (copyBot.getSteps().size() >= generationDigit)
                    smallBots.add(copyBot);


        }
        System.out.println(bestSmallBotEver.toStringStepsSequence());
        System.out.println(bestBotInGeneration.toStringStepsSequence());
        return bestSmallBotEver.getSteps();
    }

}