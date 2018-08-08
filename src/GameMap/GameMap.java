package GameMap;

import Bot.*;
import MapObject.MapObject;
import MapObject.Species;

import java.io.*;

import static GameMap.GameCondition.*;

enum GameCondition {
    STILL_MINING, RB_CRUSHED, RB_DROWNED, WIN
}

public class GameMap {
    private MapObject mapObjects[][];
    private int maxX;
    private int maxY;
    private int growth;
    private int razors;
    private GameMap previousMap;
    private MapObject bot;
    private GameCondition gameCondition;

    //-----------------------------------------------------------------------------------
    //Статические методы генерации(вместо конструкторов)
    private static GameMap init() {
        GameMap gameMap = new GameMap();
        gameMap.maxX = 0;
        gameMap.maxX = 0;
        gameMap.growth = 0;
        gameMap.razors = 0;
        gameMap.gameCondition = STILL_MINING;
        return gameMap;
    }

    private static GameMap cutMapByEnd(BufferedReader bufferedReader, String end) throws IOException {
        GameMap gameMap = init();
        StringBuilder currentLine;
        StringBuilder mapStrBuilder = new StringBuilder();

        //Считаем размер карты
        while (!(currentLine = new StringBuilder(bufferedReader.readLine())).toString().equals(end)) {
            if (currentLine.length() > gameMap.maxX) {
                gameMap.maxX = currentLine.length();
            }
            gameMap.maxY++;
            mapStrBuilder.append(currentLine).append("\n");
        }


        gameMap.mapObjects = new MapObject[gameMap.maxX][gameMap.maxY];

        int currentX = 0;
        int currentY = 0;


        //Заполняем mapObject[][]
        int i = 0;
        while (currentY < gameMap.maxY) {

            boolean symbolDefined = false;
            switch (mapStrBuilder.charAt(i)) {
                case 'R':
                    symbolDefined = true;
                    gameMap.bot = new MapObject(Species.BOT, currentX, currentY);
                    gameMap.mapObjects[currentX][currentY] = gameMap.bot;
                    break;
                case 'L':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] = new MapObject(Species.LIFT, currentX, currentY);
                    break;
                case '#':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.WALL, currentX, currentY);
                    break;
                case '*':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.STONE, currentX, currentY);
                    break;
                case 92:/* \-лямбда */
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.LAMBDA, currentX, currentY);
                    break;
                case '@':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.LAMBDA_STONE, currentX, currentY);
                    break;
                case '.':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.EARTH, currentX, currentY);
                    break;
                case ' ':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.AIR, currentX, currentY);
                    break;
                case '!':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.RAZOR, currentX, currentY);
                    break;
                case 'W':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.BEARD, currentX, currentY);
                    break;
                case 13://CR
                    while (currentX < gameMap.maxX) {
                        gameMap.mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                        currentX++;
                    }
                    i++;
                    symbolDefined = true;
                    currentX = -1;
                    currentY++;
                    break;

                case '\n':
                    while (currentX < gameMap.maxX) {
                        gameMap.mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                        currentX++;
                    }
                    symbolDefined = true;
                    currentX = -1;
                    currentY++;
                    break;

            }
            if (!symbolDefined) {
                System.out.println("Undefined symbol= " + mapStrBuilder.charAt(i));
                //System.out.println("File Name = " + bufferedReader.toString());
            }
            currentX++;
            i++;
        }

        bufferedReader.close();
        return gameMap;

    }

    private static int cutParamAfterWord(String address, String paramName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
        try {
            StringBuilder currentLine;
            do {
                currentLine = new StringBuilder(bufferedReader.readLine());
            }
            while (!currentLine.toString().contains(paramName));

            return Integer.valueOf(currentLine.delete(0, paramName.length()).toString());

        } catch (NullPointerException e) {
            return 0;
        }
    }


    public static GameMap cutMapBetweenStartAndEnd(String address, String start, String end) throws IOException {
        if (start.equals(end)) {
            System.out.println("start and end can't be equal");
            throw new UnsupportedOperationException();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
        StringBuilder currentLine;

        do {
            currentLine = new StringBuilder(bufferedReader.readLine());
        } while (!currentLine.toString().equals(start));

        return cutMapByEnd(bufferedReader, end);

    }

    public static GameMap cutNormalMap(String address) {
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
            GameMap gameMap = cutMapByEnd(bufferedReader, "");
            gameMap.growth = cutParamAfterWord(address, "Growth ");
            gameMap.razors = cutParamAfterWord(address, "Razors ");
            return gameMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static NextStep[] cutSteps(String address) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
        int c;
        do {
            c = bufferedReader.read();

        } while (c != 'c');
        bufferedReader.readLine();

        StringBuilder stringBuilder = new StringBuilder(bufferedReader.readLine());
        Bot.NextStep nextSteps[] = new Bot.NextStep[stringBuilder.length()];

        for (int i = 0; i < stringBuilder.length(); i++) {
            switch (stringBuilder.charAt(i)) {
                case 'U':
                    nextSteps[i] = NextStep.UP;
                    break;
                case 'D':
                    nextSteps[i] = NextStep.DOWN;
                    break;
                case 'L':
                    nextSteps[i] = NextStep.LEFT;
                    break;
                case 'R':
                    nextSteps[i] = NextStep.RIGHT;
                    break;
                case 'S':
                    nextSteps[i] = NextStep.STAY;
            }

        }
        return nextSteps;

    }


    //-----------------------------------------------------------------------------------
    /*Вызвается в конце каждого хода
    и передвигает объекты*/
    private void moveBot(NextStep botNextStep) {
        switch (botNextStep) {

            case LEFT:

                if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.STONE) {
                    if (mapObjects[bot.getX() - 2][bot.getY()].getSpecies() == Species.AIR)
                        if (mapObjects[bot.getX() - 2][bot.getY() + 1].getSpecies() == Species.AIR) {

                            mapObjects[bot.getX() - 2][bot.getY() + 1] =
                                    new MapObject(Species.STONE, bot.getX() - 2, bot.getY() + 1);

                            mapObjects[bot.getX()][bot.getY()] =
                                    new MapObject(Species.AIR, bot.getX(), bot.getY());

                            mapObjects[bot.getX() - 1][bot.getY()] = bot;
                            bot.setX(bot.getX() - 1);

                        } else {
                            mapObjects[bot.getX() - 2][bot.getY()] =
                                    new MapObject(Species.STONE, bot.getX() - 2, bot.getY());

                            mapObjects[bot.getX()][bot.getY()] =
                                    new MapObject(Species.AIR, bot.getX(), bot.getY());

                            mapObjects[bot.getX() - 1][bot.getY()] = bot;
                            bot.setX(bot.getX() - 1);


                        }
                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.AIR) {

                    mapObjects[bot.getX()][bot.getY()] =
                            new MapObject(Species.AIR, bot.getX(), bot.getY());


                    mapObjects[bot.getX() - 1][bot.getY()] = bot;
                    bot.setX(bot.getX() - 1);

                }
                break;

            case RIGHT:
                if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.STONE) {
                    if (mapObjects[bot.getX() + 2][bot.getY()].getSpecies() == Species.AIR)
                        if (mapObjects[bot.getX() + 2][bot.getY() + 1].getSpecies() == Species.AIR) {

                            mapObjects[bot.getX() + 2][bot.getY() + 1] =
                                    new MapObject(Species.STONE, bot.getX() + 2, bot.getY() + 1);

                            mapObjects[bot.getX()][bot.getY()] =
                                    new MapObject(Species.AIR, bot.getX(), bot.getY());

                            mapObjects[bot.getX() + 1][bot.getY()] = bot;
                            bot.setX(bot.getX() + 1);

                        } else {
                            mapObjects[bot.getX() + 2][bot.getY()] =
                                    new MapObject(Species.STONE, bot.getX() + 2, bot.getY());

                            mapObjects[bot.getX()][bot.getY()] =
                                    new MapObject(Species.AIR, bot.getX(), bot.getY());

                            mapObjects[bot.getX() + 1][bot.getY()] = bot;
                            bot.setX(bot.getX() + 1);


                        }
                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.AIR) {

                    mapObjects[bot.getX()][bot.getY()] =
                            new MapObject(Species.AIR, bot.getX(), bot.getY());


                    mapObjects[bot.getX() + 1][bot.getY()] = bot;
                    bot.setX(bot.getX() + 1);

                }

                break;

            case UP:

                if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() != Species.STONE
                        && mapObjects[bot.getX()][bot.getY() - 1].getSpecies() != Species.WALL) {

                    mapObjects[bot.getX()][bot.getY()] =
                            new MapObject(Species.AIR, bot.getX(), bot.getY());

                    bot.setY(bot.getY() - 1);
                    mapObjects[bot.getX()][bot.getY()] = bot;

                }

                break;

            case DOWN:

                if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() != Species.STONE
                        && mapObjects[bot.getX()][bot.getY() + 1].getSpecies() != Species.WALL) {

                    mapObjects[bot.getX()][bot.getY()] =
                            new MapObject(Species.AIR, bot.getX(), bot.getY());

                    bot.setY(bot.getY() + 1);
                    mapObjects[bot.getX()][bot.getY()] = bot;

                }

                break;
            case STAY:
                break;


        }

    }

    //TODO
    private void moveStone(int xStone, int yStone) {

    }

    //TODO
    private void moveAStone(int xAStone, int yAStone) {

    }

    public void moveAllObjects(NextStep botNextStep) {
        previousMap = this.copy();

        moveBot(botNextStep);

        for (int x = 0; x < maxX; x++)
            for (int y = 0; y < maxY; y++)
                switch (mapObjects[x][y].getSpecies()) {
                    case STONE:
                        moveStone(x, y);
                        break;
                    case LAMBDA_STONE:
                        moveAStone(x, y);
                        break;
                    default:
                        break;
                }


    }
    //-----------------------------------------------------------------------------------

    public void backToLastCondition() {
        this.previousMap = previousMap.previousMap;
        this.mapObjects = previousMap.mapObjects.clone();
        this.maxX = previousMap.maxX;
        this.maxY = previousMap.maxY;
        this.bot = previousMap.bot;
        this.growth = previousMap.growth;
        this.razors = previousMap.razors;
    }

    private GameMap copy() {
        GameMap gameMap = new GameMap();
        gameMap.maxX = maxX;
        gameMap.maxY = maxY;
        gameMap.previousMap = previousMap;
        gameMap.bot = bot;
        gameMap.mapObjects = mapObjects.clone();
        gameMap.growth = growth;
        gameMap.razors = razors;

        return gameMap;
    }

    //-----------------------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                stringBuilder.append(mapObjects[x][y].getSymbol());

            }
            stringBuilder.append("\n");

        }
        return stringBuilder.toString();
    }

    public MapObject[][] getObjects() {
        return mapObjects;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getGrowth() {
        return growth;
    }

    public int getRazors() {
        return razors;
    }

    public GameMap getPreviousMap() {
        return previousMap;
    }

    public MapObject getBot() {
        return bot;
    }

    public GameCondition getGameCondition() {
        return gameCondition;
    }
}

