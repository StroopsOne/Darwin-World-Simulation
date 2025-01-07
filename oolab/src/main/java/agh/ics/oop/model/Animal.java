package agh.ics.oop.model;

import agh.ics.oop.model.properities.Genomes;

import java.util.List;

public class Animal implements WorldElement {


    private MapDirection orientation;
    private Vector2d position;
    public final static Vector2d  defaultVector = new Vector2d(2, 2);
    public MapDirection getOrientation() {
        return orientation;
    }
    private int energy;
    private final int ReproductionEnergy;       //ilość energii ustalona do rozmnażania
    private final Genomes genes;
    private int ageDays;
    private int deathDay;
    private final int mingeneMutation;
    private final int maxgeneMutation;
    private Animal parent1;
    private Animal parent2;
    private int childrenCount;
    private final int parentingEnergy;  //energia zużywana przez posiadanie dzieci

    public Vector2d getPosition() {

        return position;
    }
    public Animal(Vector2d vector, int startEnergy, int geneSize, int reproductionEnergy,int mingeneMutation, int maxgeneMutation, int parentingEnergy){
        this.orientation = MapDirection.NORTH;
        this.position = vector;
        this.energy=startEnergy;
        this.genes=new Genomes(geneSize);
        this.ageDays=0;
        this.deathDay=-1;       //czyli zwierzak żyje
        this.ReproductionEnergy=reproductionEnergy;
        this.mingeneMutation=mingeneMutation;
        this.maxgeneMutation=maxgeneMutation;
        this.parent1=null;
        this.parent2=null;
        this.childrenCount=0;
        this.parentingEnergy=parentingEnergy;
    }
    //konstruktor dla tworzenia dzieci
    public Animal(Vector2d position, int inheritedEnergy, Genomes genomes, int reproductionEnergy,int mingeneMutation, int maxgeneMutation,Animal parent1,Animal parent2, int parentingEnergy){
        this.orientation = MapDirection.NORTH;
        this.position = position;
        this.energy=inheritedEnergy;
        this.genes=genomes;
        this.ageDays=0;
        this.deathDay=-1;       //czyli zwierzak żyje
        this.ReproductionEnergy=reproductionEnergy;
        this.mingeneMutation=mingeneMutation;
        this.maxgeneMutation=maxgeneMutation;
        this.parent1=parent1;
        this.parent2=parent2;
        this.childrenCount=0;
        this.parentingEnergy=parentingEnergy;

    }

    public int getEnergy() {
        return energy;
    }

    public boolean isAlive(){
        return deathDay==-1;
    }

    public void dayPasses(){
        energy=energy-1;
        if (energy==0){
            //przypisanie dnia śmierci, trzeba mieć czas zaimplementowany
            //czas chyba do simulation
        }else ageDays+=1;
    }

    public int getAge(){
        return ageDays;
    }

    public int getDeathDay(){
        return deathDay;
    }

    public Genomes getGenes(){
        return genes;
    }

    public int GetNumberOfChildren(){
        return childrenCount;
    }

    public void killedBySowoniedzwiedz(){
        //przypisanie deathDay to samo co w dayPasses
    }

    public void plantEatten(int plantValue){
        energy+=plantValue;
    }

    public boolean ReadyForKids(){
        if (energy>ReproductionEnergy)  return true;
        else return false;
    }

    // reprodukcja zwraca animal- dziecko
    public Animal reproduce(Animal partner){
        List<Integer> childGenes=this.genes.ChildGenes(this,partner);
        Genomes childGenome=new Genomes(childGenes,this.mingeneMutation,this.maxgeneMutation);
        this.animalChangeEnergy(parentingEnergy);
        partner.animalChangeEnergy(parentingEnergy);
        this.childrenCount+=1;      //dodanie dziecka do atrybutów rodziców
        partner.childrenCount+=1;
        return new Animal(this.position, 2*parentingEnergy,childGenome,this.ReproductionEnergy,this.mingeneMutation,this.maxgeneMutation,this,partner,this.parentingEnergy);
    }

    //dla bezpieczenj zmiany energi, bez wartości ujemnych
    public void animalChangeEnergy(int delta){
        this.energy+=delta;
        if(this.energy<0)   energy=0;
    }

    public String toString(){
        switch(orientation){
            case NORTH -> {
                return "N";
            }
            case SOUTH -> {
                return "S";
            }
            case EAST -> {
                return "E";
            }
            case WEST -> {
                return "W";
            }
        }
        return "";
    }
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }
    public void move(int gene, MoveValidator isMoveValid, int width) {
        Vector2d newPosition;

        for (int i = 0; i < gene; i++) {
            orientation = orientation.next();
        }

        newPosition = position.add(orientation.toUnitVector());
        if (isMoveValid.canMoveTo(newPosition)) {
            position = newPosition;
        } else if (newPosition.getX() == -1) {
            position = new Vector2d(width - 1, newPosition.getY());
        } else if (newPosition.getX() == width) {
            position = new Vector2d(0, newPosition.getY());
        }
    }
