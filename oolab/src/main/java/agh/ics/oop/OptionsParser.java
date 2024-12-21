package agh.ics.oop;
import agh.ics.oop.model.MoveDirection;

import java.util.ArrayList;


public class OptionsParser {

    public static ArrayList<MoveDirection> parse(String[] args) {
        ArrayList<MoveDirection> directions = new ArrayList<>();

            for (String direction : args) {
                switch (direction) {
                    case "f" -> directions.add(MoveDirection.FORWARD);
                    case "b" -> directions.add(MoveDirection.BACKWARD);
                    case "r" -> directions.add(MoveDirection.RIGHT);
                    case "l" -> directions.add(MoveDirection.LEFT);
                    default -> throw new IllegalArgumentException(direction + " is not legal move specification");
                }
            }

        return directions;
    }
}
