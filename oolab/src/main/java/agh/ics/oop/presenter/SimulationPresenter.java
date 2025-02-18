package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.Statistics.AnimalStatistics;
import agh.ics.oop.Statistics.SimulationStatistics;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import agh.ics.oop.model.maps.WorldMap;
import agh.ics.oop.model.util.MapChangeListener;
import agh.ics.oop.model.Boundary;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimulationPresenter implements MapChangeListener {
    private AbstractWorldMap map;
    private Simulation simulation;
    private AnimalStatistics selectedAnimalStats;
    private PrintWriter csvWriter;
    private boolean generateCsv;
    private boolean showAnimalStats = false;
    private int startAnimalCount, startEnergy, genomeSize, grassNutrient, dailyGrassSpawn, initialGrass, currentDay = 0;

    private static final Color GRASS_COLOR = Color.GREEN;
    private static final Color EMPTY_CELL_COLOR = Color.rgb(110, 204, 38);
    private static final Color OWLBEAR_COLOR = Color.RED;

    // Nazwy pól muszą odpowiadać identyfikatorom w simulation.fxml
    @FXML private TextField mostCommonGenotypesField, totalAnimalsField, totalPlantsField, freeFieldsField,
            averageEnergyField, averageLifeSpanField, averageChildrenCountField;
    @FXML private TextField genomeField, activePartField, energyField, eatenPlantsField, childrenCountField, ageField, deathDayField;
    @FXML private Label currentDayField;
    @FXML private Button startStopButton;
    @FXML private GridPane mapGrid;

    public void configureMap(AbstractWorldMap worldMap) {
        this.map = worldMap;
    }

    public void setInitialParams(int animals, int energy, int genome, int grassValue, int grassStart, int dailyGrass) {
        this.startAnimalCount = animals;
        this.startEnergy = energy;
        this.genomeSize = genome;
        this.grassNutrient = grassValue;
        this.initialGrass = grassStart;
        this.dailyGrassSpawn = dailyGrass;
    }

    public void enableCsvExport(boolean enable) {
        this.generateCsv = enable;
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0));
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    @FXML
    public void renderMap() {
        clearGrid();
        Boundary bounds = map.getCurrentBounds();
        int gridSize = 800 / Math.max(bounds.upperRight().getX() - bounds.lowerLeft().getX() + 1,
                bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1);
        drawGrid(bounds, gridSize);
    }

    private void drawGrid(Boundary bounds, int cellSize) {
        for (int y = bounds.lowerLeft().getY(); y <= bounds.upperRight().getY(); y++) {
            for (int x = bounds.lowerLeft().getX(); x <= bounds.upperRight().getX(); x++) {
                Vector2d pos = new Vector2d(x, y);
                drawCell(pos, x - bounds.lowerLeft().getX() + 1, bounds.upperRight().getY() - y + 1, cellSize);
            }
        }
    }

    private void drawCell(Vector2d pos, int col, int row, int size) {
        WorldElement element = map.objectAt(pos);
        Node node = createVisualElement(element, pos, size);
        mapGrid.add(node, col, row);
    }

    private Node createVisualElement(WorldElement element, Vector2d pos, int size) {
        StackPane container = new StackPane();
        container.setMinSize(size, size);
        container.setMaxSize(size, size);
        Rectangle cell = new Rectangle(size, size);
        cell.setFill(map.isGrassOnPosition(pos) ? GRASS_COLOR : EMPTY_CELL_COLOR);
        container.getChildren().add(cell);

        if (element instanceof Animal animal) {
            Circle animalCircle = new Circle(size / 5, animal.toColor(startEnergy));
            animalCircle.setOnMouseClicked(event -> toggleAnimalStats(animal));
            container.getChildren().add(animalCircle);
        }

        if (map instanceof TheEarthWithOwlBear && ((TheEarthWithOwlBear) map).isOwlBearAtPosition(pos)) {
            Rectangle owlBear = new Rectangle(size / 2, size / 2, OWLBEAR_COLOR);
            owlBear.setArcWidth(10);
            owlBear.setArcHeight(10);
            container.getChildren().add(owlBear);
        }

        return container;
    }

    private void updateSelectedAnimalStats() {
        if (selectedAnimalStats != null) {
            genomeField.setText(selectedAnimalStats.getGenome().toString());
            activePartField.setText(String.valueOf(selectedAnimalStats.getActivePart()));
            energyField.setText(String.valueOf(selectedAnimalStats.getEnergy()));
            eatenPlantsField.setText(String.valueOf(selectedAnimalStats.getEatenPlants()));
            childrenCountField.setText(String.valueOf(selectedAnimalStats.getChildrenCount()));
            ageField.setText(String.valueOf(selectedAnimalStats.getAge()));
            deathDayField.setText(selectedAnimalStats.getDeathDay() != null ? String.valueOf(selectedAnimalStats.getDeathDay()) : "");
        }
    }

    @Override
    public void mapChanged(WorldMap updatedMap) {
        Platform.runLater(() -> {
            renderMap();
            updateGlobalStats(updatedMap);
            updateSelectedAnimalStats();
            updateDayCounter();
            currentDay++;
        });
    }

    private void toggleAnimalStats(Animal animal) {
        if (!showAnimalStats) {
            selectedAnimalStats = new AnimalStatistics(animal, simulation);
            genomeField.setText(selectedAnimalStats.getGenome().toString());
            activePartField.setText(String.valueOf(selectedAnimalStats.getActivePart()));
            energyField.setText(String.valueOf(selectedAnimalStats.getEnergy()));
            eatenPlantsField.setText(String.valueOf(selectedAnimalStats.getEatenPlants()));
            childrenCountField.setText(String.valueOf(selectedAnimalStats.getChildrenCount()));
            ageField.setText(String.valueOf(selectedAnimalStats.getAge()));
            deathDayField.setText(selectedAnimalStats.getDeathDay() != null ? String.valueOf(selectedAnimalStats.getDeathDay()) : "");
        } else {
            updateGlobalStats(map);
        }
        showAnimalStats = !showAnimalStats;
    }

    private void updateGlobalStats(WorldMap updatedMap) {
        SimulationStatistics stats = new SimulationStatistics(simulation, (AbstractWorldMap) updatedMap);
        totalAnimalsField.setText(String.valueOf(stats.getTotalAnimals()));
        totalPlantsField.setText(String.valueOf(stats.getTotalPlants()));
        freeFieldsField.setText(String.valueOf(stats.getFreeFields()));
        averageEnergyField.setText(String.format("%.2f", stats.getAverageEnergy()));
        averageLifeSpanField.setText(String.format("%.2f", stats.getAverageLifeSpan()));
        averageChildrenCountField.setText(String.format("%.2f", stats.getAverageChildrenCount()));
        mostCommonGenotypesField.setText(stats.getMostCommonGenotypes().toString());
    }

    private void updateDayCounter() {
        currentDayField.setText(String.valueOf(simulation.getDay()));
    }

    @FXML
    public void initialize() {
        startStopButton.setText("Start");
    }

    @FXML
    public void onStartStopButtonClicked() {
        System.out.println("Simulation button clicked");
        try {
            if (simulation == null) {
                if (generateCsv) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    LocalDateTime now = LocalDateTime.now();
                    String filename = "simulation_data_" + dtf.format(now) + ".csv";
                    csvWriter = new PrintWriter(new FileWriter(filename, true));
                    csvWriter.println("Day,Total Animals,Total Plants,Free Fields,Most Common Genotypes,Average Energy,Average Life Span,Average Children Count");
                }
                simulation = new Simulation(map, startAnimalCount, startEnergy, genomeSize, grassNutrient, initialGrass, dailyGrassSpawn);
                Thread simulationThread = new Thread(simulation);
                simulationThread.setDaemon(true);
                simulationThread.start();
                startStopButton.setText("Stop");
            } else if (simulation.isRunning()) {
                simulation.pauseSimulation();
                startStopButton.setText("Start");
            } else {
                simulation.resumeSimulation();
                startStopButton.setText("Stop");
            }
        } catch (NumberFormatException e) {
            System.out.println("Number format exception");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error");
        }
    }
}
