package MapObject;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapObject)) return false;
        MapObject mapObject = (MapObject) o;
        return x == mapObject.x &&
                y == mapObject.y &&
                species == mapObject.species;
    }

    @Override
    public int hashCode() {

        return Objects.hash(species, x, y);
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
            case C_LIFT:
                return 'L';
            case O_LIFT:
                return 'O';
            case LAMBDA_STONE:
                return '@';
            case WALL:
                return '#';
            case STONE:
                return '*';
            case LAMBDA:
                return '\'';
            case EARTH:
                return '.';
            case BEARD:
                return 'W';
            case RAZOR:
                return '!';
            default:
                return 'E';
        }

    }

    public void setSpecies(Species species) {
        this.species = species;
    }

}
