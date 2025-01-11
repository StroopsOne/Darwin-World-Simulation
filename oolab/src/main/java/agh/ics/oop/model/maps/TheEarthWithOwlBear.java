package agh.ics.oop.model.maps;

import agh.ics.oop.model.*;
import agh.ics.oop.model.mapElements.OwlBear;
import agh.ics.oop.model.mapElements.WorldElement;

import java.util.Collection;
import java.util.Random;

public class TheEarthWithOwlBear extends AbstractWorldMap {
    OwlBear owlBear;
    int owlBearTerritorySide;
    Vector2d lowerTerritoryCoordinates;
    Vector2d upperTerritoryCoordinates;

    public TheEarthWithOwlBear(int height, int width, OwlBear owlBear) {
        super(height, width);
        this.owlBear = owlBear;
        owlBearTerritorySide = (int) Math.sqrt(0.2 * height * width);

        lowerTerritoryCoordinates = generateOwlBearTerritory(height, width);
        upperTerritoryCoordinates = lowerTerritoryCoordinates.add(new Vector2d(owlBearTerritorySide, owlBearTerritorySide));
    }

    public Vector2d generateOwlBearTerritory(int height, int width){ //wybiera losowe pole, które jest lewym dolnym rogiem terytorium SowoNiedźwiedzia
        Random random = new Random();
        int lowerX = random.nextInt(width - owlBearTerritorySide -1) + owlBearTerritorySide;
        int lowerY = random.nextInt(height - owlBearTerritorySide -1) + owlBearTerritorySide;
        return new Vector2d(lowerX, lowerY);
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
