package agh.ics.oop.model;

import agh.ics.oop.model.util.MapVisualizer;

import java.util.HashMap;
import java.util.Map;

public class RectangularMap extends AbstractWorldMap {
    private final Vector2d maxVector;
    private final Vector2d minVector = new Vector2d(0, 0);

    public RectangularMap(int width, int height) {
        super();

        this.maxVector = new Vector2d(width - 1, height - 1);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(maxVector) && position.follows(minVector) && !isOccupied(position);
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(minVector, maxVector);
    }


}

