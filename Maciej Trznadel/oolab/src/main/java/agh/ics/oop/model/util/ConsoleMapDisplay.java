package agh.ics.oop.model.util;

import agh.ics.oop.model.WorldMap;

public class ConsoleMapDisplay implements MapChangeListener {
    int updateCount = 0;

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        synchronized(System.out) {
            updateCount++;
            System.out.println("Update " + updateCount + ": " + message);
            System.out.println("Map Id: " + worldMap.getId());
            System.out.println(worldMap);
        }
    }

}
