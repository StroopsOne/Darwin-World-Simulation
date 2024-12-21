package agh.ics.oop.model;

import agh.ics.oop.model.util.MapVisualizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap {
    private final Map<Vector2d, Grass> grassPoints = new HashMap<>();
    private final Vector2d minVector = new Vector2d(0, 0);
    private final Vector2d maxVector = new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);


    public GrassField(int grassQuantity) {
        super();
        Random random = new Random();
        int maxCoordinate = (int) Math.sqrt(grassQuantity * 10);
        while (grassPoints.size() < grassQuantity) {
            int x = random.nextInt(maxCoordinate + 1);
            int y = random.nextInt(maxCoordinate + 1);
            Vector2d position = new Vector2d(x, y);

            if (!grassPoints.containsKey(position)) {
                grassPoints.put(position, new Grass(position));
            }
        }

    }

    @Override
    public Collection<WorldElement> getElements() {
        Collection<WorldElement> elements = super.getElements();
        elements.addAll(grassPoints.values());
        return elements;
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(minVector) && !animals.containsKey(position);
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (animals.containsKey(position)) {
            return animals.get(position);
        }
        return grassPoints.getOrDefault(position, null);
    }

    @Override
    public Boundary getCurrentBounds() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        Vector2d upperRightVector = new Vector2d(maxX, maxY);
        Vector2d lowerLeftVector = new Vector2d(minX, minY);
        for (Vector2d position : animals.keySet()) {
            lowerLeftVector = lowerLeftVector.lowerLeft(position);
            upperRightVector = upperRightVector.upperRight(position);
        }
        for (Vector2d position : grassPoints.keySet()) {
            lowerLeftVector = lowerLeftVector.lowerLeft(position);
            upperRightVector = upperRightVector.upperRight(position);
        }
        return new Boundary(lowerLeftVector, upperRightVector);
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || grassPoints.containsKey(position);
    }
}

