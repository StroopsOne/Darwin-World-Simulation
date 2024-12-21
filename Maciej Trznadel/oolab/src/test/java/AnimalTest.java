
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*public class AnimalTest {

    @Test
    public void moveRightAndForwardTest(){
        Animal animal1 = new Animal();

        assertEquals(MapDirection.NORTH , animal1.getOrientation());
        assertEquals(new Vector2d(2,2), animal1.getPosition());
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(2,3), animal1.getPosition());
        animal1.move(MoveDirection.RIGHT);
        assertEquals(MapDirection.EAST , animal1.getOrientation());
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(3,3), animal1.getPosition());
        animal1.move(MoveDirection.RIGHT);
        assertEquals(MapDirection.SOUTH , animal1.getOrientation());
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(3,2), animal1.getPosition());
        animal1.move(MoveDirection.RIGHT);
        assertEquals(MapDirection.WEST , animal1.getOrientation());
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(2,2), animal1.getPosition());
        animal1.move(MoveDirection.RIGHT);
        assertEquals(MapDirection.NORTH , animal1.getOrientation());
    }
    @Test
    public void moveLeftAndBackwardTest(){
        Animal animal2 = new Animal(new Vector2d(2, 3));

        assertEquals(MapDirection.NORTH , animal2.getOrientation());
        assertEquals(new Vector2d(2,3), animal2.getPosition());
        animal2.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(2,2), animal2.getPosition());
        animal2.move(MoveDirection.LEFT);
        assertEquals(MapDirection.WEST , animal2.getOrientation());
        animal2.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(3,2), animal2.getPosition());
        animal2.move(MoveDirection.LEFT);
        assertEquals(MapDirection.SOUTH , animal2.getOrientation());
        animal2.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(3,3), animal2.getPosition());
        animal2.move(MoveDirection.LEFT);
        assertEquals(MapDirection.EAST , animal2.getOrientation());
        animal2.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(2,3), animal2.getPosition());
        animal2.move(MoveDirection.LEFT);
        assertEquals(MapDirection.NORTH , animal2.getOrientation());
    }
    @Test
    void leavesMapTest(){
        Animal animal1 = new Animal(new Vector2d(2, 4));
        Animal animal2 = new Animal(new Vector2d(4, 3));
        Animal animal3 = new Animal(new Vector2d(0, 2));
        Animal animal4 = new Animal(new Vector2d(3, 0));

        //wychodzenie na północy
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(2, 4), animal1.getPosition());
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(2, 4), animal1.getPosition());

        //wychodzenie na wschodzie
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(4, 3), animal2.getPosition());
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(4, 3), animal2.getPosition());

        //wychodzenie na południu
        animal1.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(0, 2), animal3.getPosition());
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(0, 2), animal3.getPosition());

        //wychodzenie na zachodzie
        animal1.move(MoveDirection.LEFT);
        animal1.move(MoveDirection.FORWARD);
        assertEquals(new Vector2d(3, 0), animal4.getPosition());
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.RIGHT);
        animal1.move(MoveDirection.BACKWARD);
        assertEquals(new Vector2d(3, 0), animal4.getPosition());
    }
}
*/