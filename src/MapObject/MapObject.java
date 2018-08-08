package MapObject;

public class MapObject {
    private Species species;
    private int x;
    private int y;

    //плюс координаты
    public MapObject(Species species, int x, int y) {
        this.species = species;
        this.x = x;
        this.y = y;
    }


    protected MapObject() {
    }


    @Override
    public String toString() {
        return species.name();
    }

    public Species getSpecies() {
        return species;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public void setSpecies (Species species) {
        this.species = species;
    }

}
