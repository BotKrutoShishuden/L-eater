package Bot;

import java.util.ArrayList;
import java.util.List;


import GameMap.GameMap;
import GameMap.GameCondition;

import static Bot.LeaterBot.BONUS_OF_RESEARCH_DIVIDER;
import static Bot.LeaterBot.START_BONUS_OF_RESEARCH;

class SmallBot implements Comparable<SmallBot> {
    private GameMap gameMap;
    private List<NextStep> steps;
    private int bonusOfResearch[][];
    private int survivalRate = 0;

    SmallBot(GameMap gameMap) {
        this.gameMap = gameMap;

        bonusOfResearch = new int[this.gameMap.getMaxX()][this.gameMap.getMaxY()];
        for (int i = 0; i < this.gameMap.getMaxX(); i++)
            for (int j = 0; j < this.gameMap.getMaxY(); j++)
                bonusOfResearch[i][j] = START_BONUS_OF_RESEARCH;

        steps = new ArrayList<>();

    }

    //Логик--------------------------------------------------------------------

    //TODO
    private int calculateBonusForFoundedRazor(GameMap gameMap) {
        return 1;
    }

    SmallBot(GameMap oldMap, List<NextStep> oldSteps, NextStep nextStep, int oldSurvivalRate, int oldBonusOfResearch[][]) {
        bonusOfResearch = new int[oldMap.getMaxX()][oldMap.getMaxY()];
        if (oldBonusOfResearch == null)
            for (int x = 0; x < oldMap.getMaxX(); x++)
                for (int y = 0; y < oldMap.getMaxY(); y++)
                    bonusOfResearch[x][y] = START_BONUS_OF_RESEARCH;
        else
            for (int x = 0; x < oldMap.getMaxX(); x++)
                for (int y = 0; y < oldMap.getMaxY(); y++)
                    bonusOfResearch[x][y] = oldBonusOfResearch[x][y];


        steps = new ArrayList<>();
        steps.addAll(oldSteps);
        steps.add(nextStep);

        int oldRazorDigit = oldMap.getRazors();
        gameMap = oldMap.copy();
        gameMap.moveAllObjects(nextStep);

        int bonusForFoundedRazor = calculateBonusForFoundedRazor(oldMap.copy());
        int foundedRazorNumber = gameMap.getRazors() - oldMap.getRazors();
        if (foundedRazorNumber < 0)
            foundedRazorNumber = 0;

        survivalRate = foundedRazorNumber * bonusForFoundedRazor +
                (oldSurvivalRate + 1) +
                gameMap.getScore() +
                bonusOfResearch[getX()][getY()];

        bonusOfResearch[getX()][getY()] /= BONUS_OF_RESEARCH_DIVIDER;

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

    int[][] getBonusOfResearch() {
        return bonusOfResearch;
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
