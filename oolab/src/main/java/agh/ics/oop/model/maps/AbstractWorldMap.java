package agh.ics.oop.model.maps;


import agh.ics.oop.model.*;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.Grass;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.properities.Genomes;
import agh.ics.oop.model.util.MapChangeListener;
import agh.ics.oop.model.util.MapVisualizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static agh.ics.oop.model.properities.Genomes.ChildGenes;

public abstract class AbstractWorldMap implements WorldMap, MoveValidator {
    protected final Map<Vector2d, Grass> grassPoints = new HashMap<>();
    protected final Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
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
    private final int minGeneMutation;
    private final int maxGeneMutation;
    private final int reproductionEnergy;
    private final int parentingEnergy;
    private final boolean slightCorrection;
    Random random = new Random();


    protected AbstractWorldMap(int height, int width, int minGeneMutation, int maxGeneMutation, int reproductionEnergy, int parentingEnergy, boolean slightCorrection){
        this.minGeneMutation = minGeneMutation;
        this.maxGeneMutation = maxGeneMutation;
        this.reproductionEnergy = reproductionEnergy;
        this.parentingEnergy = parentingEnergy;
        this.slightCorrection = slightCorrection;
        this.id = UUID.randomUUID();
        this.height = height;
        this.width = width;
        maxVector = new Vector2d(width-1, height-1);
        minVector = new Vector2d(0,0);
        int upperEquatorBound = (int) Math.round(0.4*(height-1));
        int lowerEquatorBound = (int) Math.round(0.6*(height-1));
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
    }

    public void placeStartObjects(int animalsCount, int grassCount, int grassValue, int startEnergy, int geneSize){
        try {
            for (int i = 0; i < animalsCount; i++) {
                Vector2d position = new Vector2d(random.nextInt(width), random.nextInt(height));
                placeAnimal(new Animal(position, startEnergy, geneSize));
            }
        }catch(IncorrectPositionException e){
            System.out.println("Nie udalo sie wrzucic poczatkowych zwierzat na mape, system zakonczy dzialanie");
            System.exit(0);
        }
        plantNewGrasses(grassCount, grassValue);
    }

    @Override
    public void placeAnimal(Animal animal) throws IncorrectPositionException {
        Vector2d position = animal.getPosition();
        if (canMoveTo(position)) {
            animals.computeIfAbsent(position, key -> new ArrayList<>()).add(animal);
        }
        else throw new IncorrectPositionException(position);
    }

    public void runSimulationDay(int simulationDay, int grassesCount, int grassValue) throws IncorrectPositionException {
        removeDeadAnimals();
        moveAllAnimals(simulationDay);
        animalsEatGrasses();
        animalsReproduce();
        plantNewGrasses(grassesCount, grassValue);
        decreaseEnergyForAllAnimals(simulationDay);

        notifyAllObservers("Zaktualizowano mapę po dniu symulacji");
    }

    public void removeDeadAnimals() {
        List<Vector2d> emptyPositions = new ArrayList<>();

        // Iterujemy przez każdą pozycję na mapie
        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> animalsAtPosition = entry.getValue();

            // Usuwa zwierze i dodaje do listy martwych
            Iterator<Animal> iterator = animalsAtPosition.iterator();
            while (iterator.hasNext()) {
                Animal animal = iterator.next();
                if (animal.getDeathDay() != null) { // Zwierzę jest martwe, jeśli energia <= 0
                    deadAnimals.add(animal);
                    iterator.remove();
                }
            }

            // Jeśli pozycja jest pusta, oznacza ją do usunięcia
            if (animalsAtPosition.isEmpty()) {
                emptyPositions.add(position);
            }
        }

        // Usuwamy puste pozycje z mapy
        for (Vector2d position : emptyPositions) {
            animals.remove(position);
        }
    }

    public void moveAllAnimals(int simulationDay) throws IncorrectPositionException {
        Map<Vector2d, List<Animal>> updatedAnimals = new HashMap<>();

        for (Vector2d position : new HashSet<>(animals.keySet())) { // Kopiujemy klucze, aby uniknąć modyfikacji w trakcie iteracji
            List<Animal> animalsAtPosition = animals.get(position);

            if (animalsAtPosition != null) {
                List<Animal> movingAnimals = new ArrayList<>(animalsAtPosition);
                for (Animal animal : movingAnimals) {
                    Vector2d oldPosition = animal.getPosition();
                    animal.move(animal.useGene(), this, width);

                    Vector2d newPosition = animal.getPosition();
                    if (!oldPosition.equals(newPosition)) {
                        updatedAnimals.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(animal);
                        animalsAtPosition.remove(animal);
                    }
                }

                // Usuń pozycję, jeśli jest pusta
                if (animalsAtPosition.isEmpty()) {
                    animals.remove(position);
                }
            }
        }

        // Dodaj przeniesione zwierzęta na nowe pozycje
        for (Map.Entry<Vector2d, List<Animal>> entry : updatedAnimals.entrySet()) {
            animals.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
        }
    }


    public void animalsEatGrasses(){
        for (List<Animal> animalsOnPosition : animals.values()){
            Vector2d position = animalsOnPosition.getFirst().getPosition();
            if (isGrassOnPosition(position)){
                List<Animal> sortedAnimals = animalsOnPosition.stream()
                        .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                                .thenComparingInt(Animal::getAgeDays).reversed()
                                .thenComparingInt(Animal::getChildrenCount).reversed())
                        .toList();

                // Pobieramy zwierzęta o takich samych atrybutach jak najlepszy
                Animal strongestAnimal = sortedAnimals.getFirst();
                List<Animal> tiedAnimals = sortedAnimals.stream()
                        .filter(animal -> animal.getEnergy() == strongestAnimal.getEnergy()
                                && animal.getAgeDays() == strongestAnimal.getAgeDays()
                                && animal.getChildrenCount() == strongestAnimal.getChildrenCount())
                        .toList();

                // Wybieramy losowe zwierzę spośród remisujących
                Animal selectedAnimal = tiedAnimals.get(new Random().nextInt(tiedAnimals.size()));
                selectedAnimal.eatPlant(grassPoints.get(position).getPlantValue());
                grassPoints.remove(position);
            }
        }
    }

    public void animalsReproduce(){
        try {
            for (List<Animal> animalsOnPosition : animals.values()) {
                Vector2d position = animalsOnPosition.getFirst().getPosition();
                List<Animal> sortedAnimals = animalsOnPosition.stream()
                        .sorted(Comparator.comparingInt(Animal::getEnergy).reversed()
                                .thenComparingInt(Animal::getAgeDays).reversed()
                                .thenComparingInt(Animal::getChildrenCount).reversed())
                        .toList();
                if (sortedAnimals.size() > 1) {
                    for (int i = 1; i < sortedAnimals.size(); i += 2) {
                        if (sortedAnimals.get(i).getEnergy() > reproductionEnergy) {
                            Animal child = groupAnimalsToReproduce(sortedAnimals.get(i - 1), sortedAnimals.get(i));
                            placeAnimal(child);
                        }
                    }
                }
            }
        }catch(IncorrectPositionException e){
            System.out.println("Blad przy wrzucaniu dziecka na mape");
        }
    }

    public Animal groupAnimalsToReproduce(Animal mom, Animal dad) {
        List<Integer> childGenes = ChildGenes(mom, dad);
        Genomes childGenome=new Genomes(childGenes, minGeneMutation, maxGeneMutation,slightCorrection);
        mom.changeEnergy(-parentingEnergy);
        dad.changeEnergy(-parentingEnergy);
        mom.addChild();
        dad.addChild();
        Animal child=new Animal(mom.getPosition(),parentingEnergy,childGenome,mom,dad);
        return child;
    }


    public void plantNewGrasses(int grassesCount, int grassValue) {
        for (int i = 0; i < grassesCount; i++) {
            Set<Vector2d> targetSet;
            int x = random.nextInt(5); // 20% szans na niepreferowane pola, 80% na preferowane

            // Wybór zbioru do losowania
            if (x == 0 && !notPreferredPositions.isEmpty()) {
                targetSet = notPreferredPositions;
            } else if (!preferredPositions.isEmpty()) {
                targetSet = preferredPositions;
            } else if (!notPreferredPositions.isEmpty()) {
                targetSet = notPreferredPositions;
            } else {
                continue; // Brak dostępnych pól
            }

            // Losowanie pozycji z wybranego zbioru
            int randomIndex = random.nextInt(targetSet.size());
            Vector2d position = targetSet.stream().skip(randomIndex).findFirst().orElse(null);

            // Sprawdzenie, czy pole jest zajęte przez trawę lub zwierzę
            if (position != null && !isOccupied(position)) {
                grassPoints.put(position, new Grass(position, grassValue)); // Dodanie trawy
            }
        }
    }

    public void decreaseEnergyForAllAnimals(int simulationDay) {
        for (List<Animal> animalsAtPosition : animals.values()) {
            for (Animal animal : animalsAtPosition) {
                animal.dayPasses(simulationDay);
            }
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(minVector) && position.precedes(maxVector);
    }

    public boolean isGrassOnPosition(Vector2d position){
        return (grassPoints.containsKey(position) && !grassPoints.get(position).isEaten());
    }

    public boolean isOccupied(Vector2d position){
        if (animals.containsKey(position)){
            return true;
        }
        if(grassPoints.containsKey(position)){
            return true;
        }
        return false;
    }

    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)){
            return animals.get(position).getFirst();
        }
        return grassPoints.get(position);
    }

    public void addObserver(MapChangeListener observer){
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer){
        observers.remove(observer);
    }

    protected void notifyAllObservers(String message){
        for(MapChangeListener observer : observers){
            observer.mapChanged(this);
        }
    }

    public int getGrassCount(){
        return grassPoints.size();
    }

    public List<Animal> getAllLivingAnimals(){
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animalsOnPosition : animals.values()){
            allAnimals.addAll(animalsOnPosition);
        }
        return allAnimals;
    }

    public float getAvgLivingAnimalsEnergy() {
        List<Animal> livingAnimals = getAllLivingAnimals();
        if (livingAnimals.isEmpty()) {
            return 0f; // Brak zwierząt, średnia energia to 0
        }

        int sumEnergy = 0;
        for (Animal animal : livingAnimals) {
            sumEnergy += animal.getEnergy();
        }

        return (float) sumEnergy / livingAnimals.size();
    }

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

    public float getAvgChildrenCount(){
        List<Animal> livingAnimals = getAllLivingAnimals();
        if (livingAnimals.isEmpty()) {
            return 0f; // Brak żyjących zwierząt, średnia liczba dzieci to 0
        }

        int childCount = 0;
        for (Animal animal : livingAnimals) {
            childCount += animal.getChildrenCount();
        }

        return (float) childCount / livingAnimals.size();
    }

    public int getFreePositionsCount() {
        Set<Vector2d> takenPositionsCount = new HashSet<>();
        takenPositionsCount.addAll(animals.keySet());
        takenPositionsCount.addAll(grassPoints.keySet());

        return width * height - takenPositionsCount.size();
    }

    public int getLivingAnimalsCount(){
        return getAllLivingAnimals().size();
    }

    public int getDeadAnimalsCount(){
        return deadAnimals.size();
    }

    public List<Animal> getDeadAnimals(){
        return deadAnimals;
    }

    public List<Animal> getAnimalsAtPos(Vector2d position){
        return animals.get(position);
    }

    public List<Integer> getMostCommonGenotypes() {
        List<Animal> animals = new ArrayList<>(this.getAllLivingAnimals());
        animals.addAll(this.getDeadAnimals());
        Map<Genomes, Long> genotypesCount = animals.stream()
                .collect(Collectors.groupingBy(Animal::getGenes, Collectors.counting()));

        long maxCount = Collections.max(genotypesCount.values());

        return genotypesCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .findFirst()
                .map(Genomes::getGenes)
                .orElse(Collections.emptyList());
    }

    public Boundary getCurrentBounds(){
        return new Boundary(minVector, maxVector);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String toString() {
        Boundary bounds = getCurrentBounds();
        return visualizer.draw(bounds.lowerLeft(), bounds.upperRight());
    }

}
