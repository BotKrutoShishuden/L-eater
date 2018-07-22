package Bot;

import GameMap.*;
import MapObject.*;

import java.util.ArrayList;


public class LeaterBot extends MapObject {
    NextStep nextStep;


    public LeaterBot() {

    }


    //Логика бота----------------------------------------------------------------------------------
    private int calculateDistance(MapObject first, MapObject second) {
        return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY());
    }


    private MapObject findClosestLambda(ArrayList<MapObject> lambdas) {
        MapObject current = lambdas.get(0);
        int min = calculateDistance(this, current);
        for (int i = 1; i < lambdas.size(); i++) {
            current = lambdas.get(i);
            if (calculateDistance(this, current) < min)
                min = calculateDistance(this, current);
        }
        return current;
    }


    public String calculateStepsSequence(GameMap gameMap) {
        StringBuilder result = new StringBuilder();

        ArrayList<MapObject> lambdas = new ArrayList<MapObject>();
        MapObject current;
        //Вытаскиваем все лямбды в список
        for (int i = 0; i < gameMap.getMaxX(); i++) {
            for (int j = 0; j < gameMap.getMaxY(); j++) {
                current = gameMap.getObjects()[i][j];
                if (current.getSpecies() == Species.LAMBDA)
                    lambdas.add(current);
                    //Попутно находим координаты бота и даем ему о них знать
                else if (current.getSpecies() == Species.BOT) {
                    this.setX(current.getX());
                    this.setY(current.getY());
                }
            }
        }

        //Находим ближайшую лямбду
        MapObject target = findClosestLambda(lambdas);

        //Сделать проверку на препятствия и метод их обхода
        while (this.getX() != target.getX()) {
            if (this.getX() < target.getX())
                gameMap.moveAllObjects(NextStep.RIGHT);
            else
                gameMap.moveAllObjects(NextStep.LEFT);
        }

        while (this.getY() != target.getY()) {
            if (this.getY() < target.getY())
                gameMap.moveAllObjects(NextStep.DOWN);
            else
                gameMap.moveAllObjects(NextStep.UP);
        }

        return result.toString();
    }

}

