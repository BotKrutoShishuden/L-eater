package GameMap;


public class Portal {
    private PortalCondition condition;
    private int x;
    private int y;
    private char name;
    private char exitName;


    public Portal(PortalCondition condition, int x, int y, char name) {
        this.condition = condition;
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Portal(PortalCondition condition, int x, int y, char name, char exitName) {
        this.condition = condition;
        this.x = x;
        this.y = y;
        this.name = name;
        this.exitName = exitName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getName() {
        return name;
    }

    public PortalCondition getCondition() {
        return condition;
    }

    public char getExitName() {
        return exitName;
    }
}
