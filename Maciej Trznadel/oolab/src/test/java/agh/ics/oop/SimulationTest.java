/*package agh.ics.oop;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void simulationRunTest() {
        String[] moves = {"f", "c", "r", "f", "g", "l", "b", "r", "f", "f", "l", "f"};
        List<MoveDirection> directions = OptionsParser.parse(moves);
        List<Vector2d> positions = List.of(new Vector2d(2, 2));
        GrassField animalsMap = new GrassField(10);
        Simulation simulation1 = new Simulation(positions, directions, animalsMap);
        List<Animal> animalListTest = simulation1.getAnimalListCopy();

        assertEquals(new Vector2d(2, 2), animalListTest.get(0).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(0).getOrientation());
        simulation1.run();
        assertEquals(new Vector2d(5, 3), animalListTest.get(0).getPosition());
    }

    @Test
    void simulationRunTest2() {
        String[] moves = {"r", "f", "l", "r", "f", "b", "f", "r", "f", "f", "f"};
        List<MoveDirection> directions = OptionsParser.parse(moves);
        List<Vector2d> positions = List.of(new Vector2d(2, 2), new Vector2d(3, 4), new Vector2d(1, 3));
        GrassField animalsMap = new GrassField(10);
        Simulation simulation2 = new Simulation(positions, directions, animalsMap);
        List<Animal> animalListTest = simulation2.getAnimalListCopy();
        assertEquals(new Vector2d(2, 2), animalListTest.get(0).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(0).getOrientation());
        assertEquals(new Vector2d(3, 4), animalListTest.get(1).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(1).getOrientation());
        assertEquals(new Vector2d(1, 3), animalListTest.get(2).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(2).getOrientation());
        System.out.println(animalsMap);
        simulation2.run();
        assertEquals(new Vector2d(2, 0), animalListTest.get(0).getPosition());
        assertEquals(MapDirection.SOUTH, animalListTest.get(0).getOrientation());
        assertEquals(new Vector2d(4, 6), animalListTest.get(1).getPosition());
        assertEquals(MapDirection.EAST, animalListTest.get(1).getOrientation());
        assertEquals(new Vector2d(1, 3), animalListTest.get(2).getPosition());
        assertEquals(MapDirection.WEST, animalListTest.get(2).getOrientation());
    }
    @Test
    void simulationRunTest3() {
        String[] moves = {"r", "b", "f", "b", "r", "b", "f", "b", "f", "b", "f", "b", "r", "l","f","f", "r", "l", "f", "r","b","f","f"};
        List<MoveDirection> directions = OptionsParser.parse(moves);
        List<Vector2d> positions = List.of(new Vector2d(2, 2), new Vector2d(3, 4), new Vector2d(3, 4), new Vector2d(2,2));
        GrassField animalsMap = new GrassField(10);
        Simulation simulation3 = new Simulation(positions, directions, animalsMap);
        List<Animal> animalListTest = simulation3.getAnimalListCopy();

        assertEquals(new Vector2d(2, 2), animalListTest.get(0).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(0).getOrientation());
        assertEquals(new Vector2d(3, 4), animalListTest.get(1).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(1).getOrientation());
        System.out.println(animalsMap);
        simulation3.run();
        assertEquals(new Vector2d(2, 1), animalListTest.get(0).getPosition());
        assertEquals(MapDirection.NORTH, animalListTest.get(0).getOrientation());
        assertEquals(new Vector2d(1, 1), animalListTest.get(1).getPosition());
        assertEquals(MapDirection.WEST, animalListTest.get(1).getOrientation());
    }
}*/