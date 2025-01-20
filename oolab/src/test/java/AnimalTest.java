import agh.ics.oop.model.Enums.MapDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.MoveValidator;
import agh.ics.oop.model.properities.Genomes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    @Test
    void testInitialPosition() {
        Vector2d initialPosition = new Vector2d(2, 3);
        Animal animal = new Animal(initialPosition,200,8);
        assertEquals(initialPosition, animal.getPosition(), "Initial position should be set correctly");
    }

    @Test
    void genomesTest(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        Genomes genome=new Genomes(list,0,0,false);
        Vector2d position=new Vector2d(2,3);
        Animal mom=new Animal(position,200,8);
        Animal dad=new Animal(position,200,8);
        Animal animal=new Animal(position,50,genome,mom,dad);
        assertEquals(genome,animal.getGenes());
        assertEquals(50,animal.getEnergy());
        animal.changeEnergy(-100);
        assertEquals(0,animal.getEnergy());
    }
}
