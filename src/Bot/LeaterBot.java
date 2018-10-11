package Bot;

import GameMap.*;
import MapObject.*;

import java.util.*;


public final class LeaterBot implements Runnable {
    private GameMap mainGameMap;
    private List<SmallBot> smallBots;
    static int nobodyNotVisitedWays[][];

    private List<NextStep> bestStepsList;
    private String bestStepsString = "A";

    //Управление отбором
    private final int MAX_GENERATION_DIGIT = 10000;
    private final int MAX_SMALL_BOT_SIZE = 500;

    //Математика бонусов
    static final int LOCAL_START_BONUS_OF_RESEARCH = 30;
    static final int BONUS_OF_LOCAL_RESEARCH_DIVIDER = 5;
    static final int GLOBAL_BONUS_OF_RESEARCH = 100;
    static final int BONUS_FOR_RARE_LAMBDA = 10;

    //Для анализа игры
    private final boolean OBSERVING_BOTS_MODE = true;
    private List<List<NextStep>> observedStepsSequences;

    public LeaterBot(GameMap mainGameMap) {
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
        observedStepsSequences = new ArrayList<>();
        observedStepsSequences.add(stringToListOfSteps("DRDRDRDRRLUUURUULURDDDLLLLLUUA"));
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
    //Считает количество ботов из поколения
    private int obsCalculateBotWithGenerationNumber(int generationNumber) {
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
    private SmallBot obsFindBotWithSteps(List<NextStep> steps, int generation) {
        steps = headListOfSteps(steps, generation);

        for (SmallBot smallBot : smallBots) {
            if (smallBot.getSteps().equals(steps))
                return smallBot;

        }
        return null;
    }

    private int obsBotWithStepsIndexInSmallBots(List<NextStep> steps, int generation) {
        steps = headListOfSteps(steps, generation);
        int i = 0;
        for (SmallBot smallBot : smallBots) {
            if (smallBot.getSteps().equals(steps))
                return i;
            i++;
        }
        return -1;

    }

    private int obsAmountOfBotsBySteps(List<NextStep> steps, int generation) {
        steps = headListOfSteps(steps, generation);
        int number = 0;
        for (SmallBot smallBot : smallBots)
            if (smallBot.getSteps().equals(steps))
                number++;

        return number;

    }

    private List<NextStep> headListOfSteps(List<NextStep> list, int to) {
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


    /*Контроль ненужных добавлений (типо ботов идущих в стену или проигравших ботов)
    -----------------------------------------------------------------------------------*/
    //Проверка, что объект в который идет является проходимым
    private boolean speciesIsAcceptable(Species species) {
        return species != Species.PORTAL_OUT && species != Species.WALL && species != Species.BEARD;
    }

    //Возвращает true, еслии в радиусе 1 клетки есть хотя бы одна борода
    private boolean usingOfRazorIsAcceptable(GameMap gameMap, int botX, int botY) {

        for (int i = botX - 1; i < botX + 2; i++)
            for (int j = botY - 1; j < botY + 2; j++)
                if (gameMap.getMapObjects()[i][j].getSpecies() == Species.BEARD)
                    return true;
        return false;
    }

    private void smartAdd(List<SmallBot> smallBots, SmallBot oldBot, NextStep nextStep, int generationDigit) {
        if (oldBot.getGameMap().getGameCondition() == GameCondition.ABORTED ||
                oldBot.getGameMap().getGameCondition() == GameCondition.WIN ||
                oldBot.getGameMap().getGameCondition() == GameCondition.RB_DROWNED)
            return;


        SmallBot smallBot;

        switch (nextStep) {
            case UP:
                if (speciesIsAcceptable(oldBot.getGameMap().getMapObjects()[oldBot.getX()][oldBot.getY() - 1].getSpecies()) && oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case DOWN:
                if (speciesIsAcceptable(oldBot.getGameMap().getMapObjects()[oldBot.getX()][oldBot.getY() + 1].getSpecies()) && oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case LEFT:
                if (speciesIsAcceptable(oldBot.getGameMap().getMapObjects()[oldBot.getX() - 1][oldBot.getY()].getSpecies()) && oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case RIGHT:
                if (speciesIsAcceptable(oldBot.getGameMap().getMapObjects()[oldBot.getX() + 1][oldBot.getY()].getSpecies()) && oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case WAIT:
                if (oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case USE_RAZOR:
                if (usingOfRazorIsAcceptable(oldBot.getGameMap(), oldBot.getX(), oldBot.getY()) && oldBot.getSteps().size() + 1 == generationDigit) {
                    smallBot = new SmallBot(oldBot.getGameMap(), oldBot.getSteps(), nextStep, oldBot.getSurvivalRate(), oldBot.getBonusOfLocalResearch());
                    if (smallBot.getGameCondition() != GameCondition.RB_DROWNED && smallBot.getGameCondition() != GameCondition.RB_CRUSHED)
                        smallBots.add(smallBot);
                }
                break;
            case ABORT:
                break;
        }


    }

    /*Контроль похожих ботов
    -----------------------------------------------------------------------------------
    Высчитывает редкость каждой лямбды относительно текущего поколения
    выживаемость бота должна зависеть не только от количества лямбд
    но и от их редкости (при хорошей реализации бот должен пройти contest 6)*/
    private void calculateBonusForRareLamdasFounder(List<SmallBot> smallBots) {
        int[] bonusForLambdas = new int[mainGameMap.getLambdas().size()];

        for (SmallBot smallBot : smallBots) {
            int i = 0;
            for (boolean lambda : smallBot.getCollectedLambdas()) {
                if (!lambda)
                    bonusForLambdas[i] += BONUS_FOR_RARE_LAMBDA;
                i++;
            }
        }

        for (SmallBot smallBot : smallBots) {
            int i = 0;
            for (boolean lambda : smallBot.getCollectedLambdas()) {
                if (lambda)
                    smallBot.addBonusForRareLambda(bonusForLambdas[i]);
                i++;
            }
        }
    }


    //Главный метод
    //-----------------------------------------------------------------------------------
    private void calculateBestSteps() {
        SmallBot initBot = new SmallBot(mainGameMap);

        SmallBot bestSmallBotEver = new SmallBot(mainGameMap, initBot.getSteps(), NextStep.ABORT,
                initBot.getSurvivalRate(), initBot.getBonusOfLocalResearch());


        int generationDigit = 1;

        smartAdd(smallBots, initBot, NextStep.LEFT, generationDigit);
        smartAdd(smallBots, initBot, NextStep.RIGHT, generationDigit);
        smartAdd(smallBots, initBot, NextStep.UP, generationDigit);
        smartAdd(smallBots, initBot, NextStep.DOWN, generationDigit);
        smartAdd(smallBots, initBot, NextStep.WAIT, generationDigit);
        smartAdd(smallBots, initBot, NextStep.USE_RAZOR, generationDigit);


        while (generationDigit < MAX_GENERATION_DIGIT) {

            List<SmallBot> newSmallBots = new ArrayList<>();

            generationDigit++;

            for (SmallBot smallBot : smallBots) {
                smartAdd(newSmallBots, smallBot, NextStep.LEFT, generationDigit);
                smartAdd(newSmallBots, smallBot, NextStep.RIGHT, generationDigit);
                smartAdd(newSmallBots, smallBot, NextStep.UP, generationDigit);
                smartAdd(newSmallBots, smallBot, NextStep.DOWN, generationDigit);
                smartAdd(newSmallBots, smallBot, NextStep.WAIT, generationDigit);
                smartAdd(newSmallBots, smallBot, NextStep.USE_RAZOR, generationDigit);
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
                this.bestStepsList = new ArrayList<>(bestSmallBot.getSteps());
                this.bestStepsList.add(NextStep.ABORT);
                this.bestStepsString = bestSmallBot.toStringStepsSequence()+"A";


            }


            //Ищем лучшего бота по очкам среди всех ботов
            for (SmallBot smallBot : smallBots)
                if (smallBot.getScore() >= bestSmallBotEver.getScore())
                    bestSmallBotEver.copyParamsOfAnotherBot(smallBot);

            this.bestStepsList = new ArrayList<>(bestSmallBotEver.getSteps());
            this.bestStepsList.add(NextStep.ABORT);
            this.bestStepsString = bestSmallBotEver.toStringStepsSequence()+"A";

            //Удаляем худших ботов, если лист больше максимально допустимого размера
            if (smallBots.size() > MAX_SMALL_BOT_SIZE) {
                int deletedBotsDigit = smallBots.size() - MAX_SMALL_BOT_SIZE;
                for (int i = 0; i < deletedBotsDigit; i++)
                    smallBots.remove(smallBots.size() - 1);
            }
        }
    }


    public List<NextStep> getBestStepsList() {
        return bestStepsList;
    }

    public String getBestStepsString() {
        return bestStepsString;
    }

    @Override
    public void run() {
        calculateBestSteps();
    }
}