package Bot;
/*TODO добавить коэфициент выживаемости SmallBot,уваеличивающийся, при ислледовании новых клеток*/

import GameMap.*;
import MapObject.*;

import java.util.*;


public class LeaterBot extends MapObject {
    private GameMap mainGameMap;
    private List<SmallBot> smallBots;

    static final int MAX_GENERATION_DIGIT = 200;
    static final int START_BONUS_OF_RESEARCH = 10;
    static final int BONUS_OF_RESEARCH_DIVIDER = 2;
    private static final int MAX_SMALL_BOT_SIZE = 1000;


    LeaterBot(GameMap mainGameMap) {
        this.mainGameMap = mainGameMap;
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

    private boolean smartAdd(List<SmallBot> smallBots, SmallBot oldBot, NextStep nextStep) {
        if (oldBot.getGameMap().getGameCondition() == GameCondition.ABORTED)
            return false;

        int botX = oldBot.getGameMap().getBot().getX();
        int botY = oldBot.getGameMap().getBot().getY();

        GameMap oldMap = oldBot.getGameMap();
        List<NextStep> oldSteps = oldBot.getSteps();
        int oldRate = oldBot.getSurvivalRate();
        int oldBonusOfResearch[][] = oldBot.getBonusOfResearch();

        int oldListSize = smallBots.size();

        switch (nextStep) {
            case UP:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX][botY - 1].getSpecies()))
                    smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case DOWN:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX][botY + 1].getSpecies()))
                    smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case LEFT:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX - 1][botY].getSpecies()))
                    smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case RIGHT:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX + 1][botY].getSpecies()))
                    smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case WAIT:
                smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case USE_RAZOR:
                if (UsingOfRazorIsAcceptable(oldMap, botX, botY))
                    smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
                break;
            case ABORT:
                smallBots.add(new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch));
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
            if (smallBot.getSteps().size() >= stepSequence.size())
                if (smallBot.copyListStepsOfRange(0, stepSequence.size()).equals(stepSequence))
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
            if (maxStepsLength < smallBot.getSteps().size())
                maxStepsLength = smallBot.getSteps().size();

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
        SmallBot initBot = new SmallBot(mainGameMap);

        SmallBot bestSmallBotEver = new SmallBot(mainGameMap, initBot.getSteps(), NextStep.ABORT,
                initBot.getSurvivalRate(), initBot.getBonusOfResearch());

        smartAdd(smallBots, initBot, NextStep.LEFT);
        smartAdd(smallBots, initBot, NextStep.RIGHT);
        smartAdd(smallBots, initBot, NextStep.UP);
        smartAdd(smallBots, initBot, NextStep.DOWN);
        smartAdd(smallBots, initBot, NextStep.WAIT);
        smartAdd(smallBots, initBot, NextStep.USE_RAZOR);

        for (SmallBot smallBot : smallBots)
            if (smallBot.getGameCondition() == GameCondition.RB_DROWNED ||
                    smallBot.getGameCondition() == GameCondition.RB_CRUSHED)
                smallBots.remove(smallBot);

        int generationDigit = 1;

        while (generationDigit < MAX_GENERATION_DIGIT) {
            List<SmallBot> newSmallBots = new ArrayList<>();
            for (SmallBot smallBot : smallBots) {
                smartAdd(newSmallBots, smallBot, NextStep.ABORT);
                smartAdd(newSmallBots, smallBot, NextStep.LEFT);
                smartAdd(newSmallBots, smallBot, NextStep.RIGHT);
                smartAdd(newSmallBots, smallBot, NextStep.UP);
                smartAdd(newSmallBots, smallBot, NextStep.DOWN);
                smartAdd(newSmallBots, smallBot, NextStep.WAIT);
                smartAdd(newSmallBots, smallBot, NextStep.USE_RAZOR);
            }

            for (SmallBot newSmallBot : newSmallBots)
                if (!(newSmallBot.getGameCondition() == GameCondition.RB_CRUSHED || newSmallBot.getGameCondition() == GameCondition.RB_DROWNED))
                    smallBots.add(newSmallBot);


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


            generationDigit++;

            //Ищем лучшего бота по очкам
            for (SmallBot smallBot : smallBots)
                if (smallBot.getScore() > bestSmallBotEver.getScore())
                    bestSmallBotEver.setConditions(smallBot);


            //Удаляем старое поколение
            List<SmallBot> smallBotsCopy = new ArrayList<>(smallBots);
            smallBots.clear();
            for (SmallBot copyBot : smallBotsCopy)
                if (copyBot.getSteps().size() >= generationDigit)
                    smallBots.add(copyBot);

            if (smallBots.size() > MAX_SMALL_BOT_SIZE) {
                int deletedBotsDigit = smallBots.size() - MAX_SMALL_BOT_SIZE;
                for (int i = 0; i < deletedBotsDigit; i++)
                    smallBots.remove(smallBots.size() - 1);
            }


        }
        return bestSmallBotEver.getSteps();
    }


}