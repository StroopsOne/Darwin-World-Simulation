
import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {

    @Test
    void testConstructor() {
        Vector2d position = new Vector2d(5, 5);
        int energy = 50;
        Animal animal = new Animal(position, energy);

        assertEquals(position, animal.getPosition());
        assertEquals(MapDirection.NORTH, animal.getOrientation());
        assertEquals(energy, animal.getEnergy());
        assertTrue(animal.isAlive());
    }

    @Test
    void testMove() {
        Vector2d initialPosition = new Vector2d(2, 2);
        int energy = 50;
        Animal animal = new Animal(initialPosition, energy);

        MoveValidator alwaysValid = pos -> true;

        animal.move(MoveDirection.FORWARD, alwaysValid);
        assertEquals(new Vector2d(2, 3), animal.getPosition());
        assertEquals(MapDirection.NORTH, animal.getOrientation());

        animal.move(MoveDirection.RIGHT, alwaysValid);
        assertEquals(MapDirection.EAST, animal.getOrientation());

        animal.move(MoveDirection.BACKWARD, alwaysValid);
        assertEquals(new Vector2d(1, 3), animal.getPosition());
    }

    @Test
    void testMoveValidation() {
        Vector2d initialPosition = new Vector2d(2, 2);
        int energy = 50;
        Animal animal = new Animal(initialPosition, energy);

        MoveValidator blockAllMoves = pos -> false;

        animal.move(MoveDirection.FORWARD, blockAllMoves);
        assertEquals(initialPosition, animal.getPosition());
    }

    @Test
    void testEnergyManagement() {
        Vector2d position = new Vector2d(5, 5);
        int initialEnergy = 10;
        Animal animal = new Animal(position, initialEnergy);

        // Test utraty energii
        animal.dayPasses();
        assertEquals(9, animal.getEnergy());

        // Test zjedzenia rośliny
        animal.plantEatten(20);
        assertEquals(29, animal.getEnergy());

        // Test śmierci
        for (int i = 0; i < 29; i++) {
            animal.dayPasses();
        }
        assertFalse(animal.isAlive());
    }

    @Test
    void testReadyForKids() {
        Vector2d position = new Vector2d(5, 5);
        Animal animal = new Animal(position, 101);

        assertTrue(animal.ReadyForKids());

        animal.dayPasses();
        assertFalse(animal.ReadyForKids());
    }

    @Test
    void testOrientation() {
        Vector2d position = new Vector2d(2, 2);
        Animal animal = new Animal(position, 50);

        animal.move(MoveDirection.RIGHT, pos -> true);
        assertEquals(MapDirection.EAST, animal.getOrientation());

        animal.move(MoveDirection.LEFT, pos -> true);
        assertEquals(MapDirection.NORTH, animal.getOrientation());
    }

    @Test
    void testKilledBySowoniedzwiedz() {
        Vector2d position = new Vector2d(5, 5);
        Animal animal = new Animal(position, 50);

        animal.killedBySowoniedzwiedz();
        assertFalse(animal.isAlive());
    }

    @Test
    void testToString() {
        Animal animal = new Animal(new Vector2d(0, 0), 50);
        assertEquals("N", animal.toString());

        animal.move(MoveDirection.RIGHT, pos -> true);
        assertEquals("E", animal.toString());
    }

}