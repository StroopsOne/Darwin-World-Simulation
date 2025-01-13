package agh.ics.oop.model.maps;


import agh.ics.oop.model.*;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.Grass;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.util.MapChangeListener;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap, MoveValidator {
    private final Map<Vector2d, Grass> grassPoints = new HashMap<>();
    private final Map<Vector2d, Animal> animals = new HashMap<>();
    private final MapVisualizer visualizer = new MapVisualizer(this);
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final UUID id;
    private final int height;
    private final int width;
    private final Vector2d maxVector;
    private final Vector2d minVector;
    private final Set<Vector2d> preferredPositions = new HashSet<>();
    private final Set<Vector2d> notPreferredPositions = new HashSet<>();
    private final int preferredPositionsCount;
    private final int notPreferredPositionsCount;
    Random random = new Random();

    protected AbstractWorldMap(int height, int width){
        this.id = UUID.randomUUID();
        this.height = height;
        this.width = width;
        maxVector = new Vector2d(width-1, height-1);
        minVector = new Vector2d(0,0);
        int upperEquatorBound = (int) (0.4*(height-1));
        int lowerEquatorBound = (int) (0.6*(height-1));
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d position = new Vector2d(i, j);
                if (j >= upperEquatorBound && j < lowerEquatorBound) {
                    preferredPositions.add(position);
                } else {
                    notPreferredPositions.add(position);
                }
            }
        }
        this.preferredPositionsCount = preferredPositions.size();
        this.notPreferredPositionsCount = notPreferredPositions.size();

    }

    public void placeStartObjects(int animalsCount, int grassesCount, int grassValue){
        for (int i = 0; i < animalsCount; i++){
            Vector2d position = new Vector2d(random.nextInt(width), random.nextInt(height));
            animals.put(position, new Animal(/*tu trzeba przekazac*/));
        }
        plantNewGrasses(grassesCount, grassValue);
    }


    public void plantGrass(Vector2d position, Grass grass){ grassPoints.put(position, grass); }  //trawa wyrasta na pozycji
    public void removeEeatenGrass(Grass grass){grassPoints.remove(grass.getPosition());} //usuwa zjedzona trawe
    public void plantNewGrasses(int grassesCount, int grassValue) {


        for (int i = 0; i < grassesCount; i++) {
            Set<Vector2d> targetSet;
            int x = random.nextInt(5); // 20% szans na niepreferowane pola, 80% na preferowane

            if (x == 0) {
                targetSet = notPreferredPositions;
            } else {
                targetSet = preferredPositions;
            }
            // Losowanie pola z wybranego zbioru
            if (!targetSet.isEmpty()) {
                int randomIndex = random.nextInt(targetSet.size());
                Vector2d position = targetSet.stream().skip(randomIndex).findFirst().orElse(null);

                // Dodaj trawę i usuń zajętą pozycję z dostępnych pól
                if (position != null) {
                    grassPoints.put(position, new Grass(position, grassValue));
                    targetSet.remove(position);
                }
            }
        }
    }

    public boolean isGrassOnPosition(Vector2d position){
        return (grassPoints.containsKey(position) && !grassPoints.get(position).isEaten());
    }

    @Override
    public boolean placeAnimal(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        if (canMoveTo(position)) {
            animals.put(position, animal);
            notifyAllObservers("animal placed on " + position);
            return true;
        }
        else throw new IncorrectPositionException(position);
    }

    public void removeDeadAnimal(Animal animal){
        animals.remove(animal.getPosition(), animal);
        notifyAllObservers("animal removed from position: " + animal.getPosition());
    }

    @Override
    public Collection<WorldElement> getElements() {
        Collection<WorldElement> elements = new ArrayList<>();
        elements.addAll(animals.values());
        elements.addAll(grassPoints.values());
        return elements;
    }

    @Override
    public boolean isAnimalOnPosition(Vector2d position) {
        return animals.containsKey(position) && animals.get(position).isAlive();
    }

    @Override
    public void moveAnimal(Animal animal, int gene) {
        Vector2d oldPosition = animal.getPosition();
        animal.move(gene, this, width);
        Vector2d newPosition = animal.getPosition();

        if (!oldPosition.equals(newPosition)) {
            animals.remove(oldPosition);
            animals.put(newPosition, animal);
            notifyAllObservers("animal moved to " + newPosition);
        }
    }

    public void moveAllAnimals(){
        for (Animal animal : animals.values()){
            moveAnimal(animal, animal.getGenes());
            //potrzenba jakas implementacja aby było wiedziec ktory gen uzywa obecnie
        }
    }

    @Override

    public boolean canMoveTo(Vector2d position) {
        return position.follows(minVector) && position.precedes(maxVector);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) {
            return animals.get(position);
        }
        return grassPoints.getOrDefault(position, null);
    }

    public Boundary getCurrentBounds(){
        return new Boundary(minVector, maxVector);
    }

    public void addObserver(MapChangeListener observer){
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer){
        observers.remove(observer);
    }

    protected void notifyAllObservers(String message){
        for(MapChangeListener observer : observers){
            observer.mapChanged(this, message);
        }
    }

    public String toString() {
        Boundary bounds = getCurrentBounds();
        return visualizer.draw(bounds.lowerLeft(), bounds.upperRight());
    }

    @Override
    public UUID getId() {
        return id;
    }
}
