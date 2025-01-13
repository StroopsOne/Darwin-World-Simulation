package agh.ics.oop.model;


import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public String toString(){
        return switch(this) {
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH -> "Południe";
            case NORTHEAST -> "Północny wschód";
            case SOUTHEAST -> "Południowy wschód";
            case SOUTHWEST -> "Południowy zachód";
            case NORTHWEST -> "Północny zachód";
        };
    }
    public MapDirection next(){
        return switch(this){
            case NORTH -> MapDirection.NORTHEAST;
            case NORTHEAST -> MapDirection.EAST;
            case EAST -> MapDirection.SOUTHEAST;
            case SOUTHEAST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.SOUTHWEST;
            case SOUTHWEST -> MapDirection.WEST;
            case WEST -> MapDirection.NORTHWEST;
            case NORTHWEST -> MapDirection.NORTH;

        };
    }
    /*public MapDirection previous(){
        return switch(this){
            case EAST -> MapDirection.NORTH;
            case NORTH -> MapDirection.WEST;
            case WEST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.EAST;
        };
    }*/
    final Vector2d vectorNorth = new Vector2d(0, 1);
    final Vector2d vectorEast = new Vector2d(1, 0);
    final Vector2d vectorSouth = new Vector2d(0, -1);
    final Vector2d vectorWest = new Vector2d(-1, 0);
    final Vector2d vectorNorthWest = new Vector2d(-1, 1);
    final Vector2d vectorSouthWest = new Vector2d(-1, -1);
    final Vector2d vectorNorthEast = new Vector2d(1, 1);
    final Vector2d vectorSouthEast = new Vector2d(1, -1);

    public static MapDirection randomDirection(){
        MapDirection directions[]=values();
        int randomIndex=new Random().nextInt(directions.length);
        return directions[randomIndex];
    }



    public Vector2d toUnitVector(){
        return switch(this){
            case NORTH -> vectorNorth;
            case EAST -> vectorEast;
            case SOUTH -> vectorSouth;
            case WEST -> vectorWest;
            case NORTHEAST -> vectorNorthEast;
            case SOUTHEAST -> vectorSouthEast;
            case SOUTHWEST -> vectorSouthWest;
            case NORTHWEST -> vectorNorthWest;

        };
    }
}
