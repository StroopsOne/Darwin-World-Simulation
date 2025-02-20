package agh.ics.oop.Statistics;
import agh.ics.oop.Simulation;
import agh.ics.oop.model.mapElements.Animal;

import java.util.List;

public class AnimalStatistics {
    private final Animal animal;


    public AnimalStatistics(Animal animal) {
        this.animal = animal;

    }

    public int getEnergy() {
        return animal.getEnergy();
    }

    public int getEatenPlants() {
        return animal.getPlantEatenCount();
    }

    public int getAge() {
        return animal.getAgeDays();
    }

    public int getChildrenCount() {
        return animal.getChildrenCount();
    }

    public List<Integer> getGenome() { return animal.getGenes().getGenes(); }

    public int getActivePart() { return animal.getGenePartUsed(); } //UWAGA ZWRACA INDEKS A NIE ZAWARTOŚĆ GENU

    public Integer getDeathDay() { return animal.getDeathDay(); }
}
