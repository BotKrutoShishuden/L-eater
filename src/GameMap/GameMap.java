package GameMap;

import Bot.*;
import MapObject.MapObject;
import MapObject.Species;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static GameMap.GameCondition.*;


public class GameMap {
    private MapObject mapObjects[][];
    private int maxX;
    private int maxY;
    private int growth = 25;
    private int razors = 0;

    private int movesUnderWater;
    private int maxMovesUnderWater = 10;
    private int waterLevel = 0;
    private int flooding = 0;


    private GameCondition gameCondition = STILL_MINING;
    private int amountOfSteps;
    private int score;
    private int lamdasNumber;
    private int maxLambdasNumber;

    private MapObject bot;
    private GameMap previousMap;
    private PortalSystem portalSystem;

    //-----------------------------------------------------------------------------------
    //Статические методы генерации(вместо конструкторов)
    private static GameMap cutMapByEnd(BufferedReader bufferedReader, String end) throws IOException {
        GameMap gameMap = new GameMap();

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

        ArrayList<Portal> inPortals = new ArrayList<>();
        ArrayList<Portal> outPortals = new ArrayList<>();
        //Заполняем mapObject[][]
        int i = 0;
        while (currentY < gameMap.maxY) {

            boolean symbolDefined = false;
            char currentSymbol = mapStrBuilder.charAt(i);
            switch (currentSymbol) {
                case 'R':
                    symbolDefined = true;
                    gameMap.bot = new MapObject(Species.BOT, currentX, currentY);
                    gameMap.mapObjects[currentX][currentY] = gameMap.bot;
                    break;
                case 'L':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] = new MapObject(Species.C_LIFT, currentX, currentY);
                    break;
                case 'O':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] = new MapObject(Species.O_LIFT, currentX, currentY);
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
                    gameMap.maxLambdasNumber++;
                    break;
                case '@':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.LAMBDA_STONE, currentX, currentY);
                    gameMap.maxLambdasNumber++;
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

            if (Character.isDigit(currentSymbol)) {

                outPortals.add(new Portal(currentSymbol, currentX, currentY));

                gameMap.mapObjects[currentX][currentY] =
                        new MapObject(Species.PORTAL_OUT, currentX, currentY, currentSymbol);

                symbolDefined = true;

            } else if (characterIsAcceptableFigure(currentSymbol)) {
                inPortals.add(new Portal(currentSymbol, currentX, currentY));

                gameMap.mapObjects[currentX][currentY] =
                        new MapObject(Species.PORTAL_IN, currentX, currentY, currentSymbol);

                symbolDefined = true;
            }

            if (!symbolDefined)
                System.out.println("Undefined symbol = " + currentSymbol);

            currentX++;
            i++;

        }
        gameMap.portalSystem = new PortalSystem(inPortals, outPortals);


        bufferedReader.close();
        return gameMap;


    }

    private static boolean characterIsAcceptableFigure(Character symbol) {
        return symbol == 'A' || symbol == 'B' || symbol == 'C' || symbol == 'D' || symbol == 'E' || symbol == 'F' || symbol == 'G';
    }
    //-----------------------------------------------------------------------------------

    //Методы для тестов
    //-----------------------------------------------------------------------------------
    public static int cutParamAfterWord(String address, String paramName) throws IOException {
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

    public static GameCondition cutConditionAfterWord(String address, String paramName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
        try {
            StringBuilder currentLine;
            do {
                currentLine = new StringBuilder(bufferedReader.readLine());
            }
            while (!currentLine.toString().contains(paramName));

            currentLine.delete(0, paramName.length());

            switch (currentLine.toString()) {
                case "STILL_MINING":
                    return STILL_MINING;

                case "RB_DROWNED":
                    return RB_DROWNED;

                case "WIN":
                    return WIN;

                case "RB_CRUSHED":
                    return RB_CRUSHED;

                default:
                    return NULL_CONDITION;

            }

        } catch (NullPointerException e) {
            return NULL_CONDITION;
        }
    }


    public static GameMap cutMapBetweenStartAndEnd(String address, String start, String end)
            throws IOException {
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
                case 'W':
                    nextSteps[i] = NextStep.WAIT;
                case 'S':
                    nextSteps[i] = NextStep.USE_RAZOR;
            }

        }
        return nextSteps;

    }

    public static NextStep[] cutStepsFromString(String commands) {
        StringBuilder stringBuilder = new StringBuilder(commands);
        NextStep nextSteps[] = new NextStep[stringBuilder.length()];
        for (int i = 0; i < stringBuilder.length(); i++) {
            switch (stringBuilder.charAt(i)) {

                case 'W':
                    nextSteps[i] = (NextStep.WAIT);
                    break;
                case 'U':
                    nextSteps[i] = (NextStep.UP);
                    break;
                case 'D':
                    nextSteps[i] = (NextStep.DOWN);
                    break;
                case 'L':
                    nextSteps[i] = (NextStep.LEFT);
                    break;
                case 'R':
                    nextSteps[i] = (NextStep.RIGHT);
                    break;
                case 'S':
                    nextSteps[i] = (NextStep.USE_RAZOR);
                    break;
                case 'B':
                    nextSteps[i] = (NextStep.BACK);
                    break;

            }


        }
        return nextSteps;
    }
    //-----------------------------------------------------------------------------------


    //Методы для изменения карты
    //-----------------------------------------------------------------------------------
    private void moveBot(NextStep botNextStep) {
        switch (botNextStep) {

            case LEFT:
                if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                    score += 175;
                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX() - 1][bot.getY()].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX() - 1][bot.getY()].getSymbol());
                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX - 1][oldY].setSpecies(Species.AIR);

                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.STONE ||//Двигаем камни
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE) {
                    if (mapObjects[bot.getX() - 2][bot.getY()].getSpecies() == Species.AIR) {

                        if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.STONE)
                            mapObjects[bot.getX() - 2][bot.getY()].setSpecies(Species.STONE);
                        else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE)
                            mapObjects[bot.getX() - 2][bot.getY()].setSpecies(Species.LAMBDA_STONE);

                        mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.BOT);
                        bot.setX(bot.getX() - 1);

                        mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.AIR);

                    }
                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.AIR ||//Просто идем
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.RAZOR) {


                    if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA) {
                        score += 50;
                        lamdasNumber++;
                    } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.RAZOR)
                        razors++;


                    mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.BOT);
                    bot.setX(bot.getX() - 1);


                    mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.AIR);


                }


                break;

            case RIGHT:
                if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                    score += 175;
                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX() + 1][bot.getY()].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX() + 1][bot.getY()].getSymbol());
                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX + 1][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX][oldY].setSpecies(Species.AIR);


                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.STONE ||//Двигаем камни
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE) {
                    if (mapObjects[bot.getX() + 2][bot.getY()].getSpecies() == Species.AIR) {

                        if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.STONE)
                            mapObjects[bot.getX() + 2][bot.getY()].setSpecies(Species.STONE);
                        else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE)
                            mapObjects[bot.getX() + 2][bot.getY()].setSpecies(Species.LAMBDA_STONE);


                        mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.BOT);
                        bot.setX(bot.getX() + 1);

                        mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.AIR);


                    }
                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.AIR ||//Просто идем
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.RAZOR) {


                    if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA) {
                        score += 50;
                        lamdasNumber++;
                    } else if ((mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.RAZOR))
                        razors++;

                    mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.BOT);
                    bot.setX(bot.getX() + 1);

                    mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.AIR);


                }


                break;

            case UP:
                if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                    score += 175;
                } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX()][bot.getY() - 1].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX()][bot.getY() - 1].getSymbol());
                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX][oldY - 1].setSpecies(Species.AIR);

                } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() != Species.STONE//Просто идем
                        && mapObjects[bot.getX()][bot.getY() - 1].getSpecies() != Species.WALL) {

                    if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.LAMBDA) {
                        score += 50;
                        lamdasNumber++;
                    } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.RAZOR)
                        razors++;

                    bot.setY(bot.getY() - 1);
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.BOT);

                    mapObjects[bot.getX()][bot.getY() + 1].setSpecies(Species.AIR);

                }


                break;

            case DOWN:

                if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                    score += 175;
                } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX()][bot.getY() + 1].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX()][bot.getY() + 1].getSymbol());
                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX][oldY + 1].setSpecies(Species.AIR);

                } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() != Species.STONE//Просто идем
                        && mapObjects[bot.getX()][bot.getY() + 1].getSpecies() != Species.WALL) {

                    if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.LAMBDA) {
                        score += 50;
                        lamdasNumber++;
                    } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.RAZOR)
                        razors++;


                    bot.setY(bot.getY() + 1);
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.BOT);

                    mapObjects[bot.getX()][bot.getY() - 1].setSpecies(Species.AIR);

                }

                break;

            case USE_RAZOR:

                for (int i = bot.getX() - 1; i < bot.getX() + 2; i++)
                    for (int j = bot.getY() - 1; j < bot.getY() + 2; j++) {
                        MapObject current = mapObjects[i][j];
                        if (current.getSpecies() == Species.BEARD)
                            current.setSpecies(Species.AIR);
                    }

                break;

            case WAIT:
                break;


        }

    }

    private void moveStoneSim(GameMap workMap, int x, int y) {

        if (workMap.getMapObjects()[x][y + 1].getSpecies() == Species.AIR) {// проверяем что снизу ничего нет

            mapObjects[x][y].setSpecies(Species.AIR);
            mapObjects[x][y + 1].setSpecies(Species.STONE);
            if (workMap.getMapObjects()[x][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                gameCondition = RB_CRUSHED;
            }

        } else if (workMap.getMapObjects()[x][y + 1].getSpecies() != Species.AIR) {    // если что-то есть
            if (workMap.getMapObjects()[x][y + 1].getSpecies() == Species.STONE ||
                    workMap.getMapObjects()[x][y + 1].getSpecies() == Species.LAMBDA_STONE ||
                    workMap.getMapObjects()[x][y + 1].getSpecies() == Species.LAMBDA) {//Скатывается

                if (workMap.getMapObjects()[x + 1][y + 1].getSpecies() == Species.AIR &&
                        workMap.getMapObjects()[x + 1][y].getSpecies() == Species.AIR) {//Вправо
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x + 1][y + 1].setSpecies(Species.STONE);
                    if (workMap.getMapObjects()[x + 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                } else if (workMap.getMapObjects()[x - 1][y + 1].getSpecies() == Species.AIR &&
                        workMap.getMapObjects()[x - 1][y].getSpecies() == Species.AIR) {//Влево
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.STONE);
                    if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            }
        }
    }

    private void moveLambdaStoneSim(GameMap workMap, int x, int y) {

        if (workMap.getMapObjects()[x][y + 1].getSpecies() == Species.AIR) {// проверяем что снизу ничего нет

            if (workMap.getMapObjects()[x][y + 2].getSpecies() == Species.AIR) {//Если не разбивается
                mapObjects[x][y].setSpecies(Species.AIR);
                mapObjects[x][y + 1].setSpecies(Species.LAMBDA_STONE);
            } else {
                mapObjects[x][y].setSpecies(Species.AIR);
                mapObjects[x][y + 1].setSpecies(Species.LAMBDA);
                if (workMap.getMapObjects()[x][y + 2].getSpecies() == Species.BOT) { // если падает на бота
                    gameCondition = RB_CRUSHED;
                }
            }

        } else if (workMap.getMapObjects()[x][y + 1].getSpecies() == Species.STONE ||// если что-то есть
                workMap.getMapObjects()[x][y + 1].getSpecies() == Species.LAMBDA_STONE ||
                workMap.getMapObjects()[x][y + 1].getSpecies() == Species.LAMBDA) {//Скатывается

            if (workMap.getMapObjects()[x + 1][y].getSpecies() == Species.AIR &&
                    workMap.getMapObjects()[x + 1][y + 1].getSpecies() == Species.AIR) {//Вправо
                if (workMap.getMapObjects()[x + 1][y + 2].getSpecies() == Species.AIR) {//Если не разбивается
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x + 1][y + 1].setSpecies(Species.LAMBDA_STONE);
                } else {
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x + 1][y + 1].setSpecies(Species.LAMBDA);
                    if (workMap.getMapObjects()[x + 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            } else if (workMap.getMapObjects()[x - 1][y].getSpecies() == Species.AIR) {//Влево
                if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.AIR) {//Если не разбивается
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.LAMBDA_STONE);
                } else {
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.LAMBDA);
                    if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            }
        }

    }


    private void growBeard(GameMap workMap, int xBeard, int yBeard) {
        for (int i = xBeard - 1; i < xBeard + 2; i++)
            for (int j = yBeard - 1; j < yBeard + 2; j++) {
                MapObject current = workMap.getMapObjects()[i][j];
                if (current.getSpecies() == Species.AIR)
                    mapObjects[i][j].setSpecies(Species.BEARD);
            }
    }

    private void raiseWaterLevel() {
        if (flooding != 0 && amountOfSteps % flooding == 0)
            waterLevel++;
        if (bot.getY() >= getMaxY() - waterLevel)
            movesUnderWater++;
        else
            movesUnderWater = 0;

        if (movesUnderWater > maxMovesUnderWater)
            gameCondition = RB_DROWNED;
    }

    private void backToLastCondition() {
        mapObjects = previousMap.mapObjects;
        maxX = previousMap.maxX;
        maxY = previousMap.maxY;
        growth = previousMap.growth;
        razors = previousMap.razors;

        movesUnderWater = previousMap.movesUnderWater;
        waterLevel = previousMap.waterLevel;
        flooding = previousMap.flooding;


        gameCondition = previousMap.gameCondition;
        amountOfSteps = previousMap.amountOfSteps;
        score = previousMap.score;
        lamdasNumber = previousMap.lamdasNumber;
        maxLambdasNumber = previousMap.maxLambdasNumber;

        bot.setSpecies(Species.BOT);
        bot.setX(previousMap.bot.getX());
        bot.setY(previousMap.bot.getY());

        previousMap = previousMap.previousMap;
    }

    private GameMap copy() {
        GameMap gameMap = new GameMap();

        gameMap.mapObjects = new MapObject[maxX][maxY];


        for (int x = 0; x < maxX; x++)
            for (int y = 0; y < maxY; y++)
                try {
                    gameMap.mapObjects[x][y] = new MapObject(mapObjects[x][y].getSpecies(), x, y, mapObjects[x][y].getSymbol());
                } catch (NullPointerException e) {
                    gameMap.mapObjects[x][y] = new MapObject(mapObjects[x][y].getSpecies(), x, y);
                }


        gameMap.maxX = maxX;
        gameMap.maxY = maxY;
        gameMap.growth = growth;
        gameMap.razors = razors;

        gameMap.movesUnderWater = movesUnderWater;
        gameMap.waterLevel = waterLevel;
        gameMap.flooding = flooding;


        gameMap.gameCondition = gameCondition;
        gameMap.amountOfSteps = amountOfSteps;
        gameMap.score = score;
        gameMap.lamdasNumber = lamdasNumber;
        gameMap.maxLambdasNumber = maxLambdasNumber;

        gameMap.bot = new MapObject(Species.BOT, bot.getX(), bot.getY());

        gameMap.previousMap = previousMap;
        return gameMap;
    }

    public void moveAllObjects(NextStep botNextStep) {
        if (botNextStep == NextStep.BACK) {
            if (previousMap != null)
                backToLastCondition();
        } else {
            previousMap = this.copy();

            moveBot(botNextStep);

            GameMap workMap = this.copy();

            amountOfSteps++;
            score--;


            for (int x = 0; x < maxX; x++)
                for (int y = 0; y < maxY; y++)
                    switch (workMap.getMapObjects()[x][y].getSpecies()) {
                        case STONE:
                            // moveStone(x, y);
                            moveStoneSim(workMap, x, y);
                            break;
                        case LAMBDA_STONE:
                            moveLambdaStoneSim(workMap, x, y);
                            break;
                        case BEARD:
                            if (growth != 0)
                                if (amountOfSteps % growth == 0)
                                    growBeard(workMap, x, y);
                            break;
                        case C_LIFT:
                            if (lamdasNumber == maxLambdasNumber)
                                mapObjects[x][y].setSpecies(Species.O_LIFT);
                            break;
                        default:
                            break;
                    }

            raiseWaterLevel();


        }
        if (gameCondition == RB_DROWNED)
            score -= 1550;
        if (gameCondition == RB_CRUSHED)
            score -= 175;

    }
    //-----------------------------------------------------------------------------------


    //Override
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameMap)) return false;
        GameMap gameMap = (GameMap) o;
        return maxX == gameMap.maxX &&
                maxY == gameMap.maxY &&
                growth == gameMap.growth &&
                razors == gameMap.razors &&
                movesUnderWater == gameMap.movesUnderWater &&
                maxMovesUnderWater == gameMap.maxMovesUnderWater &&
                waterLevel == gameMap.waterLevel &&
                flooding == gameMap.flooding &&
                amountOfSteps == gameMap.amountOfSteps &&
                score == gameMap.score &&
                lamdasNumber == gameMap.lamdasNumber &&
                maxLambdasNumber == gameMap.maxLambdasNumber &&
                Arrays.equals(mapObjects, gameMap.mapObjects) &&
                gameCondition == gameMap.gameCondition &&
                Objects.equals(bot, gameMap.bot) &&
                Objects.equals(previousMap, gameMap.previousMap);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(maxX, maxY, growth, razors, movesUnderWater, maxMovesUnderWater, waterLevel, flooding, gameCondition, amountOfSteps, score, lamdasNumber, maxLambdasNumber, bot, previousMap);
        result = 31 * result + Arrays.hashCode(mapObjects);
        return result;
    }
    //-----------------------------------------------------------------------------------


    //SETTERS
    //-----------------------------------------------------------------------------------
    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setRazors(int razors) {
        this.razors = razors;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setAmountOfSteps(int amountOfSteps) {
        this.amountOfSteps = amountOfSteps;
    }

    public void setGameCondition(GameCondition gameCondition) {
        this.gameCondition = gameCondition;
    }

    public void setLamdasNumber(int lamdasNumber) {
        this.lamdasNumber = lamdasNumber;
    }

    public void setMaxLambdasNumber(int maxLambdasNumber) {
        this.maxLambdasNumber = maxLambdasNumber;
    }

    public void setFlooding(int flooding) {
        this.flooding = flooding;
    }


    //GETTERS
    //-----------------------------------------------------------------------------------
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

    public int getWaterLevel() {
        return waterLevel;
    }

    public int getFlooding() {
        return flooding;
    }

    public int getMovesUnderWater() {
        return movesUnderWater;
    }

    public int getScore() {
        return score;
    }

    public int getMaxMovesUnderWater() {
        return maxMovesUnderWater;
    }

    public int getAmountOfSteps() {
        return amountOfSteps;
    }

    public int getLamdasNumber() {
        return lamdasNumber;
    }

    public int getMaxLambdasNumber() {
        return maxLambdasNumber;
    }

    public MapObject[][] getMapObjects() {
        return mapObjects;
    }


}