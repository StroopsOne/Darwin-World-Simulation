package agh.ics.oop.model.mapElements;

import agh.ics.oop.model.Enums.MapDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.maps.MoveValidator;
import agh.ics.oop.model.properities.Genomes;

import java.util.Random;

public class Animal implements WorldElement {

    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private int plantEatenCount;
    private final Genomes genes;
    private int genePartUsed; //sledzi ktory gen jest teraz uzywany, mozliwe ze logika i nazwa do zmiany
    private int ageDays;
    private Integer deathDay;
    private Animal parent1;
    private Animal parent2;
    private int childrenCount;
    Random random = new Random();

    public Animal(Vector2d vector, int startEnergy, int geneSize) {
        this.orientation = MapDirection.randomDirection();
        this.position = vector;
        this.energy = startEnergy;
        this.plantEatenCount = 0;
        this.genes = new Genomes(geneSize);
        this.genePartUsed = random.nextInt(geneSize);
        this.ageDays = 0;
        this.deathDay = null;       //czyli zwierzak żyje
        this.parent1 = null;
        this.parent2 = null;
        this.childrenCount = 0;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() { return position; }

    public void changeEnergy(int value){
        this.energy=energy+value;
        if (energy<0)   energy=0;
    }

    public void dayPasses(int simulationDay) {
        energy = energy - 1;
        if (energy == 0) {
            deathDay = simulationDay; //przypisanie dnia śmierci
        } else ageDays += 1;
    }

    public void killAnimal(int day){
        deathDay=day;
    }

    public void incermentAge(){
        this.ageDays+=1;
    }

    public void addChild(){
        this.childrenCount+=1;
    }

    //konstruktor dla tworzenia dzieci
    public Animal(Vector2d position, int inheritedEnergy, Genomes genomes, Animal parent1, Animal parent2) {
        this.orientation = MapDirection.randomDirection();;
        this.position = position;
        this.energy = inheritedEnergy;
        this.plantEatenCount = 0;
        this.genes = genomes;
        this.genePartUsed = random.nextInt(this.getGenes().getGenomeSize()); //Nie wiem czemu to nie ma geneSize, ale nie chce psuc niczego, wiec na razie zostawie
        this.ageDays = 0;
        this.deathDay = null;       //czyli zwierzak żyje
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.childrenCount = 0;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAgeDays(){
        return ageDays;
    }

    public int getChildrenCount(){
        return childrenCount;
    }

    public boolean isAlive() {
        return deathDay == null;
    }


    public Integer getDeathDay() {
        return deathDay;
    }

    public Genomes getGenes() {
        return genes;
    }

    public int getGenePartUsed(){
        return genePartUsed;
    }

    public int useGene(){ //zuzywa aktualnie aktywowany gen i aktywuje kolejny, który bedzie uzyty kolenego dnia
        int temp = genePartUsed;
        if (genePartUsed < genes.getGenomeSize() - 1){
            genePartUsed++;
        } else{
            genePartUsed = 0;
        }
        return genes.getGene(temp);
    }

    public void eatPlant(int plantValue) {
        energy += plantValue;
        plantEatenCount++;
    }

    public String toString() {
        switch (orientation) {
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
            case NORTHEAST -> {
                return "NE";
            }
            case NORTHWEST -> {
                return "NW";
            }
            case SOUTHEAST -> {
                return "SE";
            }
            case SOUTHWEST -> {
                return "SW";
            }
        }
        return "";
    }

    public int getPlantEatenCount() {
        return plantEatenCount;
    }

    public boolean isAt(Vector2d position) {
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
}
