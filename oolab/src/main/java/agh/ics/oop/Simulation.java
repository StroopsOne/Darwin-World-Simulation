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
    private final int grassValue;
    private final Object lock = new Object();       //kontrola zatrzymywania watkow
    private volatile boolean running = true;
    private final int dailyGrass;
    private int day=0;

    public Simulation(AbstractWorldMap map, int numberOfAnimals, int startEnergy, int genomeSize, int grassValue, int initialGrass, int dailyGrass) throws IncorrectPositionException {
        this.map = map;
        this.grassValue = grassValue;
        animalList = new ArrayList<>();
        map.placeStartObjects(numberOfAnimals,initialGrass,grassValue,startEnergy,genomeSize);
        this.dailyGrass=dailyGrass;
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
                    map.plantNewGrasses(dailyGrass,grassValue);
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
