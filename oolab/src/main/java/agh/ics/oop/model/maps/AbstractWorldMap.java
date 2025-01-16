package agh.ics.oop.model.maps;


import agh.ics.oop.model.*;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.Grass;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.util.MapChangeListener;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractWorldMap implements WorldMap, MoveValidator {
    private final Map<Vector2d, Grass> grassPoints = new HashMap<>();
    private final Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    private final List<Animal> deadAnimals = new ArrayList<>();
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
            placeAnimal(new Animal(position, /* trzeba dodac reszte zalezy gdzie metoda bedzie */);
        }
        plantNewGrasses(grassesCount, grassValue);
    }

    public void plantGrass(Vector2d position, Grass grass){ grassPoints.put(position, grass); }  //trawa wyrasta na pozycji

    public void animalsEatGrasses(){
        for (List<Animal> animalsOnPosition : animals.values()){
            Vector2d position = animalsOnPosition.getFirst().getPosition();
            if (isGrassOnPosition(position)){
                List<Animal> sortedAnimals = animalsOnPosition.stream()
                        .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                                .thenComparingInt(Animal::getAgeDays).reversed()
                                .thenComparingInt(Animal::getChildrenCount).reversed())
                        .toList();

                // Pobierz zwierzęta o takich samych atrybutach jak najlepszy
                Animal strongestAnimal = sortedAnimals.getFirst();
                List<Animal> tiedAnimals = sortedAnimals.stream()
                        .filter(animal -> animal.getEnergy() == strongestAnimal.getEnergy()
                                && animal.getAgeDays() == strongestAnimal.getAgeDays()
                                && animal.getChildrenCount() == strongestAnimal.getChildrenCount())
                        .toList();

                // Wybierz losowe zwierzę spośród remisujących
                Animal selectedAnimal = tiedAnimals.get(new Random().nextInt(tiedAnimals.size()));
                selectedAnimal.eatPlant(grassPoints.get(position).getPlantValue());
                grassPoints.remove(position);
            }
        }
    } //Zwierzę zjada trawe

    public void animalsReproduce(){
        for (List<Animal> animalsOnPosition : animals.values()){
            Vector2d position = animalsOnPosition.getFirst().getPosition();
            List<Animal> sortedAnimals = animalsOnPosition.stream()
                    .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                            .thenComparingInt(Animal::getAgeDays).reversed()
                            .thenComparingInt(Animal::getChildrenCount).reversed())
                    .toList();
            if (sortedAnimals.size()>1){
                for (int i=1;i<sortedAnimals.size();i+=2){
                    animalsOnPosition.get(i).reproduce(animalsOnPosition.get(i-1));
                }
            }
        }
    }

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

                // Dodanie trawy i usunięcie pozycji z dostępnych pól
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
            animals.computeIfAbsent(position, key -> new ArrayList<>()).add(animal);
            notifyAllObservers("animal placed on " + position);
            return true;
        }
        else throw new IncorrectPositionException(position);
    }


    public void removeDeadAnimals(int simulationDay){
        for (List<Animal> animalsAtPosition : animals.values()){

            animalsAtPosition.removeIf(animal -> {
                animal.dayPasses(simulationDay);
                if (animal.getDeathDay() != null) {
                    deadAnimals.add(animal);  // Dodaj martwe zwierzę do deadAnimals
                    return true;  // Usuwamy martwe zwierzę
                }
                return false;  // Nie usuwamy żywego zwierzęcia
            });
        }
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

    public boolean moveAnimal(Animal animal) throws IncorrectPositionException {
        Vector2d oldPosition = animal.getPosition();
        animal.move(animal.useGene(), this, width);
        Vector2d newPosition = animal.getPosition();
        if (newPosition.equals(oldPosition)) {
            animals.computeIfAbsent(newPosition, key -> new ArrayList<>()).add(animal);
            notifyAllObservers("animal move to " + newPosition);
            return true;
        }
        else throw new IncorrectPositionException(newPosition);
    }


    public void moveAllAnimals() throws IncorrectPositionException {
        for (List<Animal> animalsOnPosition : animals.values()){
            List<Animal> animalsToRemove = new ArrayList<>(); // Lista zwierząt do usunięcia
            for (Animal animal : animalsOnPosition) {
                if (moveAnimal(animal)) {
                    animalsToRemove.add(animal);
                }
            }

            // Usuwanie zwierząt, które wykonały ruch
            animalsOnPosition.removeAll(animalsToRemove);

            // Usuwanie pustych list zwierząt
            if (animalsOnPosition.isEmpty()) {
                animals.values().remove(animalsOnPosition);
            }
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
