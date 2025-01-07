package agh.ics.oop.model.properities;
import agh.ics.oop.model.AbstractWorldMap;
import agh.ics.oop.model.Animal;

import java.util.*;

import static java.lang.reflect.Array.get;

public class Genomes {
    private final List<Integer>genes;

    public Genomes(int genomeSize){
        this.genes=new ArrayList<>();
        Random random= new Random();
        for (int i=0;i<genomeSize;i++){
            this.genes.add(random.nextInt(8));
        }
    }
    public Genomes(List<Integer>genes, int minGeneMutation, int maxGeneMutation, boolean slightCorrection){
        this.genes=genes;
        if (slightCorrection)   SlightCorrection(genes,minGeneMutation,maxGeneMutation);
        else RandomMutation(genes, minGeneMutation, maxGeneMutation);
    }

    public List<Integer>getGenes(){
        return genes;
    }

    public List<Integer>ChildGenes(Animal mom, Animal dad){     //mom, dad umowne, którsze nazwy od firstParent
        Animal strongerParent = mom.getEnergy() > dad.getEnergy() ? mom : dad;
        Animal weakerParent = mom.getEnergy() > dad.getEnergy() ? dad : mom;

        double totalEnergy= strongerParent.getEnergy()+ weakerParent.getEnergy();
        double percentageStrongerParent= strongerParent.getEnergy()/totalEnergy;        //weakerParent nie potrzebne, w praktyce prawdopodobieństwu genu tego rodzica
        Genomes strongerGenes= strongerParent.getGenes();     //zmienne dodane dla estetyki kodu
        Genomes weakerGenes= weakerParent.getGenes();

        List<Integer>childGenes=new ArrayList<>();
        Random random=new Random();
        for (int i = 0; i < strongerGenes.getGenes().size(); i++) {
            if (random.nextDouble() < percentageStrongerParent) {       //losowość z prawdopodobieństwem pojedynczy gen
                childGenes.add(strongerGenes.getGenes().get(i));
            } else {
                childGenes.add(weakerGenes.getGenes().get(i));
            }
        }
        return childGenes;

    }

    public void RandomMutation(List<Integer>genes,int mingeneMutation, int maxgeneMutation){
        List<Integer>mutatedGenes= new ArrayList<>();
        int numberOfMutations;
        Random random= new Random();
        if (mingeneMutation==maxgeneMutation)   numberOfMutations=mingeneMutation;
        else    numberOfMutations=random.nextInt(maxgeneMutation - mingeneMutation) + mingeneMutation;

        Set<Integer> mutatedIndices = new HashSet<>(); // Zbiór zmutowanych genów
        for (int i = 0; i < numberOfMutations; i++) {
            int randomIndex;

            // Znajdywanie losowego, jeszcze nie zmutowanego genu
            do {
                randomIndex = random.nextInt(genes.size());
            } while (mutatedIndices.contains(randomIndex));

            mutatedIndices.add(randomIndex);

            genes.set(randomIndex, random.nextInt(8));
        }

    }

    public void SlightCorrection(List<Integer>genes,int mingeneMutation, int maxgeneMutation){
        List<Integer>mutatedGenes= new ArrayList<>();
        Random random=new Random();
        int numberOfMutations;
        if (mingeneMutation==maxgeneMutation)   numberOfMutations=mingeneMutation;
        else    numberOfMutations=random.nextInt(maxgeneMutation - mingeneMutation) + mingeneMutation;

        Set<Integer> mutatedIndices = new HashSet<>();

        for (int i = 0; i < numberOfMutations; i++) {
            int randomIndex;

            // Znajdywanie losowego, jeszcze nie zmutowanego genu
            do {
                randomIndex = random.nextInt(genes.size());
            } while (mutatedIndices.contains(randomIndex));

            mutatedIndices.add(randomIndex);

            boolean mutateUpper= random.nextBoolean();
            if (mutateUpper)    genes.set(randomIndex,genes.get(randomIndex)+1);
            else genes.set(randomIndex,genes.get(randomIndex)-1);
        }
    }

}
