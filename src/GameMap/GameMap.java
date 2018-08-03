package GameMap;

import Bot.*;
import MapObject.MapObject;
import MapObject.Species;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class GameMap {
    private MapObject mapObjects[][];
    private int maxX;
    private int maxY;
    private int growth;
    private int razors;
    private GameMap lastCondition;
    private MapObject bot;

    //-----------------------------------------------------------------------------------
    public GameMap(String address) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
            int c;
            int currentX = 0, maxX = 0, maxY = 0;
            while ((c = bufferedReader.read()) != -1) {
                switch (c) {
                    case 'R':
                        currentX++;
                        break;
                    case 'L':
                        currentX++;
                        break;
                    case '#':
                        currentX++;
                        break;
                    case '*':
                        currentX++;
                        break;
                    case '\'':
                        currentX++;
                        break;
                    case '.':
                        currentX++;
                        break;
                    case ' ':
                        currentX++;
                        break;
                    case '\n':
                        if (currentX > maxX)
                            maxX = currentX;
                        currentX = 0;
                        maxY++;
                        break;
                    case 'G':
                        while (!Character.isDigit(c))
                            c = bufferedReader.read();
                        int degree = 1;
                        while (Character.isDigit(c)) {
                            growth = growth * degree + Character.getNumericValue(c);
                            degree *= 10;
                            c = bufferedReader.read();
                        }
                        break;
                    case 'a':
                        while (!Character.isDigit(c))
                            c = bufferedReader.read();
                        degree = 1;
                        while (Character.isDigit(c)) {
                            razors = razors * degree + Character.getNumericValue(c);
                            degree *= 10;
                            c = bufferedReader.read();
                        }
                        break;

                }

            }


            mapObjects = new MapObject[maxX][maxY];
            this.maxX = maxX;
            this.maxY = maxY;
            int botCounter = 0;
            int currentY = 0;
            currentX = 0;

            bufferedReader = new BufferedReader(new FileReader(address));

            //Добавляем еще координаты mapObjectов
            while (currentY < maxY) {
                c = bufferedReader.read();
                boolean finded = false;
                switch (c) {
                    case 'R':
                        if (botCounter == 0) {
                            finded = true;
                            botCounter++;
                            bot = new MapObject(Species.BOT, currentX, currentY);
                            mapObjects[currentX][currentY] = bot;


                        }
                        break;
                    case 'L':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(Species.LIFT, currentX, currentY);
                        break;
                    case '#':
                        finded = true;
                        mapObjects[currentX][currentY] =
                                new MapObject(Species.WALL, currentX, currentY);
                        break;
                    case '*':
                        finded = true;
                        mapObjects[currentX][currentY] =
                                new MapObject(Species.STONE, currentX, currentY);
                        break;
                    case 92:
                        finded = true;
                        mapObjects[currentX][currentY] =
                                new MapObject(Species.LAMBDA, currentX, currentY);
                        break;
                    case '.':
                        finded = true;
                        mapObjects[currentX][currentY] =
                                new MapObject(Species.EARTH, currentX, currentY);
                        break;
                    case ' ':
                        finded = true;
                        mapObjects[currentX][currentY] =
                                new MapObject(Species.AIR, currentX, currentY);
                        break;
                    case 13://CR
                        while (currentX < maxX) {
                            mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                            currentX++;
                        }
                        bufferedReader.read();
                        finded = true;
                        currentX = -1;
                        currentY++;
                        break;

                    case '\n':
                        while (currentX < maxX) {
                            mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                            currentX++;
                        }
                        finded = true;
                        currentX = -1;
                        currentY++;
                        break;

                }
                if (!finded)
                    System.out.println("Неизвестный символ = " + (char) c);
                currentX++;
            }
            bufferedReader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public GameMap() {
    }


    //-----------------------------------------------------------------------------------
    //Революция тестов
    public static GameMap cutMap(BufferedReader bufferedReader, char start, char end) throws IOException {
        GameMap gameMap = new GameMap();
        gameMap.maxX = 0;
        gameMap.maxX = 0;
        gameMap.growth = 0;
        gameMap.razors = 0;
        StringBuilder stringBuilder = new StringBuilder();

        int c;
        do {
            c = bufferedReader.read();
        } while (c != start);


        int currentX = 0;

        while ((c = bufferedReader.read()) != end) {
            switch (c) {
                case 'R':
                    currentX++;
                    break;
                case 'L':
                    currentX++;
                    break;
                case '#':
                    currentX++;
                    break;
                case '*':
                    currentX++;
                    break;
                case '\'':
                    currentX++;
                    break;
                case '.':
                    currentX++;
                    break;
                case ' ':
                    currentX++;
                    break;
                case '\n':
                    if (currentX > gameMap.maxX)
                        gameMap.maxX = currentX;
                    currentX = 0;
                    gameMap.maxY++;
                    break;
                case 'G':
                    while (!Character.isDigit(c))
                        c = bufferedReader.read();
                    int degree = 1;
                    while (Character.isDigit(c)) {
                        gameMap.growth = gameMap.growth * degree + Character.getNumericValue(c);
                        degree *= 10;
                        c = bufferedReader.read();
                    }
                    break;
                case 'a':
                    while (!Character.isDigit(c))
                        c = bufferedReader.read();
                    degree = 1;
                    while (Character.isDigit(c)) {
                        gameMap.razors = gameMap.razors * degree + Character.getNumericValue(c);
                        degree *= 10;
                        c = bufferedReader.read();
                    }
                    break;

            }
            stringBuilder.append((char) c);
        }

        gameMap.maxY -= 1;
        stringBuilder = new StringBuilder(stringBuilder.toString().replace("\r", ""));
        stringBuilder = new StringBuilder(stringBuilder.delete(0, 1));

        gameMap.mapObjects = new MapObject[gameMap.maxX][gameMap.maxY];

        int botCounter = 0;
        int currentY = 0;
        currentX = 0;


        //Добавляем еще координаты mapObjectов
        int i = 0;
        while (currentY < gameMap.maxY) {
            c = stringBuilder.charAt(i);
            i++;
            boolean finded = false;
            switch (c) {
                case 'R':
                    if (botCounter == 0) {
                        finded = true;
                        botCounter++;
                        gameMap.bot = new MapObject(Species.BOT, currentX, currentY);
                        gameMap.mapObjects[currentX][currentY] = gameMap.bot;


                    }
                    break;
                case 'L':
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] = new MapObject(Species.LIFT, currentX, currentY);
                    break;
                case '#':
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.WALL, currentX, currentY);
                    break;
                case '*':
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.STONE, currentX, currentY);
                    break;
                case 92:
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.LAMBDA, currentX, currentY);
                    break;
                case '.':
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.EARTH, currentX, currentY);
                    break;
                case ' ':
                    finded = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.AIR, currentX, currentY);
                    break;
                case 13://CR
                    while (currentX < gameMap.maxX) {
                        gameMap.mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                        currentX++;
                    }
                    bufferedReader.read();
                    finded = true;
                    currentX = -1;
                    currentY++;
                    break;

                case '\n':
                    while (currentX < gameMap.maxX) {
                        gameMap.mapObjects[currentX][currentY] = new MapObject(Species.AIR, currentX, currentY);
                        currentX++;
                    }
                    finded = true;
                    currentX = -1;
                    currentY++;
                    break;

            }
            if (!finded)
                System.out.println("Неизвестный символ = " + (char) c);
            currentX++;
        }


        return gameMap;
    }

    public static Bot.NextStep[] cutSteps(BufferedReader bufferedReader) throws IOException {

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
    private void moveLambda(int xLambda, int yLambda) {

    }

    public void moveAllObjects(NextStep botNextStep) {
        lastCondition = this.copy();

        moveBot(botNextStep);

        for (int x = 0; x < maxX; x++)
            for (int y = 0; y < maxY; y++)
                switch (mapObjects[x][y].getSpecies()) {
                    case STONE:
                        moveStone(x, y);
                        break;
                    case LAMBDA:
                        moveLambda(x, y);
                        break;
                    default:
                        break;
                }


    }
    //-----------------------------------------------------------------------------------

    public void backToLastCondition() {
        this.lastCondition = lastCondition.lastCondition;
        this.mapObjects = lastCondition.mapObjects.clone();
        this.maxX = lastCondition.maxX;
        this.maxY = lastCondition.maxY;
        this.bot = lastCondition.bot;
        this.growth = lastCondition.growth;
        this.razors = lastCondition.razors;
    }

    private GameMap copy() {
        GameMap gameMap = new GameMap();
        gameMap.maxX = maxX;
        gameMap.maxY = maxY;
        gameMap.lastCondition = lastCondition;
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

    public GameMap getLastCondition() {
        return lastCondition;
    }

    public MapObject getBot() {
        return bot;
    }
}

