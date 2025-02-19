package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.Statistics.AnimalStatistics;
import agh.ics.oop.Statistics.SimulationCharts;
import agh.ics.oop.Statistics.SimulationStatistics;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.mapElements.Animal;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class SimulationPresenter implements MapChangeListener {
    private AbstractWorldMap map;
    private Simulation simulation;
    private AnimalStatistics selectedAnimalStats;
    private PrintWriter csvWriter;
    private boolean generateCsv;
    private boolean showAnimalStats = false;
    private int startAnimalCount, startEnergy, genomeSize, grassNutrient, dailyGrassSpawn, initialGrass, currentDay = 0;
    private SimulationCharts simulationCharts;
    private static final Color CELL_COLOR = Color.rgb(42, 211, 38);
    private Image grassImage, animalImage, doubleAnimalImage, multiAnimalImage, owlBearImage;

    // Nazwy pól muszą odpowiadać identyfikatorom w simulation.fxml
    @FXML private TextField mostCommonGenotypesField, livingAnimalsField, totalPlantsField, freeFieldsField,
            averageEnergyField, averageLifeSpanField, averageChildrenCountField;
    @FXML private TextField genomeField, activePartField, energyField, eatenPlantsField, childrenCountField, ageField, deathDayField;
    @FXML private Label currentDayField;
    @FXML private Button startStopButton;
    @FXML private GridPane mapGrid;
    @FXML private Label owlBearKillsLabel;
    @FXML private VBox chartContainer;




    @FXML
    public void initialize() {
        startStopButton.setText("Start");

        // Wczytanie obrazków
        try {
            grassImage = new Image(Objects.requireNonNull(getClass().getResource("/images/grass-icon.png")).toExternalForm());
            animalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/deer_icon.png")).toExternalForm());
            doubleAnimalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/two_deer_icon.png")).toExternalForm());
            multiAnimalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/deers_icon.png")).toExternalForm());
            owlBearImage = new Image(Objects.requireNonNull(getClass().getResource("/images/OwlBear-icon.png")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Błąd: Nie znaleziono obrazków.");
        }
        // Inicjalizacja wykresu
        simulationCharts = new SimulationCharts();
        chartContainer.getChildren().add(simulationCharts.getChart());

    }

    public void configureMap(AbstractWorldMap worldMap) {
        this.map = worldMap;
        if (worldMap instanceof TheEarthWithOwlBear) {
            owlBearKillsLabel.setVisible(true);
        }
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
        mapGrid.getChildren().clear();
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    @FXML
    public void renderMap() {
        clearGrid();
        Boundary bounds = map.getCurrentBounds();
        int gridSize = 800 / Math.max(bounds.upperRight().getX() - bounds.lowerLeft().getX() + 1,
                bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1);

        for (int y = bounds.lowerLeft().getY(); y <= bounds.upperRight().getY(); y++) {
            for (int x = bounds.lowerLeft().getX(); x <= bounds.upperRight().getX(); x++) {
                Vector2d pos = new Vector2d(x, y);
                Node cell = createVisualElement(pos, gridSize);
                mapGrid.add(cell, x - bounds.lowerLeft().getX(), bounds.upperRight().getY() - y);
            }
        }
    }

    private Node createVisualElement(Vector2d pos, int size) {
        StackPane container = new StackPane();
        container.setMinSize(size, size);
        container.setMaxSize(size, size);

        Rectangle cell = new Rectangle(size, size);
        cell.setFill(CELL_COLOR);
        container.getChildren().add(cell);

        if (map instanceof TheEarthWithOwlBear && ((TheEarthWithOwlBear) map).isOwlBearAtPosition(pos)) {
            container.getChildren().add(createOwlBearElement(size));
        } else if (map.isAnimalAtPosition(pos)) {
            container.getChildren().add(createAnimalElement(pos, size));
        } else if (map.isGrassOnPosition(pos)) {
            container.getChildren().add(createGrassElement(size));
        }

        return container;
    }

    private Node createOwlBearElement(int size) {
        ImageView owlBearView = new ImageView(owlBearImage);
        owlBearView.setFitWidth(size * 0.9);
        owlBearView.setFitHeight(size * 0.9);
        return owlBearView;
    }

    public Paint setEnergyBarColor(int energy) {
        if (energy == 0) return javafx.scene.paint.Color.rgb(255, 0, 0);
        if (energy < 0.25 * startEnergy) return javafx.scene.paint.Color.rgb(209, 113, 21);
        if (energy < 0.5 * startEnergy) return javafx.scene.paint.Color.rgb(209, 183, 34);
        if (energy < 0.75 * startEnergy) return javafx.scene.paint.Color.rgb(244, 237, 62);
        if (energy < startEnergy) return javafx.scene.paint.Color.rgb(131, 180, 31);
        return javafx.scene.paint.Color.rgb(18, 124, 0);
    }

    private Node createAnimalElement(Vector2d pos, int size) {
        StackPane animalContainer = new StackPane();

        List<Animal> animals = map.getAnimalsAtPos(pos);
        ImageView animalView = new ImageView(
                animals.size() == 1 ? animalImage :
                        animals.size() == 2 ? doubleAnimalImage :
                                multiAnimalImage
        );
        animalView.setFitWidth(size * 0.8);
        animalView.setFitHeight(size * 0.8);

        // Pasek energii
        Animal firstAnimal = animals.getFirst();
        Rectangle energyBar = new Rectangle(size * 0.8, size * 0.1);
        energyBar.setFill((Color) setEnergyBarColor(firstAnimal.getEnergy()));
        StackPane.setAlignment(energyBar, javafx.geometry.Pos.BOTTOM_CENTER);

        animalContainer.getChildren().addAll(animalView, energyBar);
        animalContainer.setOnMouseClicked(event -> toggleAnimalStats(firstAnimal));

        return animalContainer;
    }

    private Node createGrassElement(int size) {
        ImageView grassView = new ImageView(grassImage);
        grassView.setFitWidth(size * 0.75);
        grassView.setFitHeight(size * 0.75);
        return grassView;
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
            simulationCharts.updateChart(currentDay, map);
            if (map instanceof TheEarthWithOwlBear theEarthWithOwlBear) {
                owlBearKillsLabel.setText("OwlBear Kills: " + theEarthWithOwlBear.getOwlBearKillsCounter());
            }
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
        freeFieldsField.setText(String.valueOf(stats.getFreeFieldsCount()));
        livingAnimalsField.setText(String.valueOf(stats.getAnimalNumber()));
        totalPlantsField.setText(String.valueOf(stats.getPlantsNumber()));
        averageEnergyField.setText(String.format("%.2f", stats.getAverageEnergy()));
        averageLifeSpanField.setText(String.format("%.2f", stats.getAverageLifeSpan()));
        averageChildrenCountField.setText(String.format("%.2f", stats.getAverageChildrenCount()));
        mostCommonGenotypesField.setText(stats.getMostCommonGenotypes().toString());
    }

    private void updateDayCounter() {
        currentDayField.setText(String.valueOf(simulation.getDay()));
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
