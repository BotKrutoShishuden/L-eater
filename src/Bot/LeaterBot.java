package Bot;

import GameMap.*;
import MapObject.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class LeaterBot extends MapObject {
    NextStep nextStep;
    GameMap gameMap;
    Way bestWay;

    class Way {
        private int score;
        private ArrayList<NextStep> steps;

        Way() {

        }

        public int getScore() {
            return score;
        }

        public ArrayList<NextStep> getSteps() {
            return steps;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setSteps(ArrayList<NextStep> steps) {
            this.steps = steps;
        }
    }


    public LeaterBot() {

    }


    //Логика бота----------------------------------------------------------------------------------

    private Way findClosestLambda(GameMap gameMap) {
        Way way = new Way();
        return way;
    }



    public Way calculateStepsSequence(GameMap gameMap) {

        Way bestWay = new Way();



        return bestWay;
    }

}

