package Bot;

public enum NextStep {
    UP, DOWN, LEFT, RIGHT, WAIT, USE_RAZOR, BACK, ABORT;

    public char getSymbol() {
        switch (this) {

            case WAIT:
                return 'W';
            case USE_RAZOR:
                return 'S';
            case RIGHT:
                return 'R';
            case LEFT:
                return 'L';
            case DOWN:
                return 'D';
            case UP:
                return 'U';
            case BACK:
                return 'B';
            case ABORT:
                return 'A';

        }
        return 'E';

    }
}
