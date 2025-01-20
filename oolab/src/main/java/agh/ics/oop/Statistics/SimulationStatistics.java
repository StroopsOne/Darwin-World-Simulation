package agh.ics.oop.Statistics;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.properities.Genomes;


import java.util.*;
import java.util.stream.Collectors;

public class SimulationStatistics {
    private final AbstractWorldMap map;
    
    public SimulationStatistics(Simulation simulation, AbstractWorldMap map) {
        this.map = map;
    }

    public int getTotalAnimals() {
        return map.getAllLivingAnimals().size()+map.getDeadAnimalsCount();
    }

    public int getTotalPlants() {
        return map.getGrassCount();       //wszystkie kiedykolwiek
    }

    public int getFreeFields() {
        return map.getFreeFields();
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

    public double getAverageEnergy() {
        return map.getAvgLivingAnimalsEnergy();
    }

    public double getAverageLifeSpan() {
        return map.getAvgDeadAnimalsLifespan();
    }

    public double getAverageChildrenCount() {
        return map.getAvgChildrenCount();
    }

    @Override
    public String toString() {
        return "SimulationStatistics{" +
                "totalAnimals=" + getTotalAnimals() +
                ", totalPlants=" + getTotalPlants() +
                ", freeFields=" + getFreeFields() +
                ", mostCommonGenotypes=" + getMostCommonGenotypes() +
                ", averageEnergy=" + String.format("%.2f", getAverageEnergy()) +
                ", averageLifeSpan=" + String.format("%.2f", getAverageLifeSpan()) +
                ", averageChildrenCount=" + String.format("%.2f", getAverageChildrenCount()) +
                '}';
    }
}