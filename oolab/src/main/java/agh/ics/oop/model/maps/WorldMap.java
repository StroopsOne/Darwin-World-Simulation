package agh.ics.oop.model.maps;

import agh.ics.oop.model.*;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.util.MapChangeListener;

import java.util.List;
import java.util.UUID;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo, idzik
 */
public interface WorldMap extends MoveValidator {

    void placeAnimal(Animal animal) throws IncorrectPositionException;

    void moveAllAnimals(int simulationDay) throws IncorrectPositionException;

    void animalsEatGrasses();

    void animalsReproduce();

    Animal groupAnimalsToReproduce(Animal mom, Animal dad);

    void plantNewGrasses(int grassesCount, int grassValue);

    int getGrassCount();

    List<Animal> getAllLivingAnimals();

    float getAvgLivingAnimalsEnergy();

    float getAvgDeadAnimalsLifespan();

    float getAvgChildrenCount();

    int getFreePositionsCount();

    int getLivingAnimalsCount();

    int getDeadAnimalsCount();

    List<Animal> getDeadAnimals();

    List<Animal> getAnimalsAtPos(Vector2d position);

    List<Integer> getMostCommonGenotypes();

    Boundary getCurrentBounds();

    UUID getId();

    void addObserver(MapChangeListener observer);

    void removeObserver(MapChangeListener observer);

    boolean isOccupied(Vector2d currentPosition);

    Object objectAt(Vector2d currentPosition);
}
