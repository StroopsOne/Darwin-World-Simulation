package agh.ics.oop.model;

public class Animal implements WorldElement {


    private MapDirection orientation;
    private Vector2d position;
    public final static Vector2d  defaultVector = new Vector2d(2, 2);
    public MapDirection getOrientation() {
        return orientation;
    }
    private int energy;
    private final int ReadyForKidsEnergy=100;       //ilość energii ustalona do rozmnażania
    private boolean alive=true;

    public Vector2d getPosition() {

        return position;
    }
    public Animal(Vector2d vector, int energy){         //usunąłem deafult vector bo w projekcie nie ma już sensu, miejsce zwierzęcia i tak losowane
        this.orientation = MapDirection.NORTH;
        this.position = vector;
        this.energy=energy;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isAlive(){
        return alive;
    }

    public void dayPasses(){
        energy=energy-1;
        if (energy==0)  alive=false;
    }

    public void killedBySowoniedzwiedz(){
        alive=false;
    }

    public void plantEatten(int plantValue){
        energy+=plantValue;
    }

    public boolean ReadyForKids(){
        if (energy>ReadyForKidsEnergy)  return true;
        else return false;
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
    public void move(int gene/* MoveValidator isMoveValid*/) {
        Vector2d newPosition;

        for (int i=0; i < gene; i++){
            orientation = orientation.next();
        }
        position = position.add(orientation.toUnitVector());
        /*
        newPosition = position.add(orientation.toUnitVector());
        if (isMoveValid.canMoveTo(newPosition)) {
            position = newPosition;
        }
        */
    }
}
