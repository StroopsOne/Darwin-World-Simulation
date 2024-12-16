import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {

    @Test
    public void equalsTest(){
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(-1, 3);
        Vector2d v3 = new Vector2d(3, 4);
        assertTrue(v1.equals(v3));
        assertFalse(v1.equals(v2));
        assertFalse(v1.equals(null));
        assertFalse(v1.equals("Java"));
        assertTrue(v1.equals(v1));
    }

    @Test
    public void toStringTest(){
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(-1, 3);
        Vector2d v3 = new Vector2d(2, -5);
        assertEquals("(3,4)", v1.toString());
        assertEquals("(-1,3)", v2.toString());
        assertEquals("(2,-5)", v3.toString());
    }
    @Test
    public void precedesTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(3, 3);
        Vector2d v3 = new Vector2d(2, -5);
        Vector2d v4 = new Vector2d(3, 4);
        assertFalse(v1.precedes(v2));
        assertFalse(v2.precedes(v3));
        assertTrue(v3.precedes(v4));
        assertTrue(v1.precedes(v4));
    }
    @Test
    public void followsTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(3, 3);
        Vector2d v3 = new Vector2d(2, -5);
        Vector2d v4 = new Vector2d(3, 4);
        assertTrue(v1.follows(v2));
        assertTrue(v2.follows(v3));
        assertFalse(v3.follows(v4));
        assertTrue(v1.follows(v4));
    }
    @Test
    public void upperRightTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(3, 3);
        Vector2d v3 = new Vector2d(7, -5);
        Vector2d v4 = new Vector2d(9, -6);
        assertEquals(new Vector2d(3, 4), v1.upperRight(v2));
        assertEquals(new Vector2d(7, 3), v2.upperRight(v3));
        assertEquals(new Vector2d(9, -5), v3.upperRight(v4));
        assertEquals(new Vector2d(9, 4), v1.upperRight(v4));
    }
    @Test
    public void lowerLeftTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(5, 3);
        Vector2d v3 = new Vector2d(7, -5);
        Vector2d v4 = new Vector2d(9, -6);
        assertEquals(new Vector2d(3, 3), v1.lowerLeft(v2));
        assertEquals(new Vector2d(5, -5), v2.lowerLeft(v3));
        assertEquals(new Vector2d(7, -6), v3.lowerLeft(v4));
        assertEquals(new Vector2d(3, -6), v1.lowerLeft(v4));
    }
    @Test
    public void addTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(5, 3);
        Vector2d v3 = new Vector2d(7, -5);
        Vector2d v4 = new Vector2d(9, -6);
        assertEquals(new Vector2d(8, 7), v1.add(v2));
        assertEquals(new Vector2d(12, -2), v2.add(v3));
        assertEquals(new Vector2d(16, -11), v3.add(v4));
        assertEquals(new Vector2d(12, -2), v1.add(v4));
    }
    @Test
    public void subtractTest() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(5, 3);
        Vector2d v3 = new Vector2d(7, -5);
        Vector2d v4 = new Vector2d(5, -6);
        assertEquals(new Vector2d(-2, 1), v1.subtract(v2));
        assertEquals(new Vector2d(-2, 8), v2.subtract(v3));
        assertEquals(new Vector2d(2, 1), v3.subtract(v4));
        assertEquals(new Vector2d(-2, 10), v1.subtract(v4));
    }
    @Test
    public void opposite() {
        Vector2d v1 = new Vector2d(3, 4);
        Vector2d v2 = new Vector2d(5, 5);
        Vector2d v3 = new Vector2d(7, -5);
        Vector2d v4 = new Vector2d(5, -6);
        assertEquals(new Vector2d(4, 3), v1.opposite());
        assertEquals(new Vector2d(5, 5), v2.opposite());
        assertEquals(new Vector2d(-5, 7), v3.opposite());
        assertEquals(new Vector2d(-6, 5), v4.opposite());
    }


}
