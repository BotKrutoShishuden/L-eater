package GameMap;

import java.util.ArrayList;
import java.util.HashMap;

public class PortalSystem {
    private HashMap<Character, Integer> xCoordinates;
    private HashMap<Character, Integer> yCoordinates;

    public PortalSystem(ArrayList<Portal> inPortals, ArrayList<Portal> outPortals) {
        xCoordinates = new HashMap<>();
        yCoordinates = new HashMap<>();
        for (Portal inPortal : inPortals)
            for (Portal outPortal : outPortals)
                if ((inPortal.getSymbol() == 'A' && outPortal.getSymbol() == '1') ||
                        (inPortal.getSymbol() == 'B' && outPortal.getSymbol() == '2') ||
                        (inPortal.getSymbol() == 'C' && outPortal.getSymbol() == '3') ||
                        (inPortal.getSymbol() == 'D' && outPortal.getSymbol() == '4') ||
                        (inPortal.getSymbol() == 'E' && outPortal.getSymbol() == '5') ||
                        (inPortal.getSymbol() == 'F' && outPortal.getSymbol() == '6') ||
                        (inPortal.getSymbol() == 'G' && outPortal.getSymbol() == '7')) {
                    xCoordinates.put(inPortal.getSymbol(), outPortal.getX());
                    yCoordinates.put(inPortal.getSymbol(), outPortal.getY());
                }


    }

    public int getXOutCoordinate(Character symbol) {
        return xCoordinates.get(symbol);
    }

    public int getYOutCoordinate(Character symbol) {
        return yCoordinates.get(symbol);
    }

}
