package agh.ics.oop.model;


public enum MapDirection {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    public String toString(){
        return switch(this) {
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH -> "Południe";
        };
    }
    public MapDirection next(){
        return switch(this){
            case EAST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.WEST;
            case WEST -> MapDirection.NORTH;
            case NORTH -> MapDirection.EAST;
        };
    }
    public MapDirection previous(){
        return switch(this){
            case EAST -> MapDirection.NORTH;
            case NORTH -> MapDirection.WEST;
            case WEST -> MapDirection.SOUTH;
            case SOUTH -> MapDirection.EAST;
        };
    }
    final Vector2d vectorNorth = new Vector2d(0, 1);
    final Vector2d vectorEast = new Vector2d(1, 0);
    final Vector2d vectorSouth = new Vector2d(0, -1);
    final Vector2d vectorWest = new Vector2d(-1, 0);


    public Vector2d toUnitVector(){
        return switch(this){
            case NORTH -> vectorNorth;
            case EAST -> vectorEast;
            case SOUTH -> vectorSouth;
            case WEST -> vectorWest;

        };
    }
}
