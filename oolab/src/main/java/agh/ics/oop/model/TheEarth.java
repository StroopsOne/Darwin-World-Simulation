package agh.ics.oop.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TheEarth extends AbstractWorldMap {
    private final Map<Vector2d, Grass> grassPoints = new HashMap<>();
    private final Vector2d maxVector;
    private final Vector2d minVector;


    public TheEarth(int height, int width) {
        super(height, width);
        maxVector = new Vector2d(width-1, height-1);
        minVector = new Vector2d(0,0);
        Random random = new Random();

    }

    public void placeGrass(Vector2d position, Grass grass){
        grassPoints.put(position, grass);
    }


    @Override
    public Collection<WorldElement> getElements() {
        Collection<WorldElement> elements = super.getElements();
        elements.addAll(grassPoints.values());
        return elements;
    }

    public boolean isGrassOnPosition(Vector2d position){
        return (grassPoints.containsKey(position) && !grassPoints.get(position).isEaten());
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(minVector) && position.precedes(maxVector);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) {
            return animals.get(position);
        }
        return grassPoints.getOrDefault(position, null);
    }

}

