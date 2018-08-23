package Bot;

import java.util.ArrayList;
import java.util.List;


import GameMap.GameMap;
import GameMap.GameCondition;

import static Bot.LeaterBot.BONUS_OF_RESEARCH_DIVIDER;
import static Bot.LeaterBot.getStartBonusOfResearch;

class SmallBot implements Comparable<SmallBot> {
    private GameMap gameMap;
    private List<NextStep> steps;
    private int bonusOfLocalResearch[][];
    private int survivalRate = 0;

    SmallBot(GameMap gameMap) {
        this.gameMap = gameMap;

        bonusOfLocalResearch = new int[this.gameMap.getMaxX()][this.gameMap.getMaxY()];
        for (int i = 0; i < this.gameMap.getMaxX(); i++)
            for (int j = 0; j < this.gameMap.getMaxY(); j++)
                bonusOfLocalResearch[i][j] = getStartBonusOfResearch(gameMap);

        steps = new ArrayList<>();

    }

    //Логик--------------------------------------------------------------------

    //TODO Скорее всего надо подправить, пока непонятно как он себя ведет
    private int calculateBonusForFoundedRazor(GameMap gameMap) {
        if (gameMap.getBeards() == 0) return 0;
        if (gameMap.getRazors() == 0 && gameMap.getBeards() > 0) return gameMap.getBeards() * 10 / gameMap.getGrowth();
        if (gameMap.getRazors() > 1 && gameMap.getBeards() < 5)
            return (gameMap.getBeards() - gameMap.getRazors()) * 10 / gameMap.getGrowth();
        if (gameMap.getBeards() > 5) return gameMap.getBeards();
        return 1;
    }

    SmallBot(GameMap oldMap, List<NextStep> oldSteps, NextStep nextStep,
             int oldSurvivalRate, int oldBonusOfResearch[][]) {

        gameMap = oldMap.copy();

        steps = new ArrayList<>();
        steps.addAll(oldSteps);
        steps.add(nextStep);

        if (nextStep == NextStep.ABORT)
            return;


        //Инициализация локальных бонусов
        bonusOfLocalResearch = new int[oldMap.getMaxX()][oldMap.getMaxY()];
        if (oldBonusOfResearch == null)
            for (int x = 0; x < oldMap.getMaxX(); x++)
                for (int y = 0; y < oldMap.getMaxY(); y++)
                    bonusOfLocalResearch[x][y] = getStartBonusOfResearch(oldMap);
        else
            for (int x = 0; x < oldMap.getMaxX(); x++)
                for (int y = 0; y < oldMap.getMaxY(); y++)
                    bonusOfLocalResearch[x][y] = oldBonusOfResearch[x][y];


        //Бонус за глобальное исследование карты
        int bonusForGlobalResearch = LeaterBot.nobodyNotVisitedWays[getX()][getY()];
        LeaterBot.nobodyNotVisitedWays[getX()][getY()] = 0;

        //Бонус за локальное исследование карты
        int bonusForLocalResearch = bonusOfLocalResearch[getX()][getY()];
        bonusOfLocalResearch[getX()][getY()] /= BONUS_OF_RESEARCH_DIVIDER;

        //Бонусы за бритвы
        int foundedRazorNumber;
        int bonusForFoundedRazor;
        int oldRazorNumber = oldMap.getRazors();
        gameMap.moveAllObjects(nextStep);
        foundedRazorNumber = gameMap.getRazors() - oldMap.getRazors();
        if (foundedRazorNumber < 0)
            foundedRazorNumber = 0;
        bonusForFoundedRazor = calculateBonusForFoundedRazor(oldMap.copy()) * foundedRazorNumber;


        survivalRate = bonusForGlobalResearch +
                bonusForLocalResearch +
                bonusForFoundedRazor +
                (oldSurvivalRate + 1) + gameMap.getScore();

    }


    //GETTERS,SETTERS--------------------------------------------------------------------
    void setConditions(SmallBot smallBot) {
        gameMap = smallBot.gameMap.copy();


        steps.clear();
        steps.addAll(smallBot.getSteps());
    }

    GameMap getGameMap() {
        return gameMap;
    }

    int[][] getBonusOfLocalResearch() {
        return bonusOfLocalResearch;
    }

    GameCondition getGameCondition() {
        return gameMap.getGameCondition();
    }

    int getX() {
        return gameMap.getBot().getX();
    }

    int getY() {
        return gameMap.getBot().getY();
    }

    int getSurvivalRate() {
        return survivalRate;
    }

    List<NextStep> getSteps() {
        return steps;
    }

    List<NextStep> copyListStepsOfRange(int from, int to) {
        List<NextStep> result = new ArrayList<>();
        for (int i = from; i < to; i++)
            result.add(steps.get(i));
        return result;

    }

    int getScore() {
        return gameMap.getScore();
    }

    NextStep getLastStep() {
        return getSteps().get(getSteps().size() - 1);
    }


    //Override--------------------------------------------------------------------
    @Override
    public String toString() {
        return "survivability " + getSurvivalRate() + ", Score " + gameMap.getScore() + ", Steps " + getSteps().size() + "\n"
                + toStringStepsSequence();
    }

    private String toStringStepsSequence() {
        StringBuilder stringBuilder = new StringBuilder();

        for (NextStep step : steps)
            stringBuilder.append(step.getSymbol());

        return stringBuilder.toString();
    }

    @Override
    public int compareTo(SmallBot o) {
        if (getSurvivalRate() > o.getSurvivalRate())
            return 1;
        else if (getSurvivalRate() < o.getSurvivalRate())
            return -1;
        else//Новые поколения живут
            return Integer.compare(getSteps().size(), o.getSteps().size());

    }
}
