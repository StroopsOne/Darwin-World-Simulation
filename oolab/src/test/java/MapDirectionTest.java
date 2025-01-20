import agh.ics.oop.model.Enums.MapDirection;
import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class MapDirectionTest {
    @Test
    public void nextTest() {
        assertEquals(MapDirection.SOUTHWEST, MapDirection.SOUTH.next());
        assertEquals(MapDirection.NORTH, MapDirection.WEST.next().next());
        assertEquals(MapDirection.NORTHEAST, MapDirection.NORTH.next());
        assertEquals(MapDirection.SOUTH, MapDirection.EAST.next().next());
    }
    @Test
    public void toUnitVectorTest(){
        assertEquals(new Vector2d(1,1),MapDirection.NORTHEAST.toUnitVector());
        assertEquals(new Vector2d(-1,1),MapDirection.NORTHWEST.toUnitVector());
        assertEquals(new Vector2d(0,-1),MapDirection.SOUTH.toUnitVector());
    }
}
