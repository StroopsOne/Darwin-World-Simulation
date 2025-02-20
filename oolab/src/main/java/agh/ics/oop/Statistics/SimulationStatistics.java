package agh.ics.oop.Statistics;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.properities.Genomes;


import java.util.*;
import java.util.stream.Collectors;

public class SimulationStatistics {
    private final AbstractWorldMap map;
    
    public SimulationStatistics(AbstractWorldMap map) {
        this.map = map;
    }

    public int getFreeFieldsCount() {
        return map.getFreePositionsCount();
    }

    public int getAnimalNumber() {
        return map.getAllLivingAnimals().size();
    }

    public int getPlantsNumber() {
        return map.getGrassCount();       //wszystkie kiedykolwiek
    }

    public double getAverageEnergy() {
        return map.getAvgLivingAnimalsEnergy();
    }

    public double getAverageLifeSpan() {
        return map.getAvgDeadAnimalsLifespan();
    }

    public double getAverageChildrenCount() {
        return map.getAvgChildrenCount();
    }

    public List<Integer> getMostCommonGenotypes() {
        List<Animal> animals = new ArrayList<>(map.getAllLivingAnimals());
        animals.addAll(map.getDeadAnimals());
        Map<Genomes, Long> genotypesCount = animals.stream()
                .collect(Collectors.groupingBy(Animal::getGenes, Collectors.counting()));

        long maxCount = Collections.max(genotypesCount.values());

        return genotypesCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .map(Genomes::getGenes)
                .orElse(Collections.emptyList());
    }



    @Override
    public String toString() {
        return "SimulationStatistics{" +
                "freeFields=" + getFreeFieldsCount() +
                ", livingAnimals=" + getAnimalNumber() +
                ", Plants=" + getPlantsNumber() +
                ", averageEnergy=" + String.format("%.2f", getAverageEnergy()) +
                ", averageLifeSpan=" + String.format("%.2f", getAverageLifeSpan()) +
                ", averageChildrenCount=" + String.format("%.2f", getAverageChildrenCount()) +
                ", mostCommonGenotypes=" + getMostCommonGenotypes() +
                '}';
    }
}