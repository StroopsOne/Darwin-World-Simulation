
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.MoveDirection;
import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnimalTest {

    @Test
    public void moveTest(){
        Animal animal = new Animal(new Vector2d(2,2));
        animal.move(0);
        assertEquals(new Vector2d(2,3), animal.getPosition());
        animal.move(0);
        assertEquals(new Vector2d(2,4), animal.getPosition());
        animal.move(7);
        assertEquals(new Vector2d(1,5), animal.getPosition());
        animal.move(0);
        assertEquals(new Vector2d(0,6), animal.getPosition());
        animal.move(4);
        assertEquals(new Vector2d(1,5), animal.getPosition());


    }
}
