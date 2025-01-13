package agh.ics.oop.model.mapElements;

import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.maps.MoveValidator;
import agh.ics.oop.model.properities.Genomes;

import java.util.List;
import java.util.Random;

public class Animal implements WorldElement {


    private MapDirection orientation;
    private Vector2d position;
    public final static Vector2d defaultVector = new Vector2d(2, 2);
    private int energy;
    private final int ReproductionEnergy;       //ilość energii ustalona do rozmnażania
    private final Genomes genes;
    private final int geneUsedNumber; //sledzi ktory gen jest teraz uzywany, mozliwe ze logika i nazwa do zmiany
    private int ageDays;
    private Integer deathDay;
    private final int mingeneMutation;
    private final int maxgeneMutation;
    private Animal parent1;
    private Animal parent2;
    private int childrenCount;
    private final int parentingEnergy;  //energia zużywana przez posiadanie dzieci
    Random random = new Random();
    private final boolean slightCorrection;
    private int descendatns=0;

    public Animal(Vector2d vector, int startEnergy, int geneSize, int reproductionEnergy, int mingeneMutation, int maxgeneMutation, int parentingEnergy, boolean slightCorrection) {
        this.orientation=MapDirection.randomDirection();
        this.position = vector;
        this.energy = startEnergy;
        this.genes = new Genomes(geneSize);
        this.geneUsedNumber = random.nextInt(geneSize);
        this.ageDays = 0;
        this.deathDay = null;       //czyli zwierzak żyje
        this.ReproductionEnergy = reproductionEnergy;
        this.mingeneMutation = mingeneMutation;
        this.maxgeneMutation = maxgeneMutation;
        this.parent1 = null;
        this.parent2 = null;
        this.childrenCount = 0;
        this.parentingEnergy = parentingEnergy;
        this.slightCorrection=slightCorrection;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() { return position; }

    //konstruktor dla tworzenia dzieci
    public Animal(Vector2d position, MapDirection direction, int inheritedEnergy, Genomes genomes, int reproductionEnergy, int mingeneMutation, int maxgeneMutation, Animal parent1, Animal parent2, int parentingEnergy, boolean slightCorrection) {
        this.orientation = direction;
        this.position = position;
        this.energy = inheritedEnergy;
        this.geneUsedNumber = random.nextInt(this.getGenes().getGenomeSize()); //Nie wiem czemu to nie ma geneSize, ale nie chce psuc niczego, wiec na razie zostawie
        this.genes = genomes;
        this.ageDays = 0;
        this.deathDay = null;       //czyli zwierzak żyje
        this.ReproductionEnergy = reproductionEnergy;
        this.mingeneMutation = mingeneMutation;
        this.maxgeneMutation = maxgeneMutation;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.childrenCount = 0;
        this.parentingEnergy = parentingEnergy;
        this.slightCorrection= slightCorrection;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isAlive() {
        return deathDay == -1;
    }

    public void dayPasses() {
        energy = energy - 1;
        if (energy == 0) {
            //przypisanie dnia śmierci, trzeba mieć czas zaimplementowany
            //czas chyba do simulation
        } else ageDays += 1;
    }

    public int getAge() {
        return ageDays;
    }

    public int getDeathDay() {
        return deathDay;
    }

    public Genomes getGenes() {
        return genes;
    }

    public int GetNumberOfChildren() {
        return childrenCount;
    }

    public void killedByOwlBear() {
        //przypisanie deathDay to samo co w dayPasses
    }

    public void plantEatten(int plantValue) {
        energy += plantValue;
    }

    public boolean ReadyForKids() {
        if (energy > ReproductionEnergy) return true;
        else return false;
    }

    // reprodukcja zwraca animal- dziecko
    public Animal reproduce(Animal partner) {
        List<Integer> childGenes = this.genes.ChildGenes(this, partner);
        Genomes childGenome=new Genomes(childGenes,this.mingeneMutation,this.maxgeneMutation,this.SlightCorrection);
        this.ParentingSubstraction(parentingEnergy);
        partner.ParentingSubstraction(parentingEnergy);
        this.childrenCount += 1;      //dodanie dziecka do atrybutów rodziców
        partner.childrenCount += 1;
        //DODAJ WHILE Z POTOMKAMI
        Animal dziecko=new Animal(this.position, 2*parentingEnergy,childGenome,this.ReproductionEnergy,this.mingeneMutation,this.maxgeneMutation,this,partner,this.parentingEnergy,this.SlightCorrection);
        this.descendatns+=1;
        return dziecko;
    }

    //dla bezpieczenj zmiany energi, bez wartości ujemnych
    public void ParentingSubstraction(int parentingEnergy){
        this.energy-=parentingEnergy;
        if(this.energy<0)   energy=0;       //nie powinno wystąpić
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
