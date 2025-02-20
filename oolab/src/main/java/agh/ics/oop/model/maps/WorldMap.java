package agh.ics.oop.model.maps;

import agh.ics.oop.model.*;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.util.MapChangeListener;

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

    int getLivingAnimalsCount();

    int getGrassCount();

    float getAvgLivingAnimalsEnergy();

    Boundary getCurrentBounds();

    UUID getId();

    void addObserver(MapChangeListener observer);

    void removeObserver(MapChangeListener observer);

    boolean isOccupied(Vector2d currentPosition);

    Object objectAt(Vector2d currentPosition);
}
