package agh.ics.oop.model;

public class Grass implements WorldElement {
    private final Vector2d grassPosition;
    private boolean notEatten= true;
    private final int plantValue;     //wielkość rośliny

    public Grass(Vector2d grassPosition, int plantValue){
        this.grassPosition = grassPosition;
        this.plantValue=plantValue;
    }

    public int getPlantValue(){
        return plantValue;
    }

    public boolean isAlive(){
        if (notEatten)  return true;
        else return false;
    }

    public void plantEatten(){
        notEatten=false;
    }

    public Vector2d getPosition(){
        return grassPosition;
    }
    public String toString(){
        return "*";
    }
}
