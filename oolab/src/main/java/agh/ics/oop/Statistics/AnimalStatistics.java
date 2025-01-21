package agh.ics.oop.Statistics;
import agh.ics.oop.Simulation;
import agh.ics.oop.model.mapElements.Animal;

import java.util.List;

public class AnimalStatistics {
    private final Animal animal;
    private final Simulation simulation;

    public AnimalStatistics(Animal animal, Simulation simulation) {
        this.animal = animal;
        this.simulation = simulation;
    }

    public List<Integer> getGenome() {
        return animal.getGenes().getGenes();
    }

    public int getActivePart() {
        return animal.getGenePartUsed();        //UWAGA ZWRCA INDEKS A NIE ZAWARTOŚĆ GENU
    }

    public int getEnergy() {
        return animal.getEnergy();
    }

    public int getEatenPlants() {
        return animal.getPlantEatenCount();
    }

    public int getChildrenCount() {
        return animal.getChildrenCount();
    }
    //offspring

    public int getAge() {
        return animal.getAgeDays();
    }

    public Integer getDeathDay() {
        return animal.getDeathDay();
    }
}
