package agh.ics.oop.model;

import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.properities.Genomes;

import java.util.ArrayList;
import java.util.List;

import static agh.ics.oop.model.properities.Genomes.ChildGenes;

public class AnimalFabric{
    private final int mingeneMutation;
    private final int maxgeneMutation;
    private final int reproductionEnergy;
    private final int parentingEnergy;
    private final int geneSize;
    private final boolean slightCorrection;
    private final int plantValue;
    private List<Animal> animals;
    private final AbstractWorldMap map;

    public AnimalFabric(int mingeneMutation, int maxgeneMutation, int reproductionEnergy, int parentingEnergy, int geneSize, boolean slightCorrection, int plantValue, AbstractWorldMap map){
        this.mingeneMutation = mingeneMutation;
        this.maxgeneMutation = maxgeneMutation;
        this.reproductionEnergy = reproductionEnergy;
        this.parentingEnergy = parentingEnergy;
        this.geneSize = geneSize;
        this.slightCorrection = slightCorrection;
        this.plantValue = plantValue;
        this.map = map;
        animals=new ArrayList<>();
    }

    //klasa która przechowuje konfigurację robi rzeczy z nią związane, oryginalnie jej sensem było to żeby to ona wstawiała obiekty na mapę

    //można tu dac na przykład

    //placeStartAnimals

    //AnimalsEatGrasses

    //plantNewGrasses

    //removeDeadAnimals

    //tylko pytanie które i ile żeby mapa też nei była pusta


    /*
    public void dayPasses(int day){
        for (Animal animal : animals){
            animal.changeEnergy(-1);
            if (animal.getEnergy()==0){
                animal.killAnimal(day);
            }else{
                animal.incermentAge();
            }
        }
    }
     */


    public int getReproductionEnergy(){             //getter musi istnieć bo metoda do reprodukcji nie ma tej zmiennej (jest w mapie)
        return reproductionEnergy;
    }

    //dzięki temu że jest tu reprodukcja konstuktor Animala już nie straszy
    public Animal reproduce(Animal mom, Animal dad) {
        List<Integer> childGenes = ChildGenes(mom, dad);
        Genomes childGenome=new Genomes(childGenes,mingeneMutation,maxgeneMutation,slightCorrection);
        mom.changeEnergy(-parentingEnergy);
        dad.changeEnergy(-parentingEnergy);
        mom.addChild();
        dad.addChild();
        Animal child=new Animal(mom.getPosition(),parentingEnergy,childGenome,mom,dad);
        return child;
    }

}
