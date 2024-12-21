package agh.ics.oop.model;


import agh.ics.oop.model.util.MapChangeListener;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap, MoveValidator {
    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    private final MapVisualizer visualizer = new MapVisualizer(this);
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final UUID id;

    protected AbstractWorldMap() {
        this.id = UUID.randomUUID();
    }

    @Override
    public Collection<WorldElement> getElements() {
        return new ArrayList<>(animals.values());
    }

    @Override
    public boolean place(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        if (canMoveTo(position)) {
            animals.put(position, animal);
            notifyAllObservers("animal placed on " + position);
            return true;
        }
        else throw new IncorrectPositionException(position);
    }

    @Override
    public void move(Animal animal, int rotation) {
        Vector2d oldPosition = animal.getPosition();
        animal.move(rotation/*, this*/);
        Vector2d newPosition = animal.getPosition();

        if (!oldPosition.equals(newPosition)) {
            animals.remove(oldPosition);
            animals.put(newPosition, animal);
            notifyAllObservers("animal moved to " + newPosition);
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        return animals.get(position);
    }
    public abstract Boundary getCurrentBounds();

    public String toString() {
        Boundary bounds = getCurrentBounds();
        return visualizer.draw(bounds.lowerLeft(), bounds.upperRight());
    }
    public void addObserver(MapChangeListener observer){
        observers.add(observer);
    }
    public void removeObserver(MapChangeListener observer){
        observers.remove(observer);
    }
    private void notifyAllObservers(String message){
        for(MapChangeListener observer : observers){
            observer.mapChanged(this, message);
        }
    }

    @Override
    public UUID getId() {
        return id;
    }
}
