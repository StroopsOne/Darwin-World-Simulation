package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangularMapTest {
    private final RectangularMap rectangularMap = new RectangularMap(5, 5);
    private final Vector2d position = new Vector2d(2, 2);
    private final Animal animal = new Animal(position);

    @Test
    public void toStringTest() throws IncorrectPositionException {
        rectangularMap.place(animal);
        System.out.println(rectangularMap.toString());
    }
    @Test
    public void placeTest1() {
        try {
            assertTrue(rectangularMap.place(animal));
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }

        assertTrue(rectangularMap.isOccupied(position));
    }

    @Test
    public void placeTest2() throws IncorrectPositionException {
        Animal anotherAnimal = new Animal(new Vector2d(2, 2));
        rectangularMap.place(animal);
        IncorrectPositionException thrown = assertThrows(
                IncorrectPositionException.class,
                () -> rectangularMap.place(anotherAnimal)
        );
        assertEquals("Position (2,2) is not correct.", thrown.getMessage());

    }

    @Test
    public void canMoveToTest1() {
        assertTrue(rectangularMap.canMoveTo(new Vector2d(3, 3)));
        try {
            rectangularMap.place(animal);
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertFalse(rectangularMap.canMoveTo(position));
    }

    @Test
    public void canMoveToTest2() {
        Vector2d outOfBounds = new Vector2d(-1, -1);
        assertFalse(rectangularMap.canMoveTo(outOfBounds));
    }

    @Test
    public void moveTest1() {
        try {
            rectangularMap.place(animal);
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        Vector2d newPosition = new Vector2d(3, 2);
        rectangularMap.move(animal, MoveDirection.RIGHT);
        rectangularMap.move(animal, MoveDirection.FORWARD);
        assertEquals(newPosition, animal.getPosition());
    }

    @Test
    public void moveTest2() {
        try {
            rectangularMap.place(animal);
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        Vector2d edgePosition = new Vector2d(0, 2);
        rectangularMap.move(animal, MoveDirection.LEFT);
        rectangularMap.move(animal, MoveDirection.FORWARD);
        rectangularMap.move(animal, MoveDirection.FORWARD);
        rectangularMap.move(animal, MoveDirection.FORWARD);

        assertEquals(edgePosition, animal.getPosition());
    }

    @Test
    public void isOccupiedTest() {
        assertFalse(rectangularMap.isOccupied(new Vector2d(1, 1)));
        try {
            rectangularMap.place(animal);
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertTrue(rectangularMap.isOccupied(position));
    }

    @Test
    public void objectAtTest() {
        try {
            rectangularMap.place(animal);
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertEquals(animal, rectangularMap.objectAt(position));
        assertNull(rectangularMap.objectAt(new Vector2d(4, 4)));
    }


}