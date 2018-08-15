package GameMap;

import java.util.ArrayList;
import java.util.HashMap;

public class PortalSystem {
    private HashMap<Character, Integer> xOutValue;
    private HashMap<Character, Integer> yOutValue;
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
                if (inPortal.getExitName() == outPortal.getName()) {
                    xOutValue.put(inPortal.getName(), outPortal.getX());
                    yOutValue.put(inPortal.getName(), outPortal.getY());
                    xInCoordinates.add(inPortal.getX());
                    yInCoordinates.add(inPortal.getY());
                }
            }
            xInListValue.put(outPortal.getName(), xInCoordinates);
            yInListValue.put(outPortal.getName(), yInCoordinates);
        }
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
}
