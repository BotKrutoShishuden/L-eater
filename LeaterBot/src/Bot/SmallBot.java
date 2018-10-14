package Bot;

import java.util.ArrayList;
import java.util.List;


import GameMap.GameMap;
import GameMap.GameCondition;

import static Bot.LeaterBot.BONUS_OF_LOCAL_RESEARCH_DIVIDER;
import static Bot.LeaterBot.LOCAL_START_BONUS_OF_RESEARCH;

final class SmallBot implements Comparable<SmallBot> {
    private GameMap gameMap;
    private List<NextStep> steps;
    private int bonusOfLocalResearch[][];
    private int bonusForRareLambdas = 0;
    private int survivalRate = 0;

    SmallBot(GameMap gameMap) {
        this.gameMap = gameMap;

        bonusOfLocalResearch = new int[this.gameMap.getMaxX()][this.gameMap.getMaxY()];
        for (int i = 0; i < this.gameMap.getMaxX(); i++)
            for (int j = 0; j < this.gameMap.getMaxY(); j++)
                bonusOfLocalResearch[i][j] = LOCAL_START_BONUS_OF_RESEARCH;

        steps = new ArrayList<>();

    }

    //Логик--------------------------------------------------------------------
    private int calculateBonusForFoundedRazor(GameMap gameMap) {
        return 10 * getGameMap().getGrowth() * gameMap.getBeardsNumber() /
                (gameMap.getRazorsNumber() + 1);
    }

    SmallBot(GameMap oldMap, List<NextStep> oldSteps, NextStep nextStep,
             int oldSurvivalRate, int oldBonusOfResearch[][]) {

        gameMap = oldMap.copy();

        steps = new ArrayList<>();
        steps.addAll(oldSteps);
        steps.add(nextStep);//Бонусы за бритвы

        if (nextStep == NextStep.ABORT)
            return;


        //Инициализация локальных бонусов, наследуется от предыдущего бота
        bonusOfLocalResearch = new int[oldMap.getMaxX()][oldMap.getMaxY()];
        if (oldBonusOfResearch == null)
            for (int x = 0; x < oldMap.getMaxX(); x++)
                for (int y = 0; y < oldMap.getMaxY(); y++)
                    bonusOfLocalResearch[x][y] = LOCAL_START_BONUS_OF_RESEARCH;
        else
            for (int x = 0; x < oldMap.getMaxX(); x++)
                if (oldMap.getMaxY() >= 0)
                    System.arraycopy(oldBonusOfResearch[x], 0, bonusOfLocalResearch[x], 0, oldMap.getMaxY());


        //Бонус за глобальное исследование карты
        int bonusForGlobalResearch = LeaterBot.nobodyNotVisitedWays[getX()][getY()];
        LeaterBot.nobodyNotVisitedWays[getX()][getY()] = 0;

        //Бонус за локальное исследование карты
        int bonusForLocalResearch = bonusOfLocalResearch[getX()][getY()];
        bonusOfLocalResearch[getX()][getY()] /= BONUS_OF_LOCAL_RESEARCH_DIVIDER;

        //Бонусы за бритвы
        int foundedRazorNumber;
        int bonusForFoundedRazor;

        gameMap.moveAllObjects(nextStep);


        foundedRazorNumber = gameMap.getRazorsNumber() < oldMap.getRazorsNumber() ? 0 : gameMap.getRazorsNumber() - oldMap.getRazorsNumber();
        bonusForFoundedRazor = calculateBonusForFoundedRazor(oldMap.copy()) * foundedRazorNumber;


        survivalRate = bonusForGlobalResearch +
                bonusForLocalResearch +
                bonusForFoundedRazor +
                (oldSurvivalRate + 1) + gameMap.getScore();

    }


    //SETTERS--------------------------------------------------------------------
    public void addSurvivalRate(int rate) {
        survivalRate += rate;
    }

    public void addBonusForRareLambda(int bonus) {
        bonusForRareLambdas += bonus;
    }

    void copyParamsOfAnotherBot(SmallBot smallBot) {
        gameMap = smallBot.gameMap.copy();


        steps.clear();
        steps.addAll(smallBot.getSteps());
    }

    //GETTERS--------------------------------------------------------------------
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

    List<NextStep> getListOfStepInRange(int from, int to) {
        List<NextStep> result = new ArrayList<>();
        for (int i = from; i < to; i++)
            result.add(steps.get(i));
        return result;

    }

    int getScore() {
        return gameMap.getScore();
    }

    boolean[] getCollectedLambdas() {
        return gameMap.getCollectedLambdas();
    }

    public int getBonusForRareLambdas() {
        return bonusForRareLambdas;
    }

    //Override--------------------------------------------------------------------
    @Override
    public String toString() {
        return "Survivability " + getSurvivalRate() + ", RL " + getBonusForRareLambdas() + ", Score " + gameMap.getScore() + ", Steps " + getSteps().size() + "\n"
                + toStringStepsSequence();
    }

    public String toStringStepsSequence() {
        StringBuilder stringBuilder = new StringBuilder();

        for (NextStep step : steps)
            stringBuilder.append(step.getSymbol());

        return stringBuilder.toString();
    }

    public String toStringAllWayWithSteps() {
        if (GameMap.STORAGE_PREVIOUS_MAP) {
            StringBuilder reportBuilder = new StringBuilder();
            while (gameMap.getPreviousMap() != null)
                gameMap.moveAllObjects(NextStep.BACK);

            reportBuilder.append("Start map\n").append(gameMap.toString());

            for (NextStep nextStep : getSteps()) {
                reportBuilder.append(nextStep).append("\n");
                gameMap.moveAllObjects(nextStep);
                reportBuilder.append(gameMap.toString()).append("\n");
            }

            return reportBuilder.toString();
        }
        return "STORAGE PREVIOUS MAP MODE OFF";

    }

    @Override
    public int compareTo(SmallBot o) {
        /*Сначала сравнение по поколениям
        Если одно поколение - по survivalRate*/
        if (getSteps().size() > o.getSteps().size())
            return 1;
        else if (getSteps().size() < o.getSteps().size())
            return -1;
        else
            return Integer.compare(getSurvivalRate() + getBonusForRareLambdas(),
                    o.getSurvivalRate() + o.getBonusForRareLambdas());


    }
}
