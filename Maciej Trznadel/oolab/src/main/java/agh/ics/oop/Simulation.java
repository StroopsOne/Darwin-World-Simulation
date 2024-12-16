package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.IncorrectPositionException;
import agh.ics.oop.model.WorldMap;
import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable {
    private final List<Animal> animalList;
    private final List<MoveDirection> moveList;
    private final WorldMap mapAnimals;


    public Simulation(List<Vector2d> startPositions, List<MoveDirection> moveList, WorldMap mapAnimals) throws IncorrectPositionException {
        animalList = new ArrayList<>();
        int counter = 0;
        for (Vector2d pos : startPositions) {
            Animal animal = new Animal(pos);
            try{

                mapAnimals.place(animal);
                animalList.add(animal);

            }catch (IncorrectPositionException e){
                System.out.println(e.getMessage());
            }
        }
        this.moveList = moveList;
        this.mapAnimals = mapAnimals;
    }

    public List<Animal> getAnimalListCopy() {
        return new ArrayList<>(animalList);
    }
    public void run() {
        int moveNum = 0;
        int len = moveList.size();
        while (moveNum < len) {
            int petIdx = 1;
            for (Animal pet : animalList) {
                mapAnimals.move(pet, moveList.get(moveNum));

                moveNum++;
                if (moveNum == len) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Simulation interrupted.");
                    return;
                }
            }
        }
    }
}
//Uważam, że użycie tu ArrayList jest odpowiednie, ponieważ pozwala dodawać elementy na końcu w czasie O(1),
//z czego korzystamy w zadaniu. Również dostęp do dowolnych elementów listy jest w czasie O(1) z którego to dostępu
//korszytsany w metodzie run, dlatego uważam, że użyta przeze mnie Lista jest najlepsza z dostępnych.
