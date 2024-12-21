import agh.ics.oop.OptionsParser;
import agh.ics.oop.model.MoveDirection;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class OptionsParserTest {
    @Test
    public void parseTest(){
        String[] directions = {"f", "t", "l", "r", "b", "l", "a", "f"};



        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> OptionsParser.parse(directions)
        );
        assertEquals("t is not legal move specification", thrown.getMessage());
    }

}

