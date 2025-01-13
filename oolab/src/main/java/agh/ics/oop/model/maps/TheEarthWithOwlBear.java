package agh.ics.oop.model.maps;

import agh.ics.oop.model.*;
import agh.ics.oop.model.mapElements.OwlBear;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.MapDirection;
import java.util.Collection;
import java.util.Random;

public class TheEarthWithOwlBear extends AbstractWorldMap {
    int owlBearTerritorySide;
    private final Vector2d lowerTerritoryCoordinates;
    private final Vector2d upperTerritoryCoordinates;
    private final OwlBear owlBear;

    public TheEarthWithOwlBear(int height, int width) {
        super(height, width);
        owlBearTerritorySide = (int) Math.sqrt(0.2 * height * width);

        lowerTerritoryCoordinates = generateOwlBearTerritory(height, width);
        upperTerritoryCoordinates = lowerTerritoryCoordinates.add(new Vector2d(owlBearTerritorySide, owlBearTerritorySide));
        int lowerX = lowerTerritoryCoordinates.getX();
        int upperX = upperTerritoryCoordinates.getX();
        int lowerY = lowerTerritoryCoordinates.getY();
        int upperY = upperTerritoryCoordinates.getY();
        Vector2d position = new Vector2d(random.nextInt(upperX - lowerX) + lowerX, random.nextInt(upperY - lowerY) + lowerY);
        MapDirection orientation = MapDirection.randomDirection();
        this.owlBear = new OwlBear(position, orientation);
    }

    public Vector2d generateOwlBearTerritory(int height, int width){ //wybiera losowe pole, które jest lewym dolnym rogiem terytorium SowoNiedźwiedzia
        Random random = new Random();
        int lowerX = random.nextInt(width - owlBearTerritorySide -1) + owlBearTerritorySide;
        int lowerY = random.nextInt(height - owlBearTerritorySide -1) + owlBearTerritorySide;
        return new Vector2d(lowerX, lowerY);
    }

    @Override
    public void moveAllAnimals(){
        super.moveAllAnimals();
        moveOwlBear(owlBear);
    }

    public void moveOwlBear(OwlBear owlBear) {
        Random random = new Random();
        int gene = random.nextInt(8);
        Vector2d oldPosition = owlBear.getPosition();
        owlBear.move(gene, this::canMoveOwlBearTo); //nadpisuje metode canMoveTo, aby działała jak canMoveOwlBearTo
        Vector2d newPosition = owlBear.getPosition();

        if (!oldPosition.equals(newPosition)) {
            notifyAllObservers("owlBear moved to " + newPosition);
        }
    }

    public boolean canMoveOwlBearTo(Vector2d position) {
        return position.follows(lowerTerritoryCoordinates) && position.precedes(upperTerritoryCoordinates);
    }

    @Override
    public Collection<WorldElement> getElements(){
        Collection<WorldElement> elements = super.getElements();
        elements.add(owlBear);
        return elements;
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        if (owlBear.isAtPosition(position)){
            return owlBear;
        } else{
            return super.objectAt(position);
        }
    }
}
