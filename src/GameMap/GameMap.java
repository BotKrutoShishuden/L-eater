package GameMap;

import Bot.*;

import static GameMap.PortalCondition.*;


import MapObject.MapObject;
import MapObject.Species;

import java.io.*;
import java.util.*;

import static GameMap.GameCondition.*;


public class GameMap {


    private MapObject mapObjects[][];
    private int maxX;
    private int maxY;
    private int growth = 25;
    private int razorsNumber = 0;
    private int thrownRazors = 0;
    private int beardsNumber = 0;

    private int movesUnderWater;
    private int maxMovesUnderWater = 10;            //ПРОСЬБА РЕДАКТИРОВАТЬ GameMap.copy()при добавлении полей
    private int waterLevel = 0;
    private int flooding = 0;

    private GameCondition gameCondition = STILL_MINING;
    private int amountOfSteps;
    private int score;
    private int lambdasNumber;
    private int maxLambdasNumber;
    private int earthNumber;

    private MapObject bot;
    private boolean[] collectedLambdas;
    private List<MapObject> lambdas;

    private GameMap previousMap;
    public static final boolean STORAGE_PREVIOUS_MAP = true;

    private PortalSystem portalSystem;

    //Статические методы генерации(вместо конструкторов)
    //-----------------------------------------------------------------------------------
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

        //Собираем информацию для о порталах
        HashMap<Character, Character> portalCompliance = new HashMap<>();
        try {


            StringBuilder trampolineInf = new StringBuilder(bufferedReader.readLine());
            while (!trampolineInf.toString().contains("Trampoline"))
                trampolineInf = new StringBuilder(bufferedReader.readLine());
            while (trampolineInf.toString().contains("Trampoline") &&
                    trampolineInf.toString().contains("targets")) {
                Character in = trampolineInf.charAt(11);
                Character out = trampolineInf.charAt(21);
                portalCompliance.put(in, out);
                trampolineInf = new StringBuilder(bufferedReader.readLine());

            }
        } catch (NullPointerException n) {
            //Порталов нет
        }


        gameMap.mapObjects = new MapObject[gameMap.maxX][gameMap.maxY];

        int currentX = 0;
        int currentY = 0;

        ArrayList<Portal> inPortals = new ArrayList<>();
        ArrayList<Portal> outPortals = new ArrayList<>();

        gameMap.lambdas = new ArrayList<>();

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
                    gameMap.lambdas.add(new MapObject(Species.LAMBDA, currentX, currentY));
                    break;
                case '@':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.LAMBDA_STONE, currentX, currentY);
                    gameMap.maxLambdasNumber++;
                    gameMap.lambdas.add(new MapObject(Species.LAMBDA, currentX, currentY));
                    break;
                case '.':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.EARTH, currentX, currentY);
                    gameMap.earthNumber++;
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
                    gameMap.thrownRazors++;
                    break;
                case 'W':
                    symbolDefined = true;
                    gameMap.mapObjects[currentX][currentY] =
                            new MapObject(Species.BEARD, currentX, currentY);
                    gameMap.beardsNumber++;
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

            if (portalCompliance.containsValue(currentSymbol)) {

                outPortals.add(new Portal(OUT, currentX, currentY, currentSymbol));

                gameMap.mapObjects[currentX][currentY] =
                        new MapObject(Species.PORTAL_OUT, currentX, currentY, currentSymbol);

                symbolDefined = true;

            } else if (portalCompliance.containsKey(currentSymbol)) {
                inPortals.add(
                        new Portal(IN, currentX, currentY, currentSymbol, portalCompliance.get(currentSymbol)));

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

        gameMap.collectedLambdas = new boolean[gameMap.lambdas.size()];

        bufferedReader.close();
        return gameMap;


    }


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

            return Integer.valueOf(currentLine.delete(0, paramName.length()).toString().trim());

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

                case "ABORTED":
                    return ABORTED;

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
            gameMap.razorsNumber = cutParamAfterWord(address, "Razors ");
            gameMap.flooding = cutParamAfterWord(address, "Flooding ");
            gameMap.maxMovesUnderWater = cutParamAfterWord(address, "Waterproof ");
            gameMap.waterLevel = cutParamAfterWord(address, "Water ");
            return gameMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GameMap();

    }

    public static NextStep[] cutSteps(String address) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(address));
        StringBuilder stringBuilder;
        do {
            stringBuilder = new StringBuilder(bufferedReader.readLine());

        } while (!stringBuilder.toString().equals("c"));

        stringBuilder = new StringBuilder(bufferedReader.readLine());
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
                    break;
                case 'S':
                    nextSteps[i] = NextStep.USE_RAZOR;
                    break;
                case 'A':
                    nextSteps[i] = NextStep.ABORT;
                    break;
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
                case 'A' :
                    nextSteps[i] = (NextStep.ABORT);
                    break;
                default:
                    nextSteps[i] = (NextStep.WAIT);
                    break;

            }


        }
        return nextSteps;
    }


    //Методы для изменения карты
    //-----------------------------------------------------------------------------------
    public int getLambdaIndexFromCoordinates(int x, int y) {
        int i = 0;
        for (MapObject lambda : lambdas) {
            if (lambda.getX() == x && lambda.getY() == y && !collectedLambdas[i])
                return i;
            i++;
        }
        return -1;
    }

    private void moveBot(NextStep botNextStep) {
        switch (botNextStep) {

            case LEFT:
                if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX() - 1][bot.getY()].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX() - 1][bot.getY()].getSymbol());

                    //Закрываем входы связанные с выходом
                    ArrayList<Integer> xCoordinateThatMustBeClosed =
                            portalSystem.getXCoordinateThatMustBeClosed(mapObjects[newX][newY].getSymbol());
                    ArrayList<Integer> yCoordinateThatMustBeClosed =
                            portalSystem.getYCoordinateThatWeMustBeClosed(mapObjects[newX][newY].getSymbol());
                    for (int i = 0; i < xCoordinateThatMustBeClosed.size(); i++)
                        mapObjects[xCoordinateThatMustBeClosed.get(i)][yCoordinateThatMustBeClosed.get(i)].
                                setSpecies(Species.AIR);

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
                        else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE) {
                            mapObjects[bot.getX() - 2][bot.getY()].setSpecies(Species.LAMBDA_STONE);
                            moveLambdaList(bot.getX() - 1, bot.getY(), bot.getX() - 2, bot.getY());
                        }

                        mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.BOT);
                        bot.setX(bot.getX() - 1);

                        mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.AIR);

                    }
                } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.AIR ||//Просто идем
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.RAZOR) {


                    if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.LAMBDA) {
                        collectedLambdas[getLambdaIndexFromCoordinates(bot.getX() - 1, bot.getY())] = true;
                        score += 50;
                        lambdasNumber++;
                    } else if (mapObjects[bot.getX() - 1][bot.getY()].getSpecies() == Species.RAZOR)
                        razorsNumber++;


                    mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.BOT);
                    bot.setX(bot.getX() - 1);


                    mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.AIR);


                }


                break;

            case RIGHT:
                if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX() + 1][bot.getY()].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX() + 1][bot.getY()].getSymbol());

                    //Закрываем входы связанные с выходом
                    ArrayList<Integer> xCoordinateThatMustBeClosed =
                            portalSystem.getXCoordinateThatMustBeClosed(mapObjects[newX][newY].getSymbol());
                    ArrayList<Integer> yCoordinateThatMustBeClosed =
                            portalSystem.getYCoordinateThatWeMustBeClosed(mapObjects[newX][newY].getSymbol());
                    for (int i = 0; i < xCoordinateThatMustBeClosed.size(); i++)
                        mapObjects[xCoordinateThatMustBeClosed.get(i)][yCoordinateThatMustBeClosed.get(i)].
                                setSpecies(Species.AIR);

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
                        else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA_STONE) {
                            mapObjects[bot.getX() + 2][bot.getY()].setSpecies(Species.LAMBDA_STONE);
                            moveLambdaList(bot.getX() + 1, bot.getY(), bot.getX() + 2, bot.getY());
                        }


                        mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.BOT);
                        bot.setX(bot.getX() + 1);

                        mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.AIR);


                    }
                } else if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.AIR ||//Просто идем
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.RAZOR) {


                    if (mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.LAMBDA) {
                        collectedLambdas[getLambdaIndexFromCoordinates(bot.getX() + 1, bot.getY())] = true;
                        score += 50;
                        lambdasNumber++;
                    } else if ((mapObjects[bot.getX() + 1][bot.getY()].getSpecies() == Species.RAZOR))
                        razorsNumber++;

                    mapObjects[bot.getX() + 1][bot.getY()].setSpecies(Species.BOT);
                    bot.setX(bot.getX() + 1);

                    mapObjects[bot.getX() - 1][bot.getY()].setSpecies(Species.AIR);


                }


                break;

            case UP:
                if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX()][bot.getY() - 1].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX()][bot.getY() - 1].getSymbol());

                    //Закрываем входы связанные с выходом
                    ArrayList<Integer> xCoordinateThatMustBeClosed =
                            portalSystem.getXCoordinateThatMustBeClosed(mapObjects[newX][newY].getSymbol());
                    ArrayList<Integer> yCoordinateThatMustBeClosed =
                            portalSystem.getYCoordinateThatWeMustBeClosed(mapObjects[newX][newY].getSymbol());
                    for (int i = 0; i < xCoordinateThatMustBeClosed.size(); i++)
                        mapObjects[xCoordinateThatMustBeClosed.get(i)][yCoordinateThatMustBeClosed.get(i)].
                                setSpecies(Species.AIR);

                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX][oldY - 1].setSpecies(Species.AIR);

                } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.AIR || //Просто идем
                        mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.RAZOR) {

                    if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.LAMBDA) {

                        collectedLambdas[getLambdaIndexFromCoordinates(bot.getX(), bot.getY() - 1)] = true;
                        score += 50;
                        lambdasNumber++;

                    } else if (mapObjects[bot.getX()][bot.getY() - 1].getSpecies() == Species.RAZOR)
                        razorsNumber++;

                    bot.setY(bot.getY() - 1);
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.BOT);

                    mapObjects[bot.getX()][bot.getY() + 1].setSpecies(Species.AIR);

                }


                break;

            case DOWN:

                if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.O_LIFT) {
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.AIR);
                    gameCondition = WIN;
                } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.PORTAL_IN) {//Проверяем портал
                    int newX = portalSystem.getXOutCoordinate(mapObjects[bot.getX()][bot.getY() + 1].getSymbol());
                    int newY = portalSystem.getYOutCoordinate(mapObjects[bot.getX()][bot.getY() + 1].getSymbol());

                    //Закрываем входы связанные с выходом
                    ArrayList<Integer> xCoordinateThatMustBeClosed =
                            portalSystem.getXCoordinateThatMustBeClosed(mapObjects[newX][newY].getSymbol());
                    ArrayList<Integer> yCoordinateThatMustBeClosed =
                            portalSystem.getYCoordinateThatWeMustBeClosed(mapObjects[newX][newY].getSymbol());
                    for (int i = 0; i < xCoordinateThatMustBeClosed.size(); i++)
                        mapObjects[xCoordinateThatMustBeClosed.get(i)][yCoordinateThatMustBeClosed.get(i)].
                                setSpecies(Species.AIR);

                    int oldX = bot.getX();
                    int oldY = bot.getY();
                    bot.setX(newX);
                    bot.setY(newY);
                    mapObjects[newX][newY].setSpecies(Species.BOT);

                    mapObjects[oldX][oldY].setSpecies(Species.AIR);
                    mapObjects[oldX][oldY + 1].setSpecies(Species.AIR);

                } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.AIR || //Просто идем
                        mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.EARTH ||
                        mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.LAMBDA ||
                        mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.RAZOR) {

                    if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.LAMBDA) {
                        collectedLambdas[getLambdaIndexFromCoordinates(bot.getX(), bot.getY() + 1)] = true;
                        score += 50;
                        lambdasNumber++;
                    } else if (mapObjects[bot.getX()][bot.getY() + 1].getSpecies() == Species.RAZOR)
                        razorsNumber++;


                    bot.setY(bot.getY() + 1);
                    mapObjects[bot.getX()][bot.getY()].setSpecies(Species.BOT);

                    mapObjects[bot.getX()][bot.getY() - 1].setSpecies(Species.AIR);

                }

                break;

            case USE_RAZOR:
                if (razorsNumber != 0) {
                    razorsNumber--;
                    for (int i = bot.getX() - 1; i < bot.getX() + 2; i++)
                        for (int j = bot.getY() - 1; j < bot.getY() + 2; j++) {
                            MapObject current = mapObjects[i][j];
                            if (current.getSpecies() == Species.BEARD)
                                current.setSpecies(Species.AIR);
                        }

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
                        workMap.getMapObjects()[x - 1][y].getSpecies() == Species.AIR &&
                        workMap.getMapObjects()[x][y + 1].getSpecies() != Species.LAMBDA) {//Влево
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.STONE);
                    if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            }
        }
    }

    private void moveLambdaList(int oldX, int oldY, int newX, int newY) {
        for (int i = 0; i < lambdas.size(); i++)
            if (lambdas.get(i).getX() == oldX && lambdas.get(i).getY() == oldY) {
                // lambda.setCoordinates(newX, newY);
                lambdas.set(i, new MapObject(Species.LAMBDA, newX, newY));
            }
    }

    private void moveLambdaStoneSim(GameMap workMap, int x, int y) {

        if (workMap.getMapObjects()[x][y + 1].getSpecies() == Species.AIR) {// проверяем что снизу ничего нет

            if (workMap.getMapObjects()[x][y + 2].getSpecies() == Species.AIR) {//Если не разбивается
                mapObjects[x][y].setSpecies(Species.AIR);
                mapObjects[x][y + 1].setSpecies(Species.LAMBDA_STONE);
                moveLambdaList(x, y, x, y + 1);
            } else {
                mapObjects[x][y].setSpecies(Species.AIR);
                mapObjects[x][y + 1].setSpecies(Species.LAMBDA);
                moveLambdaList(x, y, x, y + 1);
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
                    moveLambdaList(x, y, x + 1, y + 1);
                } else {
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x + 1][y + 1].setSpecies(Species.LAMBDA);
                    moveLambdaList(x, y, x + 1, y + 1);
                    if (workMap.getMapObjects()[x + 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            } else if (workMap.getMapObjects()[x - 1][y].getSpecies() == Species.AIR &&
                    workMap.getMapObjects()[x - 1][y + 1].getSpecies() == Species.AIR &&
                    workMap.getMapObjects()[x][y + 1].getSpecies() != Species.LAMBDA) {//Влево
                if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.AIR) {//Если не разбивается
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.LAMBDA_STONE);
                    moveLambdaList(x, y, x - 1, y + 1);
                } else {
                    mapObjects[x][y].setSpecies(Species.AIR);
                    mapObjects[x - 1][y + 1].setSpecies(Species.LAMBDA);
                    moveLambdaList(x, y, x - 1, y + 1);
                    if (workMap.getMapObjects()[x - 1][y + 2].getSpecies() == Species.BOT) {// если падает на бота
                        gameCondition = RB_CRUSHED;
                    }
                }
            }
        }
    }


    private void growBeard(GameMap workMap, int xBeard, int yBeard) {
        if (beardsNumber != 0) {
            for (int i = xBeard - 1; i < xBeard + 2; i++)
                for (int j = yBeard - 1; j < yBeard + 2; j++) {
                    try {
                        MapObject current = workMap.getMapObjects()[i][j];
                        if (current.getSpecies() == Species.AIR)
                            mapObjects[i][j].setSpecies(Species.BEARD);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
        }
    }

    private void raiseWaterLevel() {
        if (bot.getY() >= getMaxY() - waterLevel)
            movesUnderWater++;
        else
            movesUnderWater = 0;

        if (flooding != 0 && amountOfSteps % flooding == 0 && amountOfSteps != 0)
            waterLevel++;

        if (gameCondition == WIN)
            return;

        if (movesUnderWater > maxMovesUnderWater)
            gameCondition = RB_DROWNED;
    }


    private void backToLastCondition() {
        mapObjects = previousMap.copyMapObjects();
        maxX = previousMap.maxX;
        maxY = previousMap.maxY;
        growth = previousMap.growth;
        razorsNumber = previousMap.razorsNumber;
        thrownRazors = previousMap.thrownRazors;
        beardsNumber = previousMap.beardsNumber;

        movesUnderWater = previousMap.movesUnderWater;
        maxMovesUnderWater = previousMap.maxMovesUnderWater;
        waterLevel = previousMap.waterLevel;
        flooding = previousMap.flooding;


        gameCondition = previousMap.gameCondition;
        amountOfSteps = previousMap.amountOfSteps;
        score = previousMap.score;
        lambdasNumber = previousMap.lambdasNumber;
        maxLambdasNumber = previousMap.maxLambdasNumber;
        earthNumber = previousMap.earthNumber;

        bot.setSpecies(Species.BOT);
        bot.setX(previousMap.bot.getX());
        bot.setY(previousMap.bot.getY());

        collectedLambdas = previousMap.collectedLambdas;
        lambdas = previousMap.lambdas;
        portalSystem = previousMap.portalSystem;
        previousMap = previousMap.previousMap;


    }

    private boolean[] copyCollectedLambdas() {
        boolean copiedCollLamdas[] = new boolean[collectedLambdas.length];
        int i = 0;
        for (boolean lambda : collectedLambdas) {
            copiedCollLamdas[i] = lambda;
            i++;
        }
        return copiedCollLamdas;
    }

    private MapObject[][] copyMapObjects() {
        MapObject[][] copiedMapObjects = new MapObject[maxX][maxY];
        for (int x = 0; x < maxX; x++)
            for (int y = 0; y < maxY; y++)
                try {
                    copiedMapObjects[x][y] = new MapObject(mapObjects[x][y].getSpecies(), x, y, mapObjects[x][y].getSymbol());
                } catch (NullPointerException e) {
                    copiedMapObjects[x][y] = new MapObject(mapObjects[x][y].getSpecies(), x, y);
                }
        return copiedMapObjects;
    }

    public GameMap copy() {
        GameMap copyMap = new GameMap();

        copyMap.mapObjects = copyMapObjects();

        copyMap.mapObjects = copyMapObjects();
        copyMap.maxX = maxX;
        copyMap.maxY = maxY;
        copyMap.growth = growth;
        copyMap.razorsNumber = razorsNumber;
        copyMap.thrownRazors = thrownRazors;
        copyMap.beardsNumber = beardsNumber;

        copyMap.movesUnderWater = movesUnderWater;
        copyMap.maxMovesUnderWater = maxMovesUnderWater;
        copyMap.waterLevel = waterLevel;
        copyMap.flooding = flooding;

        copyMap.gameCondition = gameCondition;
        copyMap.amountOfSteps = amountOfSteps;
        copyMap.score = score;
        copyMap.lambdasNumber = lambdasNumber;
        copyMap.maxLambdasNumber = maxLambdasNumber;
        copyMap.earthNumber = earthNumber;

        copyMap.bot = new MapObject(bot.getSpecies(), bot.getX(), bot.getY());
        copyMap.collectedLambdas = copyCollectedLambdas();
        copyMap.lambdas = new ArrayList<>(lambdas);

        copyMap.portalSystem = new PortalSystem(portalSystem);

        copyMap.previousMap = previousMap;

        return copyMap;

    }

    public void moveAllObjects(NextStep botNextStep) {
        if (botNextStep == NextStep.BACK && STORAGE_PREVIOUS_MAP) {
            if (previousMap != null)
                backToLastCondition();

        } else if (gameCondition != GameCondition.STILL_MINING)
            return;

        else if (botNextStep == NextStep.ABORT) {
            amountOfSteps++;
            gameCondition = GameCondition.ABORTED;
            return;
        } else {
            if (STORAGE_PREVIOUS_MAP)
                previousMap = this.copy();

            moveBot(botNextStep);

            GameMap workMap = this.copy();


            amountOfSteps++;
            raiseWaterLevel();

            for (int x = 0; x < maxX; x++)
                for (int y = 0; y < maxY; y++)
                    switch (workMap.getMapObjects()[x][y].getSpecies()) {
                        case STONE:
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
                            if (lambdasNumber == maxLambdasNumber)
                                mapObjects[x][y].setSpecies(Species.O_LIFT);
                            break;
                        default:
                            break;
                    }

            //Контроль слившихся лямбд
            for (int i = 0; i < lambdas.size(); i++)
                for (int j = 0; j < lambdas.size(); j++)
                    if (i != j && !collectedLambdas[i] && !collectedLambdas[j])
                        if (lambdas.get(i).getX() == lambdas.get(j).getX() &&
                                lambdas.get(i).getY() == lambdas.get(j).getY()) {
                            lambdas.remove(j);
                            maxLambdasNumber--;
                            break;
                        }


        }
        score--;
        switch (gameCondition) {
            case RB_DROWNED:
                score -= collectedLambdas.length * 25;
                break;
            case RB_CRUSHED:
                score -= collectedLambdas.length * 25; //такие вот дела
                break;
            case ABORTED:
                score += 1;
                amountOfSteps--;
                break;
            case WIN:
                score += collectedLambdas.length * 25; //такие вот дела
                break;
            case STILL_MINING:
                break;
            case NULL_CONDITION:
                break;
        }


    }


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


    //SETTERS
    //-----------------------------------------------------------------------------------
    public void setGrowth(int growth) {
        this.growth = growth;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setRazorsNumber(int razorsNumber) {
        this.razorsNumber = razorsNumber;
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

    public void setLambdasNumber(int lambdasNumber) {
        this.lambdasNumber = lambdasNumber;
    }

    public void setMaxLambdasNumber(int maxLambdasNumber) {
        this.maxLambdasNumber = maxLambdasNumber;
    }

    public void setFlooding(int flooding) {
        this.flooding = flooding;
    }

    public void setMaxMovesUnderWater(int maxMovesUnderWater) {
        this.maxMovesUnderWater = maxMovesUnderWater;
    }

    public void setMovesUnderWater(int movesUnderWater) {
        this.movesUnderWater = movesUnderWater;
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

    public int getRazorsNumber() {
        return razorsNumber;
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

    public int getLambdasNumber() {
        return lambdasNumber;
    }


    public int getMaxLambdasNumber() {
        return maxLambdasNumber;
    }

    public MapObject[][] getMapObjects() {
        return mapObjects;
    }


    public List<MapObject> getLambdas() {
        return lambdas;
    }

    public int getEarthNumber() {
        return earthNumber;
    }

    public int getThrownRazors() {
        return thrownRazors;
    }

    public int getBeardsNumber() {
        return beardsNumber;
    }

    public boolean[] getCollectedLambdas() {
        return collectedLambdas;
    }

    public int getCollectedLambdasNumber() {
        int number = 0;
        for (boolean lamda : getCollectedLambdas())
            if (lamda)
                number++;

        return number;

    }

}