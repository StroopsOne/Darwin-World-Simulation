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
    private final Object lock = new Object();       //kontrola zatrzymywania watkow
    private volatile boolean running = true;
    private int day=0;
    private AnimalFabric fabric;

    public Simulation(List<Vector2d> startPositions, WorldMap mapAnimals, AbstractWorldMap map, int numberOfAnimals, int startEnergy, int genomeSize, int reproductionEnergy, int parentingEnergy, int mingeneMutation, int maxgeneMutation, int grassValue, int initialGrass, boolean slightCorrection) throws IncorrectPositionException {
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
        this.fabric=new AnimalFabric(mingeneMutation,maxgeneMutation,parentingEnergy,genomeSize,slightCorrection,grassValue,map);
        map.placeStartObjects(initialAnimals,initialGrass,grassValue,startEnergy,genomeSize);
    }

    public void pauseSimulation() {
        running = false;
    }

    public void resumeSimulation() {
        running = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    public List<Animal> getAnimalListCopy() {
        return new ArrayList<>(animalList);
    }
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (lock) {
                    while (!running) {
                        lock.wait();
                    }
                }
                try {
                    Thread.sleep(1000);     //przerwa między dniami
                    map.removeDeadAnimals(day);
                    map.moveAllAnimals();
                    map.animalsEatGrasses();
                    map.animalsReproduce();
                    //Co tu, ile dziennie ma być trawy?
                    //map.plantNewGrasses();
                    day++;
                } catch (IncorrectPositionException e) {
                    System.out.println("Blad w poruszaniu zwierzat");
                }
            }
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
