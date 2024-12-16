package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.ConsoleMapDisplay;
import agh.ics.oop.model.IncorrectPositionException;
import agh.ics.oop.model.util.MapChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {

    public static void main(String[] args) throws InterruptedException {
        List<Simulation> simulationList = null;
        try {
            List<MoveDirection> possibleDirections = new ArrayList<>();
            possibleDirections.add(MoveDirection.LEFT);
            possibleDirections.add(MoveDirection.RIGHT);
            possibleDirections.add(MoveDirection.FORWARD);
            possibleDirections.add(MoveDirection.BACKWARD);
            simulationList = new ArrayList<>();
            MapChangeListener observer = new ConsoleMapDisplay();
            Random random = new Random();
            for (int i = 0; i < 2000; i++) {
                List<Vector2d> positions = new ArrayList<>();
                List<MoveDirection> moves = new ArrayList<>();
                int mapType = random.nextInt(2);
                if (mapType == 0) {
                    int numVectors = random.nextInt(10) + 1;
                    int numMoves = random.nextInt(20) + 1;
                    int grassPoints = random.nextInt(10) + 1;
                    GrassField animalsMap = new GrassField(grassPoints);
                    animalsMap.addObserver(observer);
                    for (int j = 0; j < numVectors; j++) {
                        int x = random.nextInt(grassPoints * 2) + 1;
                        int y = random.nextInt(grassPoints * 2) + 1;
                        positions.add(new Vector2d(x, y));
                    }
                    for (int k = 0; k < numMoves; k++) {
                        int moveType = random.nextInt(4);
                        moves.add(possibleDirections.get(moveType));
                    }
                    Simulation simulation = new Simulation(positions, moves, animalsMap);
                    simulationList.add(simulation);
                } else {
                    int numVectors = random.nextInt(10) + 1;
                    int numMoves = random.nextInt(20) + 1;
                    int width = random.nextInt(10) + 1;
                    int height = random.nextInt(10) + 1;
                    RectangularMap animalsMap = new RectangularMap(width, height);
                    animalsMap.addObserver(observer);
                    for (int j = 0; j < numVectors; j++) {
                        int x = random.nextInt(width);
                        int y = random.nextInt(height);
                        positions.add(new Vector2d(x, y));
                    }
                    for (int k = 0; k < numMoves; k++) {
                        int moveType = random.nextInt(4);
                        moves.add(possibleDirections.get(moveType));
                    }
                    Simulation simulation = new Simulation(positions, moves, animalsMap);
                    simulationList.add(simulation);

                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IncorrectPositionException e) {
            throw new RuntimeException(e);
        }
        SimulationEngine simulationEngine = new SimulationEngine(simulationList);


        simulationEngine.runAsyncinThreadPool();
        try {
            simulationEngine.awaitSimulationsEnd();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("System zakonczyl dzialanie");
    }
}
