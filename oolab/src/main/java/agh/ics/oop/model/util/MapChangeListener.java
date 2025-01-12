package agh.ics.oop.model.util;

import agh.ics.oop.model.maps.WorldMap;

public interface MapChangeListener {

    void mapChanged(WorldMap worldMap, String message);
}
