package Bot;

import GameMap.*;
import MapObject.*;

import java.util.*;


final class LeaterBot {
    private GameMap mainGameMap;
    private List<SmallBot> smallBots;
    static int nobodyNotVisitedWays[][];

    //Управление отбором
    private final int MAX_GENERATION_DIGIT = 100;//TODO высчитывать относительно карты
    private final int MAX_SMALL_BOT_SIZE = 500;
    private final int MAX_SIMILAR_BOTS = 10;
    private final int MIN_REDUCED_GENERATION = 0;

    //Математика бонусов
    static final int LOCAL_START_BONUS_OF_RESEARCH = 30;
    static final int BONUS_OF_LOCAL_RESEARCH_DIVIDER = 5;
    static final int GLOBAL_BONUS_OF_RESEARCH = 100;
    static final int BONUS_FOR_RARE_LAMBDA = 10;

    //Для анализа игры
    private List<List<NextStep>> observedBotsList;
    private final boolean OBSERVING_BOTS_MODE = true;


    LeaterBot(GameMap mainGameMap) {
        this.mainGameMap = mainGameMap;
        smallBots = new ArrayList<>();
        if (OBSERVING_BOTS_MODE)
            initObservedBotsList();

        nobodyNotVisitedWays = new int[mainGameMap.getMaxX()][mainGameMap.getMaxY()];
        for (int x = 0; x < mainGameMap.getMaxX(); x++)
            for (int y = 0; y < mainGameMap.getMaxY(); y++)
                nobodyNotVisitedWays[x][y] = GLOBAL_BONUS_OF_RESEARCH;


    }


    //Для наблюдения за конкретными последовательностиями шагов на карте на разных этапах
    //-----------------------------------------------------------------------------------
    private void initObservedBotsList() {
        observedBotsList = new ArrayList<>();
        observedBotsList.add(stringToListOfSteps("WRRULUUWDWULUUUUUUUUUUURRDDDDDDDDDDLULDDDDDLA"));
        observedBotsList.add(stringToListOfSteps("DDLWWWWWWWWWWRUUURRDDDRLUUUUUUUUUUUUUULLL"));
    }

    //Перевод строки с шагами в List этих шагов
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
                case 'A':
                    steps.add(NextStep.ABORT);
                    break;
                default:
                    System.out.println("Case undefined in stringToListOfSteps");
                    break;
            }

        }
        return steps;

    }


    //Методы для анализирования в процесса отладки(ужасная грязь)
    //-----------------------------------------------------------------------------------
    //Возвращает количество ботов с указанной последовательностью шагов
    private int calculateAmountsOfBotsByStep(List<NextStep> stepSequence) {
        int digit = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getSteps().size() >= stepSequence.size())
                if (smallBot.copyListStepsOfRange(0, stepSequence.size()).equals(stepSequence))
                    digit++;

        return digit;
    }


    //Считает количество ботов из поколения
    private int calculateBotWithGenerationNumber(int generationNumber) {
        int result = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getSteps().size() == generationNumber)
                result++;

        return result;
    }

    private int numberOfWinnersBots() {
        int i = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getGameCondition() == GameCondition.WIN)
                i++;
        return i;
    }

    //Полезные
    private SmallBot foundbotWithSteps(List<NextStep> steps, int generation) {
        steps = headList(steps, generation);

        for (SmallBot smallBot : smallBots) {
            if (smallBot.getSteps().equals(steps))
                return smallBot;

        }
        return null;
    }

    private int botWithStepsIndex(List<NextStep> steps, int generation) {
        steps = headList(steps, generation);
        int i = 0;
        for (SmallBot smallBot : smallBots) {
            if (smallBot.getSteps().equals(steps))
                return i;
            i++;
        }
        return -1;

    }

    private int amountOfBotsBySteps(List<NextStep> steps, int generation) {
        steps = headList(steps, generation);
        int number = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getSteps().equals(steps))
                number++;

        return number;

    }

    private List<NextStep> headList(List<NextStep> list, int to) {
        List<NextStep> copyList = new ArrayList<>(list);
        List<NextStep> result = new ArrayList<>();
        int i = 0;
        for (NextStep nextStep : copyList) {
            result.add(nextStep);
            i++;
            if (i == to)
                return result;

        }
        return result;

    }


    //Контроль ненужных добавлений (типа ботов идущих в стену или проигравших ботов)
    //-----------------------------------------------------------------------------------
    private static boolean SpeciesIsAcceptable(Species species) {
        return species != Species.PORTAL_OUT && species != Species.WALL && species != Species.BEARD;
    }

    //Возвращает true, есл
    // и в радиусе 1 клетки есть хотя бы одна борода
    private static boolean UsingOfRazorIsAcceptable(GameMap gameMap, int botX, int botY) {

        for (int i = botX - 1; i < botX + 2; i++)
            for (int j = botY - 1; j < botY + 2; j++)
                if (gameMap.getMapObjects()[i][j].getSpecies() == Species.BEARD)
                    return true;
        return false;
    }

    private void smartAdd(List<SmallBot> smallBots, SmallBot oldBot, NextStep nextStep) {
        if (oldBot.getGameMap().getGameCondition() == GameCondition.ABORTED ||
                oldBot.getGameMap().getGameCondition() == GameCondition.WIN ||
                oldBot.getGameMap().getGameCondition() == GameCondition.RB_DROWNED)
            return;

        int botX = oldBot.getGameMap().getBot().getX();
        int botY = oldBot.getGameMap().getBot().getY();

        GameMap oldMap = oldBot.getGameMap();
        List<NextStep> oldSteps = oldBot.getSteps();
        int oldRate = oldBot.getSurvivalRate();
        int oldBonusOfResearch[][] = oldBot.getBonusOfLocalResearch();

        SmallBot smallBot;

        switch (nextStep) {
            case UP:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX][botY - 1].getSpecies())) {
                    smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case DOWN:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX][botY + 1].getSpecies())) {
                    smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case LEFT:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX - 1][botY].getSpecies())) {
                    smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case RIGHT:
                if (SpeciesIsAcceptable(oldMap.getMapObjects()[botX + 1][botY].getSpecies())) {
                    smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case WAIT:
                smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                    smallBots.add(smallBot);
                break;
            case USE_RAZOR:
                if (UsingOfRazorIsAcceptable(oldMap, botX, botY)) {
                    smallBot = new SmallBot(oldMap, oldSteps, nextStep, oldRate, oldBonusOfResearch);
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case ABORT:
                System.out.println("АБОРТ ЭТО ГРЕХ");
                break;
        }


    }

    //Контроль похожих ботов
    //-----------------------------------------------------------------------------------
    //Возвращает количество разных ботов в smallBots
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

    //TODO очень опасное дерьмо в данный момент, надо попробовать это нормально реализовать
    //TODO//List<SmallBot> должен быть предварительно отсоритрован
    private void controlOfSimilarSmallBots() {
//        ArrayList<SmallBot> copySmallBots = new ArrayList<>(smallBots);
//        smallBots.clear();
//
//        SmallBot indexBot = copySmallBots.get(0);
//        int numberOfSavedSimilarBots = 0;
//        for (int i = 0; i < copySmallBots.size(); i++)
//            if (indexBot.compareTo(copySmallBots.get(i)) == 0) {
//                if (numberOfSavedSimilarBots < MAX_SIMILAR_BOTS) {
//                    smallBots.add(copySmallBots.get(i));
//                    numberOfSavedSimilarBots++;
//                }
//            } else {
//                indexBot = copySmallBots.get(i);
//                numberOfSavedSimilarBots = 0;
//                i--;
//            }
//

    }

    //TODO Высчитывает редкость каждой лямбды относительно текущего поколения
    //TODO выживаемость бота должна зависеть не только от количества лямбд
    //TODO но и от их редкости (при хорошей реализации бот должен пройти contest 6)
    private void calculateBonusForRareLamdasFounder(List<SmallBot> smallBots) {
        int[] bonusForLambdas = new int[mainGameMap.getLambdas().size()];
        int[] numberOfPickups = new int[mainGameMap.getLambdas().size()];
        for (SmallBot smallBot : smallBots) {
            int i = 0;
            for (boolean lambda : smallBot.getCollectedLambdasList()) {
                if (lambda)
                    numberOfPickups[i]++;
                else
                    bonusForLambdas[i] += BONUS_FOR_RARE_LAMBDA;
                i++;
            }
        }

        //TODO не использовать магические числа, привязать все к полям класса
        for (SmallBot smallBot : smallBots) {
            int i = 0;
            for (boolean lambda : smallBot.getCollectedLambdasList()) {
                if (lambda && numberOfPickups[i] < smallBots.size() / 100)
                    smallBot.addSurvivalRate(bonusForLambdas[i] * 50);
                else if (lambda && numberOfPickups[i] > smallBots.size() * 0.75)
                    smallBot.addSurvivalRate(bonusForLambdas[i] / 2);
                else
                    smallBot.addSurvivalRate(bonusForLambdas[i]);
                i++;
            }
        }
    }


    //Главный метод
    //-----------------------------------------------------------------------------------
    List<NextStep> calculateBestSteps() {
        SmallBot initBot = new SmallBot(mainGameMap);

        SmallBot bestSmallBotEver = new SmallBot(mainGameMap, initBot.getSteps(), NextStep.ABORT,
                initBot.getSurvivalRate(), initBot.getBonusOfLocalResearch());

        smartAdd(smallBots, initBot, NextStep.LEFT);
        smartAdd(smallBots, initBot, NextStep.RIGHT);
        smartAdd(smallBots, initBot, NextStep.UP);
        smartAdd(smallBots, initBot, NextStep.DOWN);
        smartAdd(smallBots, initBot, NextStep.WAIT);
        smartAdd(smallBots, initBot, NextStep.USE_RAZOR);


        int generationDigit = 1;

        while (generationDigit < MAX_GENERATION_DIGIT) {

            List<SmallBot> newSmallBots = new ArrayList<>();

            for (SmallBot smallBot : smallBots) {
                smartAdd(newSmallBots, smallBot, NextStep.LEFT);
                smartAdd(newSmallBots, smallBot, NextStep.RIGHT);
                smartAdd(newSmallBots, smallBot, NextStep.UP);
                smartAdd(newSmallBots, smallBot, NextStep.DOWN);
                smartAdd(newSmallBots, smallBot, NextStep.WAIT);
                smartAdd(newSmallBots, smallBot, NextStep.USE_RAZOR);
            }

            smallBots.addAll(newSmallBots);

            calculateBonusForRareLamdasFounder(smallBots);


            //Сортируем ботов,чтобы в начале листа лежали лучшие
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

            generationDigit++;

            //Ищем лучшего бота по очкам среди всех ботов
            for (SmallBot smallBot : smallBots)
                if (smallBot.getScore() >= bestSmallBotEver.getScore())
                    bestSmallBotEver.setConditions(smallBot);

            //Удаляем старое поколение
            List<SmallBot> smallBotsCopy = new ArrayList<>(smallBots);
            smallBots.clear();
            for (SmallBot copyBot : smallBotsCopy)
                if (copyBot.getSteps().size() >= generationDigit)
                    smallBots.add(copyBot);
            smallBotsCopy.clear();


            //TODO Метод не реализован
            if (generationDigit > MIN_REDUCED_GENERATION)
                controlOfSimilarSmallBots();


            //Удаляем худших ботов, если лист больше максимально допустимого размера
            if (smallBots.size() > MAX_SMALL_BOT_SIZE) {
                int deletedBotsDigit = smallBots.size() - MAX_SMALL_BOT_SIZE;
                for (int i = 0; i < deletedBotsDigit; i++)
                    smallBots.remove(smallBots.size() - 1);
            }

        }

        List<NextStep> bestSteps = new ArrayList<>(bestSmallBotEver.getSteps());
        if (bestSteps.get(bestSteps.size() - 1) != NextStep.ABORT || bestSmallBotEver.getGameCondition() != GameCondition.WIN)
            bestSteps.add(NextStep.ABORT);

        System.out.println("Flooding = " + bestSmallBotEver.getGameMap().getFlooding());
        System.out.println("Water level = " + bestSmallBotEver.getGameMap().getWaterLevel());
        System.out.println("Max moves under water = " + bestSmallBotEver.getGameMap().getMaxMovesUnderWater());
        System.out.println("Moves under water = " + bestSmallBotEver.getGameMap().getMovesUnderWater());
        return bestSteps;
    }


}