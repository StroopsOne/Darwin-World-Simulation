package agh.ics.oop.model;

public class Grass implements WorldElement {
    private final Vector2d grassPosition;
    private boolean grassEaten= true;
    private final int plantValue;     //wielkość rośliny

    public Grass(Vector2d grassPosition, int plantValue){
        this.grassPosition = grassPosition;
        this.plantValue=plantValue;
    }

    public int getPlantValue(){
        return plantValue;
    }

    public boolean isEaten(){ //zmieniłem nazwe na isEaten i troche logike bo chyba bardziej pasuje
        return grassEaten;
    }

    public void plantEatten(){
        grassEaten=true;
    }

    public Vector2d getPosition(){
        return grassPosition;
    }
    public String toString(){
        return "*";
    }
}
