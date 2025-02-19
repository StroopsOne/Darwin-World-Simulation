package agh.ics.oop.Statistics;

import agh.ics.oop.model.maps.AbstractWorldMap;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class SimulationCharts {
    private final LineChart<Number, Number> chart;
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> lifespanSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> childrenSeries = new XYChart.Series<>();

    public SimulationCharts() {
        // Oś X (Dzień)
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Days");

        // Oś Y (Ilość)
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Count");

        // Tworzenie wykresu
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Simulation Chart");
        chart.setCreateSymbols(false);

        // Dodanie serii danych do wykresu
        animalSeries.setName("Number of Animals");
        plantSeries.setName("Number of plants");
        energySeries.setName("Average energy");
        lifespanSeries.setName("Average lifespan");
        childrenSeries.setName("Average offspring count");

        chart.getData().addAll(animalSeries, plantSeries, energySeries, lifespanSeries, childrenSeries);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public void updateChart(int day, AbstractWorldMap map) {
        animalSeries.getData().add(new XYChart.Data<>(day, map.getLivingAnimalsCount()));
        plantSeries.getData().add(new XYChart.Data<>(day, map.getGrassCount()));
        energySeries.getData().add(new XYChart.Data<>(day, (int) map.getAvgLivingAnimalsEnergy()));
        lifespanSeries.getData().add(new XYChart.Data<>(day, (int) map.getAvgDeadAnimalsLifespan()));
        childrenSeries.getData().add(new XYChart.Data<>(day, (int) map.getAvgChildrenCount()));
    }
}
