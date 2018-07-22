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

    private GameMap() {
    }


    /*Вызвается в конце каждого хода
    и передвигает объекты*/
    public void moveAllObjects(NextStep botNextStep) {
        lastCondition = this.copy();

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

            case RIGHT:
                break;

            case UP:
                break;

            case DOWN:
                break;


        }

    }

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

