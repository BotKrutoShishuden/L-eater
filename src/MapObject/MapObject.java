package MapObject;

public class MapObject {
    private NextStep nextStep;
    private Species species;
    private Coordinates coordinates;

    //плюс координаты
    public MapObject(NextStep nextStep, Species species, int x, int y) {
        this.nextStep = nextStep;
        this.species = species;
        this.coordinates.setX(x);
        this.coordinates.setY(y);
    }


    protected MapObject() {
    }


    @Override
    public String toString() {
        return species.name();
    }


    public NextStep getNextStep() {
        return nextStep;
    }

    public Species getSpecies() {
        return species;
    }

    public int getX(){
        return coordinates.getX();
    }

    public int getY(){
        return coordinates.getY();
    }

    public void setX(int iks){
        this.coordinates.setX(iks);
    }

    public void setY(int igrek){
        this.coordinates.setY(igrek);
    }

    public char getSymbol() {
        switch (species) {
            case BOT:
                return 'R';
            case AIR:
                return ' ';
            case LIFT:
                return 'L';
            case WALL:
                return '#';
            case STONE:
                return '*';
            case LAMBDA:
                return '\'';
            case EARTH:
                return '.';

        }
        return 'E';
    }

}
