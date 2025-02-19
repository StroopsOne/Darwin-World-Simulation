package agh.ics.oop.presenter;

import agh.ics.oop.model.mapElements.Animal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class AnimalEnergyChart {
    private final Animal animal;
    private final XYChart.Series<Number, Number> energySeries;
    private final int startDay;

    public AnimalEnergyChart(Animal animal, int currentDay) {
        this.animal = animal;
        this.startDay = currentDay;

        Stage stage = new Stage();
        stage.setTitle("Energy Chart for Animal: ");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Days");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy Level");

        LineChart<Number, Number> energyChart = new LineChart<>(xAxis, yAxis);
        energyChart.setTitle("Energy Level Over Time");

        energySeries = new XYChart.Series<>();
        energySeries.setName("Energy Level");
        energyChart.getData().add(energySeries);

        Scene scene = new Scene(energyChart, 800, 600);
        stage.setScene(scene);

        stage.show();
    }

    public void updateChart(int currentDay) {
        Platform.runLater(() -> {
            int relativeDay = currentDay - startDay;
            energySeries.getData().add(new XYChart.Data<>(relativeDay, animal.getEnergy()));
        });
    }

    public Animal getAnimal() {
        return this.animal;
    }
}