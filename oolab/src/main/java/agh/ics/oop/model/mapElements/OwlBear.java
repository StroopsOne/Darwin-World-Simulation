package agh.ics.oop.model.mapElements;

import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.maps.MoveValidator;

public class OwlBear implements WorldElement {

    private MapDirection orientation;
    private Vector2d position;

    public OwlBear(Vector2d position, MapDirection orientation){
        this.position = position;
        this.orientation = orientation;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() { return position; }

    public boolean isAtPosition(Vector2d position){
        return this.position.equals(position);
    }

    public void move(int gene, MoveValidator isMoveValid) {
        Vector2d newPosition;

        for (int i = 0; i < gene; i++) {
            orientation = orientation.next();
        }

        newPosition = position.add(orientation.toUnitVector());
        if (isMoveValid.canMoveTo(newPosition)) {
            position = newPosition;
        }
    }


    public String toString() {
        switch (orientation) {
            case NORTH -> {
                return "N";
            }
            case SOUTH -> {
                return "S";
            }
            case EAST -> {
                return "E";
            }
            case WEST -> {
                return "W";
            }
            case NORTHEAST -> {
                return "NE";
            }
            case NORTHWEST -> {
                return "NW";
            }
            case SOUTHEAST -> {
                return "SE";
            }
            case SOUTHWEST -> {
                return "SW";
            }
        }
        return "";
    }
}

