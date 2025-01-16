package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.WorldMap;
import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animalList;
    private final AbstractWorldMap map;
    private final int initialAnimals;
    private final int startEnergy;
    private final int genomeSize;
    private final int reproductionEnergy;
    private final int parentingEnergy;
    private final int mingeneMutation;
    private final int maxgeneMutation;
    private final int grassValue;
    private final int initialGrass;
    private volatile boolean running = true;
    private int day=0;


    public Simulation(List<Vector2d> startPositions, WorldMap mapAnimals, AbstractWorldMap map, int numberOfAnimals, int startEnergy, int genomeSize, int reproductionEnergy, int parentingEnergy, int mingeneMutation, int maxgeneMutation, int grassValue, int initialGrass) throws IncorrectPositionException {
        this.map = map;
        this.initialAnimals = numberOfAnimals;
        this.startEnergy = startEnergy;
        this.genomeSize = genomeSize;
        this.reproductionEnergy = reproductionEnergy;
        this.parentingEnergy = parentingEnergy;
        this.mingeneMutation = mingeneMutation;
        this.maxgeneMutation = maxgeneMutation;
        this.grassValue = grassValue;
        this.initialGrass = initialGrass;
        animalList = new ArrayList<>();
        map.placeStartObjects(initialAnimals,initialGrass,grassValue);
    }

    public void pauseSimulation() {
        running = false;
    }


    public List<Animal> getAnimalListCopy() {
        return new ArrayList<>(animalList);
    }
    public void run() {
        while (running){
            Thread.sleep(1000);     //przerwa między dniami
            map.removeDeadAnimals(day);
            map.moveAllAnimals();
            map.animalsEatGrasses();
            map.animalsReproduce();
            //dodanie trawy
            day++;
        }
    }
}
