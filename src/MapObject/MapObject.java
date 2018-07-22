package MapObject;

public class MapObject {
    private NextStep nextStep;
    private Species species;


    public MapObject(NextStep nextStep, Species species) {
        this.nextStep = nextStep;
        this.species = species;
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
