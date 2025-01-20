package agh.ics.oop.model.util;

import agh.ics.oop.model.maps.WorldMap;

public class ConsoleMapDisplay implements MapChangeListener {
    int updateCount = 0;

    @Override
    public void mapChanged(WorldMap worldMap) {
        synchronized(System.out) {
            updateCount++;
            System.out.println("Map Id: " + worldMap.getId());
            System.out.println(worldMap);
        }
    }

}
