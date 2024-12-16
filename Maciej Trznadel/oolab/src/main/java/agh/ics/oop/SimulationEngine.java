package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine implements Runnable {
    private final List<Simulation> simulationList;
    private final List<Thread> threadList;
    private ExecutorService executorService;
    public SimulationEngine(List<Simulation> simulationList){
        this.simulationList = simulationList;
        this.threadList = new ArrayList<>();
    }

    public void runsync(){

        for (Simulation currentSimulation : simulationList){
            currentSimulation.run();
        }
    }

    public void runAsync(){

        for (Simulation currentSimulation : simulationList){
            Thread thread = new Thread(currentSimulation);
            threadList.add(thread);
            thread.start();
        }
    }
    public void runAsyncinThreadPool(){
        executorService = Executors.newFixedThreadPool(4);
        for(Simulation simulation : simulationList) {
            executorService.submit(simulation);
        }
    }

    public void awaitSimulationsEnd() throws InterruptedException {
        for (Thread thread : threadList){
            try {
                thread.join();
            }catch (InterruptedException e){
                throw e;
            }
        }
        if (executorService != null) {
            executorService.shutdown();
            if(!executorService.awaitTermination(10, TimeUnit.SECONDS)){
                System.out.println("Zadanie w puli nie zdazyly sie wykonac");
                executorService.shutdownNow();
            }


        }
    }

    @Override
    public void run() {
        System.out.println("Thread started.");
    }
}
