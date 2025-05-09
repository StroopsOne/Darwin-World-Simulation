package agh.ics.oop.model.maps;

import agh.ics.oop.model.*;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.OwlBear;
import agh.ics.oop.model.Enums.MapDirection;
import agh.ics.oop.model.mapElements.WorldElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

 public class TheEarthWithOwlBear extends AbstractWorldMap {
    int owlBearTerritorySide;
    private final Vector2d lowerTerritoryCoordinates;
    private final Vector2d upperTerritoryCoordinates;
    private final OwlBear owlBear;
    private int owlBearkillCounter;
     private final List<Animal> deadAnimals = new ArrayList<>();

    public TheEarthWithOwlBear(int height, int width, int mingeneMutation, int maxgeneMutation, int reproductionEnergy, int parentingEnergy, boolean slightCorrection) {
        super(height, width,mingeneMutation,maxgeneMutation,reproductionEnergy,parentingEnergy,slightCorrection);
        owlBearkillCounter = 0;
        owlBearTerritorySide = (int) Math.sqrt(0.2 * height * width);

        lowerTerritoryCoordinates = generateOwlBearTerritory(height, width);
        upperTerritoryCoordinates = lowerTerritoryCoordinates.add(new Vector2d(owlBearTerritorySide, owlBearTerritorySide));
        int lowerX = lowerTerritoryCoordinates.getX();
        int upperX = upperTerritoryCoordinates.getX();
        int lowerY = lowerTerritoryCoordinates.getY();
        int upperY = upperTerritoryCoordinates.getY();
        Vector2d position = new Vector2d(random.nextInt(upperX - lowerX) + lowerX, random.nextInt(upperY - lowerY) + lowerY);
        MapDirection orientation = MapDirection.randomDirection();
        this.owlBear = new OwlBear(position, orientation);
    }

    public Vector2d generateOwlBearTerritory(int height, int width){ //wybiera losowe pole, które jest lewym dolnym rogiem terytorium SowoNiedźwiedzia
        Random random = new Random();
        int lowerX = random.nextInt(width - owlBearTerritorySide -1);
        int lowerY = random.nextInt(height - owlBearTerritorySide -1);
        return new Vector2d(lowerX, lowerY);
    }

    @Override
    public boolean isOccupied(Vector2d position){
        if (owlBear.isAtPosition(position)){
            return true;
        }
        return super.isOccupied(position);

    }
    @Override
    public WorldElement objectAt(Vector2d position) {
        if (owlBear.isAtPosition(position)) {
            return owlBear;
        }
        return super.objectAt(position);
    }

     @Override
     public int getFreePositionsCount() {
         return super.getFreePositionsCount() - (grassPoints.containsKey(owlBear.getPosition()) ? 0 : 1);
     }

    @Override
    public void moveAllAnimals(int simulationDay) throws IncorrectPositionException {
        super.moveAllAnimals(simulationDay);
        moveOwlBear(owlBear);
        owlBearKillsAnimals(simulationDay);
    }

    //pomocniczo

    @Override
    public float getAvgDeadAnimalsLifespan() {
        if (deadAnimals.isEmpty()) {
            return 0f; // Brak zwierząt, średnia energia to 0
        }

        int sumLifespan = 0;
        for (Animal animal : deadAnimals) {
            sumLifespan += animal.getDeathDay();
        }

        return (float) sumLifespan / deadAnimals.size();
    }

    public void moveOwlBear(OwlBear owlBear) {
        Random random = new Random();
        int gene = random.nextInt(8);
        Vector2d oldPosition = owlBear.getPosition();
        owlBear.move(gene, this::canMoveOwlBearTo);
        Vector2d newPosition = owlBear.getPosition();
    }

    public boolean isOwlBearAtPosition(Vector2d position){
        return owlBear.isAtPosition(position);
    }


    public void owlBearKillsAnimals(int simulationDay){
        Vector2d position = owlBear.getPosition();
        List<Animal> animalsAtPosition = animals.get(position);
        if (animalsAtPosition != null) {
            for (Animal animal : animalsAtPosition) {
                animal.killAnimal(simulationDay);
                owlBearkillCounter++;
                //dodanie do listy martwych
                deadAnimals.add(animal);
            }
            animals.remove(position);
        }

    }

     public int getOwlBearKillsCounter() {
         return owlBearkillCounter;
     }

    public boolean canMoveOwlBearTo(Vector2d position) {
        return position.follows(lowerTerritoryCoordinates) && position.precedes(upperTerritoryCoordinates);
    }

}
