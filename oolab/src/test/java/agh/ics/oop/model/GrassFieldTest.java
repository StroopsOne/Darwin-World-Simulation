/*
package agh.ics.oop.model;

import agh.ics.oop.OptionsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrassFieldTest {
    private final GrassField grassField = new GrassField(10);
    private final Vector2d position = new Vector2d(2, 2);
    private final Animal animal = new Animal(position);


    @Test
    public void toStringTest() throws IncorrectPositionException {
        grassField.place(animal);
        System.out.println(grassField.getElements());
        Boundary currentBounds = grassField.getCurrentBounds();
        System.out.println(currentBounds.lowerLeft());
        System.out.println(currentBounds.upperRight());
        System.out.println(grassField.toString());
    }

    @Test
    public void placeTest1() {
        try {
            assertTrue(grassField.place(animal));
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertTrue(grassField.isOccupied(position));
    }

    @Test
    public void placeTest2() throws IncorrectPositionException {
        Animal anotherAnimal = new Animal(new Vector2d(2, 2));
        grassField.place(animal);
        IncorrectPositionException thrown = assertThrows(
                IncorrectPositionException.class,
                () -> grassField.place(anotherAnimal)
        );
        assertEquals("Position (2,2) is not correct.", thrown.getMessage());

    }

    @Test
    public void isOccupiedTest() {
        assertFalse(grassField.isOccupied(new Vector2d(5, 5)));
        try {
            grassField.place(animal);;
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertTrue(grassField.isOccupied(position));
    }

    @Test
    public void canMoveToTest1() {
        assertTrue(grassField.canMoveTo(new Vector2d(5, 5)));
        try {
            grassField.place(animal);;
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertFalse(grassField.canMoveTo(position));
    }


    @Test
    public void canMoveToTest2() {
        Vector2d outOfBounds = new Vector2d(-1, -1);
        assertFalse(grassField.canMoveTo(outOfBounds));
    }


    // Test dla metody move
    @Test
    public void moveTest1() {
        try {
            grassField.place(animal);;
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        Vector2d newPosition = new Vector2d(3, 2);
        grassField.move(animal, MoveDirection.RIGHT);
        grassField.move(animal, MoveDirection.FORWARD);
        assertEquals(newPosition, animal.getPosition());
    }

    @Test
    public void moveTest2() {
        try {
            grassField.place(animal);;
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        Vector2d newPosition = new Vector2d(0, 2);
        grassField.move(animal, MoveDirection.LEFT);
        grassField.move(animal, MoveDirection.FORWARD);
        grassField.move(animal, MoveDirection.FORWARD);
        grassField.move(animal, MoveDirection.FORWARD);
        assertEquals(newPosition, animal.getPosition());
    }


    @Test
    public void testObjectAt() {
        try {
            grassField.place(animal);;
        }catch(IncorrectPositionException e){
            System.out.println(e.getMessage());
        }
        assertEquals(animal, grassField.objectAt(position));
    }


}*/
