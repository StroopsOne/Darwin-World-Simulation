package agh.ics.oop.presenter;

import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.WorldMap;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class AnimalEnergyChart {
    private final Animal animal;
    private final WorldMap map;
    private final XYChart.Series<Number, Number> animalEnergySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> AvgEnergySeries = new XYChart.Series<>();
    private final int startDay;

    public AnimalEnergyChart(Animal animal, WorldMap map, int currentDay) {
        this.animal = animal;
        this.map = map;
        this.startDay = currentDay;

        Stage stage = new Stage();
        stage.setTitle("Animal Energy Chart ");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Days");
        xAxis.setForceZeroInRange(false);
        xAxis.setLowerBound(startDay);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy Level");

        LineChart<Number, Number> energyChart = new LineChart<>(xAxis, yAxis);
        energyChart.setTitle("Energy Level Over Time");
        energyChart.setCreateSymbols(false);

        animalEnergySeries.setName("Animal Energy Level");
        AvgEnergySeries.setName("Average Energy Level");
        energyChart.getData().addAll(animalEnergySeries, AvgEnergySeries);

        Scene scene = new Scene(energyChart, 400, 300);
        stage.setScene(scene);

        stage.show();
    }

    public void updateChart(int currentDay) {
        Platform.runLater(() -> {
            animalEnergySeries.getData().add(new XYChart.Data<>(currentDay, animal.getEnergy()));
            AvgEnergySeries.getData().add(new XYChart.Data<>(currentDay, map.getAvgLivingAnimalsEnergy()));
        });
    }

    public Animal getAnimal() {
        return this.animal;
    }
}