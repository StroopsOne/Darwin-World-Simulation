package agh.ics.oop;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarth;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void testInitialization() throws IncorrectPositionException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        // Sprawdzamy, czy symulacja poprawnie inicjalizuje mapę
        assertEquals(5, map.getLivingAnimalsCount(), "Initial number of animals should be 5.");
        assertEquals(10, map.getGrassCount(), "Initial number of grasses should be 10.");
    }

    @Test
    void testDailyCycle() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Poczekaj na zakończenie jednego cyklu
        Thread.sleep(1200);

        // Zatrzymaj symulację
        simulation.pauseSimulation();
        simulationThread.interrupt();

        // Sprawdzamy stan po jednym dniu
        assertTrue(map.getLivingAnimalsCount() > 0, "Animals should still be alive after one day.");
        assertEquals(15, map.getGrassCount(), "Grass count should increase by daily grass.");
    }

    @Test
    void testPauseAndResume() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Pauzuj symulację
        simulation.pauseSimulation();
        Thread.sleep(500);
        int animalsBeforePause = map.getLivingAnimalsCount();

        // Wznów symulację
        simulation.resumeSimulation();
        Thread.sleep(1500);
        simulationThread.interrupt();

        int animalsAfterResume = map.getLivingAnimalsCount();

        assertTrue(animalsAfterResume >= animalsBeforePause, "Number of animals should not decrease while paused.");
    }

    @Test
    void testMultipleDays() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Poczekaj na zakończenie 5 dni
        Thread.sleep(5200);

        // Zatrzymaj symulację
        simulation.pauseSimulation();
        simulationThread.interrupt();

        assertTrue(map.getLivingAnimalsCount() > 0, "Animals should still be alive after multiple days.");
        assertTrue(map.getGrassCount() > 10, "Grass count should increase over multiple days.");
    }

    @Test
    void testDeadAnimals() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 1, 8, 50, 10, 5); // Zwierzęta z małą energią

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Poczekaj na zakończenie jednego dnia
        Thread.sleep(1200);

        // Zatrzymaj symulację
        simulation.pauseSimulation();
        simulationThread.interrupt();

        assertEquals(0, map.getLivingAnimalsCount(), "All animals should be dead after one day with low energy.");
        assertEquals(5, map.getDeadAnimalsCount(), "Dead animals count should match initial animal count.");
    }
}
