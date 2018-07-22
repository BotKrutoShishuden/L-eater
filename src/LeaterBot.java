import MapObject.*;


public class LeaterBot extends MapObject {
    private GameMap gameMap;

    //Миша петух

    public LeaterBot() {

    }

    //Главный метод бота
    public String calculateStepsSequence(GameMap gameMap) {
        StringBuilder result = new StringBuilder();

        NextStep nextStep = NextStep.STAY;

        //ЛОГИКА

        gameMap.moveAllObjects(nextStep);


        return result.toString();

    }

}
