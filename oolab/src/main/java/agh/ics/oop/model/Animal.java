package agh.ics.oop.model;

public class Animal implements WorldElement {


    private MapDirection orientation;
    private Vector2d position;
    public final static Vector2d  defaultVector = new Vector2d(2, 2);
    public MapDirection getOrientation() {
        return orientation;
    }
    public Vector2d getPosition() {

        return position;
    }

    public Animal(){
        this(defaultVector);
    }
    public Animal(Vector2d vector){
        this.orientation = MapDirection.NORTH;
        this.position = vector;
    }
    public String toString(){
        switch(orientation){
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
        }
        return "";
    }
    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }
    public void move(MoveDirection direction, MoveValidator isMoveValid) {

        Vector2d newPosition;
        switch(direction) {
            case RIGHT -> orientation = orientation.next();
            case LEFT -> orientation = orientation.previous();

            case FORWARD -> {
                newPosition = position.add(orientation.toUnitVector());
                if (isMoveValid.canMoveTo(newPosition)) {
                    position = newPosition;
                }
            }
            case BACKWARD -> {
                newPosition = position.subtract(orientation.toUnitVector());
                if (isMoveValid.canMoveTo(newPosition)) {
                    position = newPosition;
                }
            }
        }

    }
}
