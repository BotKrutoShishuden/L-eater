package GameMap;

public class Portal {
    private int x;
    private int y;
    private char symbol;

    public Portal(char symbol, int x, int y) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getSymbol() {
        return symbol;
    }
}
