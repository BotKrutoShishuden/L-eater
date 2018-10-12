package GameMap;

import java.util.ArrayList;
import java.util.HashMap;

public class PortalSystem {
    //координаты порталов
    private HashMap<Character, Integer> xOutValue;
    private HashMap<Character, Integer> yOutValue;
    //координаты порталов в списках (у одного выходного портала может быть несколько входных)
    private HashMap<Character, ArrayList<Integer>> xInListValue;
    private HashMap<Character, ArrayList<Integer>> yInListValue;

    public PortalSystem(ArrayList<Portal> inPortals, ArrayList<Portal> outPortals) {
        xOutValue = new HashMap<>();
        yOutValue = new HashMap<>();
        xInListValue = new HashMap<>();
        yInListValue = new HashMap<>();
        for (Portal outPortal : outPortals) {
            ArrayList<Integer> xInCoordinates = new ArrayList<>();
            ArrayList<Integer> yInCoordinates = new ArrayList<>();
            for (Portal inPortal : inPortals) {
                // если имя выхода для текущего элемента inPortals совпадает с именем текущего элемента в outPortals,
                // записываем координаты выхода для текущего портала inPortals
                if (inPortal.getExitName() == outPortal.getName()) {
                    xOutValue.put(inPortal.getName(), outPortal.getX());
                    yOutValue.put(inPortal.getName(), outPortal.getY());
                    //записываем координаты входов в список
                    xInCoordinates.add(inPortal.getX());
                    yInCoordinates.add(inPortal.getY());
                }
            }
            //сопоставляем выходы со входами
            xInListValue.put(outPortal.getName(), xInCoordinates);
            yInListValue.put(outPortal.getName(), yInCoordinates);
        }
    }

    //метод для копирования текущей системы порталов, используется в GameMap для бекапа карты
    public PortalSystem(PortalSystem copied) {
        xOutValue = new HashMap<>(copied.getxOutValue());
        yOutValue = new HashMap<>(copied.getyOutValue());
        yInListValue = new HashMap<>(copied.getyInListValue());
        xInListValue = new HashMap<>(copied.getxInListValue());
    }

    public int getXOutCoordinate(Character symbol) {
        return xOutValue.get(symbol);
    }

    public int getYOutCoordinate(Character symbol) {
        return yOutValue.get(symbol);
    }

    public ArrayList<Integer> getXCoordinateThatMustBeClosed(Character symbol) {
        return xInListValue.get(symbol);
    }

    public ArrayList<Integer> getYCoordinateThatWeMustBeClosed(Character symbol) {
        return yInListValue.get(symbol);
    }


    //GETTERS
    public HashMap<Character, Integer> getxOutValue() {
        return xOutValue;
    }

    public HashMap<Character, Integer> getyOutValue() {
        return yOutValue;
    }

    public HashMap<Character, ArrayList<Integer>> getxInListValue() {
        return xInListValue;
    }

    public HashMap<Character, ArrayList<Integer>> getyInListValue() {
        return yInListValue;
    }
}
