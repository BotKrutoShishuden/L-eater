import MapObject.MapObject;
import MapObject.NextStep;
import MapObject.Species;

import javax.lang.model.element.UnknownElementException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class GameMap {
    private MapObject mapObjects[][];
    private int maxX;
    private int maxY;
    private int growth;
    private int razors;
    private GameMap lastCondition;


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
            maxY -= 1;

            mapObjects = new MapObject[maxX][maxY];
            this.maxX = maxX;
            this.maxY = maxY;
            int botCounter = 0;
            int currentY = 0;
            currentX = 0;

            bufferedReader = new BufferedReader(new FileReader(address));

            while (currentY < maxY) {
                c = bufferedReader.read();
                boolean finded = false;
                switch (c) {
                    case 'R':
                        if (botCounter == 0) {
                            finded = true;
                            mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.BOT);
                            botCounter++;
                        }
                        break;
                    case 'L':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.LIFT);
                        break;
                    case '#':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.WALL);
                        break;
                    case '*':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.STONE);
                        break;
                    case 92:
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.LAMBDA);
                        break;
                    case '.':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.EARTH);
                        break;
                    case ' ':
                        finded = true;
                        mapObjects[currentX][currentY] = new MapObject(NextStep.STAY, Species.AIR);
                        break;
                    case '\n':
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

    /*Вызвается в конце каждого хода
    и передвигает объекты*/
    public void moveAllObjects(NextStep botNextStep) {


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


}

