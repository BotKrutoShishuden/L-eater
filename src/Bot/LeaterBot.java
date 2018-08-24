package Bot;


import GameMap.*;
import MapObject.*;

import java.util.*;


public class LeaterBot extends MapObject {
    private GameMap mainGameMap;
    private List<SmallBot> smallBots;
    static int nobodyNotVisitedWays[][];

    //Управление отбором
    static final int MAX_GENERATION_DIGIT = 100;//TODO высчитывать относительно карты
    private final int MAX_SMALL_BOT_SIZE = 500;
    private final int MAX_SIMILAR_BOTS = 10;
    private final int MIN_REDUCED_GENERATION = 0;

    //Математика бонусов
    static final int LOCAL_START_BONUS_OF_RESEARCH = 30;
    static final int BONUS_OF_LOCAL_RESEARCH_DIVIDER = 5;
    static final int GLOBAL_BONUS_OF_RESEARCH = 100;

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


    //Контроль ненужных добавлений (типо ботов идущих в стену)
    //-----------------------------------------------------------------------------------
    private static boolean SpeciesIsAcceptable(Species species) {
        return species != Species.PORTAL_OUT && species != Species.WALL && species != Species.BEARD;
    }

    //Возвращает true, есл
    // и в радиусе 1 клетки есть хотя бы одна борода
    private static boolean UsingOfRazorIsAcceptable(GameMap gameMap, int botX, int botY) {

        for (int i = botX - 1; i < botX + 2; i++) {
            for (int j = botY - 1; j < botY + 2; j++) {
                if (gameMap.getMapObjects()[i][j].getSpecies() == Species.BEARD) return true;
            }
        }
        return false;

    }

    private boolean smartAdd(List<SmallBot> smallBots, SmallBot oldBot, NextStep nextStep) {
        if (oldBot.getGameMap().getGameCondition() == GameCondition.ABORTED ||
                oldBot.getGameMap().getGameCondition() == GameCondition.WIN)
            return false;

        int botX = oldBot.getGameMap().getBot().getX();
        int botY = oldBot.getGameMap().getBot().getY();

        GameMap oldMap = oldBot.getGameMap();
        List<NextStep> oldSteps = oldBot.getSteps();
        int oldRate = oldBot.getSurvivalRate();
        int oldBonusOfResearch[][] = oldBot.getBonusOfLocalResearch();

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
                System.out.println("АБОРТ ЭТО ГРЕХ");
                break;
        }
        return oldListSize != smallBots.size();

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
    public void calculateBonusForRareLamdasFounder(List<SmallBot> smallBots) {

    }


    //Главный метод
    //-----------------------------------------------------------------------------------
    public List<NextStep> calculateBestSteps() {
        SmallBot initBot = new SmallBot(mainGameMap);

        SmallBot bestSmallBotEver = new SmallBot(mainGameMap, initBot.getSteps(), NextStep.ABORT,
                initBot.getSurvivalRate(), initBot.getBonusOfLocalResearch());

        smartAdd(smallBots, initBot, NextStep.LEFT);
        smartAdd(smallBots, initBot, NextStep.RIGHT);
        smartAdd(smallBots, initBot, NextStep.UP);
        smartAdd(smallBots, initBot, NextStep.DOWN);
        smartAdd(smallBots, initBot, NextStep.WAIT);
        smartAdd(smallBots, initBot, NextStep.USE_RAZOR);

        List<SmallBot> copySmallBots = new ArrayList<>(smallBots);
        smallBots.clear();
        for (SmallBot smallBot : copySmallBots)
            if (!(smallBot.getGameCondition() == GameCondition.RB_DROWNED ||
                    smallBot.getGameCondition() == GameCondition.RB_CRUSHED))
                smallBots.add(smallBot);

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

            //Добавляем новое поколение без умерших и утоновшух
            for (SmallBot newSmallBot : newSmallBots)
                if (!(newSmallBot.getGameCondition() == GameCondition.RB_CRUSHED || newSmallBot.getGameCondition() == GameCondition.RB_DROWNED))
                    smallBots.add(newSmallBot);

            //TODO
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


            //TODO Удаляем похожих ботов
            if (generationDigit > MIN_REDUCED_GENERATION)
                controlOfSimilarSmallBots();


            //Удаляем худших ботов, если лист больше максимального допустимого размер
            if (smallBots.size() > MAX_SMALL_BOT_SIZE) {
                int deletedBotsDigit = smallBots.size() - MAX_SMALL_BOT_SIZE;
                for (int i = 0; i < deletedBotsDigit; i++)
                    smallBots.remove(smallBots.size() - 1);
            }


        }

        List<NextStep> bestSteps = new ArrayList<>(bestSmallBotEver.getSteps());
        if (bestSteps.get(bestSteps.size() - 1) != NextStep.ABORT && bestSmallBotEver.getGameCondition() != GameCondition.WIN)
            bestSteps.add(NextStep.ABORT);

        return bestSteps;
    }


}