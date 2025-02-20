package agh.ics.oop.Statistics;

import agh.ics.oop.model.maps.AbstractWorldMap;

import java.util.*;

public class SimulationStatistics {
    private final AbstractWorldMap map;
    
    public SimulationStatistics(AbstractWorldMap map) {
        this.map = map;
    }

    public int getFreeFieldsCount() {
        return map.getFreePositionsCount();
    }

    public int getLivingAnimalsNumber() {
        return map.getAllLivingAnimals().size();
    }

    public int getCurrentPlantsNumber() { return map.getGrassCount(); }

    public double getAverageEnergy() {
        return map.getAvgLivingAnimalsEnergy();
    }

    public double getAverageLifeSpan() {
        return map.getAvgDeadAnimalsLifespan();
    }

    public double getAverageChildrenCount() {
        return map.getAvgChildrenCount();
    }

    public List<Integer> getMostCommonGenotypes() { return map.getMostCommonGenotypes(); }
}