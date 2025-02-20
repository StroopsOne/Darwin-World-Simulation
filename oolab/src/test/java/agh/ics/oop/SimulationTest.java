package agh.ics.oop;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.Exceptions.IncorrectPositionException;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarth;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void testInitialization() throws IncorrectPositionException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        map.placeStartObjects(5, 10, 50, 100, 8);

        assertEquals(5, map.getLivingAnimalsCount(), "Initial number of animals should be 5.");
    }

    @Test
    void testDailyCycle() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(1000, 1000, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        Thread.sleep(1500);
        System.out.println("Grass count after 1.5s: " + map.getGrassCount());

        Thread.sleep(1000);
        System.out.println("Grass count after 2.5s: " + map.getGrassCount());

        Thread.sleep(1000);
        System.out.println("Grass count after 3.5s: " + map.getGrassCount());

        simulation.pauseSimulation();
        simulationThread.interrupt();

        assertTrue(map.getLivingAnimalsCount() > 0, "Animals should still be alive after one day.");
        assertTrue(map.getGrassCount() >= 15, "Grass count should increase by daily grass.");
    }


    @Test
    void testPauseAndResume() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 5);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Pauza symulacji
        simulation.pauseSimulation();
        Thread.sleep(500);
        int animalsBeforePause = map.getLivingAnimalsCount();

        // Wznowienie symulacji
        simulation.resumeSimulation();
        Thread.sleep(1500); // Poczekaj na zakończenie jednego cyklu
        simulationThread.interrupt();

        int animalsAfterResume = map.getLivingAnimalsCount();

        assertTrue(animalsAfterResume >= animalsBeforePause, "Number of animals should not decrease while paused.");
    }

    @Test
    void testMultipleDays() throws IncorrectPositionException, InterruptedException {
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 5, 100, 8, 50, 10, 10);

        Thread simulationThread = new Thread(simulation);
        simulationThread.start();

        // Poczekaj na zakończenie kilku dni
        Thread.sleep(5200);

        // Pauza i zakończenie symulacji
        simulation.pauseSimulation();
        simulationThread.interrupt();

        assertTrue(map.getLivingAnimalsCount() > 0, "Animals should still be alive after multiple days.");
        assertTrue(map.getGrassCount() > 15, "Grass count should increase over multiple days.");
    }

    @Test
    void EnergyTest() throws IncorrectPositionException, InterruptedException{
        AbstractWorldMap map = new TheEarth(10, 10, 1, 3, 50, 20, true);
        Simulation simulation = new Simulation(map, 1, 2, 8, 0, 0, 0);
        Thread simulationThread = new Thread(simulation);
        simulationThread.start();
        java.util.List<Animal> listOfAnimal = map.getAllLivingAnimals();
        Thread.sleep(1500);
        for (int i=0;i<listOfAnimal.size();i++){
            System.out.println(listOfAnimal.get(i).getEnergy());
        }
        Thread.sleep(1000);
        for (int i=0;i<listOfAnimal.size();i++){
            System.out.println(listOfAnimal.get(i).getEnergy());
        }
        Thread.sleep(1000);
        for (int i=0;i<listOfAnimal.size();i++){
            System.out.println(listOfAnimal.get(i).getEnergy());
        }
        Thread.sleep(1000);
        for (int i=0;i<listOfAnimal.size();i++){
            System.out.println(listOfAnimal.get(i).getEnergy());
        }
        assertEquals(1,map.getDeadAnimalsCount());
    }
}
