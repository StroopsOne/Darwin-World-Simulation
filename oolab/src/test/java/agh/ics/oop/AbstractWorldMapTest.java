package agh.ics.oop;

import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.Grass;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWorldMapTest {
    private AbstractWorldMap map;

    @BeforeEach
    void setUp() {
        map = new TheEarth(10, 10, 1, 3, 50, 20, true);
    }

    //testy na dziwne przypadki
    @Test
    void testMapInitialization() {
        assertEquals(0, map.getLivingAnimalsCount());
        assertEquals(0, map.getGrassCount());
    }

    @Test
    void testPlaceAnimal() {
        Animal animal = new Animal(new Vector2d(2, 2), 100, 8);
        try {
            map.placeAnimal(animal);
        }catch(IncorrectPositionException e){
            fail("Animal placement failed.");
        }
        assertEquals(1, map.getLivingAnimalsCount());
        assertTrue(map.isOccupied(new Vector2d(2, 2)));
    }

    @Test
    void testAnimalReproduction() {
        Animal mom = new Animal(new Vector2d(2, 2), 100, 8);
        Animal dad = new Animal(new Vector2d(2, 2), 100, 8);
        try {
            map.placeAnimal(mom);
            map.placeAnimal(dad);
        }catch(IncorrectPositionException e){
            fail("Animal placement failed.");
        }
        map.animalsReproduce();
        assertEquals(3, map.getLivingAnimalsCount());
        //przy okazji sprawdzamy inne rzeczy
        assertEquals(1,mom.getChildrenCount());
        assertEquals(1,dad.getChildrenCount());
    }

    @Test
    void testAnimalEatsGrass() {
        // Trawa na pewno będzie na (0, 0) dzięki małej mapie
        map = new TheEarth(1, 1, 1, 3, 50, 20, true);
        Animal animal = new Animal(new Vector2d(0, 0), 100, 8);

        map.plantNewGrasses(1, 50);

        assertEquals(1,map.getGrassCount());

        try {
            map.placeAnimal(animal);
        } catch (IncorrectPositionException e) {
            fail("Animal placement failed.");
        }

        map.animalsEatGrasses();

        assertEquals(150, animal.getEnergy(), "Animal's energy should increase after eating grass.");
        assertEquals(0, map.getGrassCount(), "Grass count should decrease after being eaten.");
    }


    @Test
    void testMoveAnimal() throws Exception {
        map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Animal animal = new Animal(new Vector2d(2, 2), 100, 8);
        map.placeAnimal(animal);

        map.moveAnimal(animal);

        assertNotEquals(new Vector2d(2, 2), animal.getPosition());
        System.out.println("Animal new position: " + animal.getPosition());
    }

    @Test
    void testMapStatistics() {
        map.placeStartObjects(5, 5, 20, 100, 8);

        assertEquals(5, map.getLivingAnimalsCount());
        assertEquals(5, map.getGrassCount());
        assertEquals(0, map.getDeadAnimalsCount());
    }

    @Test
    void testRemoveDeadAnimals() {
        Animal animal = new Animal(new Vector2d(2, 2), 0, 8); // Martwe zwierzę
        Animal animal2 = new Animal(new Vector2d(3, 2), 0, 8); // Martwe zwierzę
        Animal animal3 = new Animal(new Vector2d(4, 4), 5, 8); // Żywe zwierzę

        try {
            map.placeAnimal(animal);
            map.placeAnimal(animal2);
            map.placeAnimal(animal3);
        } catch (IncorrectPositionException e) {
            fail("Failed to place animals on the map.");
        }

        map.removeDeadAnimals(1);

        // Martwe zwierzęta są usuwane
        assertEquals(2, map.getDeadAnimalsCount(), "Dead animals count should increase after removing dead animals.");

        // Żywe zwierzęta pozostają na mapie
        assertEquals(1, map.getLivingAnimalsCount(), "Living animals count should remain unchanged for animals with energy > 0.");

        // Pozycje martwych zwierząt są usuwane z mapy
        assertFalse(map.isOccupied(new Vector2d(2, 2)), "Position (2,2) should not be occupied after removing dead animals.");
        assertFalse(map.isOccupied(new Vector2d(3, 2)), "Position (3,2) should not be occupied after removing dead animals.");
        assertTrue(map.isOccupied(new Vector2d(4, 4)), "Position (4,4) should still be occupied by a living animal.");
    }

}
