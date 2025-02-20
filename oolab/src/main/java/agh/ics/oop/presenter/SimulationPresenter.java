package agh.ics.oop.presenter;

import agh.ics.oop.*;
import agh.ics.oop.Statistics.AnimalStatistics;
import agh.ics.oop.Statistics.SimulationCharts;
import agh.ics.oop.Statistics.SimulationStatistics;
import agh.ics.oop.model.Boundary;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import agh.ics.oop.model.maps.WorldMap;
import agh.ics.oop.model.util.MapChangeListener;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimulationPresenter implements MapChangeListener {
    private AbstractWorldMap map;
    private Simulation simulation;
    private PrintWriter csvWriter;
    private boolean generateCsv;
    private boolean showAnimalStats = false;
    private AnimalStatistics selectedAnimalStats;
    private int startAnimalCount, startEnergy, genomeSize, grassNutrient, dailyGrassSpawn, initialGrass, currentDay = 0;
    private SimulationCharts simulationCharts;
    private static final Color CELL_COLOR = Color.rgb(42, 211, 38);
    private Image grassImage, animalImage, doubleAnimalImage, multiAnimalImage, owlBearImage;
    private List<AnimalEnergyChart> animalEnergyCharts = new ArrayList<>();

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
        loadImages();
        simulationCharts = new SimulationCharts();
        chartContainer.getChildren().add(simulationCharts.getChart());
    }

    public void configureMap(AbstractWorldMap worldMap) {
        this.map = worldMap;
        owlBearKillsLabel.setVisible(worldMap instanceof TheEarthWithOwlBear);
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

    /// Obsługa symulacji ///
    @FXML
    public void onStartStopButtonClicked() {
        System.out.println("Simulation button clicked");
        try {
            if (simulation == null) {
                startSimulation();
            } else if (simulation.isRunning()) {
                pauseSimulation();
            } else {
                resumeSimulation();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error");
        }
    }

    private void startSimulation() throws Exception {
        if (generateCsv) initializeCsvWriter();
        simulation = new Simulation(map, startAnimalCount, startEnergy, genomeSize, grassNutrient, initialGrass, dailyGrassSpawn);
        Thread simulationThread = new Thread(simulation);
        simulationThread.setDaemon(true);
        simulationThread.start();
        startStopButton.setText("Stop");
    }

    private void pauseSimulation() {
        simulation.pauseSimulation();
        startStopButton.setText("Start");
    }

    private void resumeSimulation() {
        simulation.resumeSimulation();
        startStopButton.setText("Stop");
    }

    private void initializeCsvWriter() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        String filename = "simulation_data_" + dtf.format(now) + ".csv";
        csvWriter = new PrintWriter(new FileWriter(filename, true));
        csvWriter.println("Day,Total Animals,Total Plants,Free Fields,Most Common Genotypes,Average Energy,Average Life Span,Average Children Count");
    }

    /// renderowanie mapy ///

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
                Node cell = createMapElement(pos, gridSize);
                if (cell != null) {
                    mapGrid.add(cell, x - bounds.lowerLeft().getX(), y - bounds.lowerLeft().getY());
                }
            }
        }
    }

    private Node createMapElement(Vector2d pos, int size) {
        StackPane cellContainer = new StackPane();
        cellContainer.setPrefSize(size, size);

        Rectangle background = new Rectangle(size, size);
        background.setFill(CELL_COLOR);
        cellContainer.getChildren().add(background);

        if (map.isGrassOnPosition(pos)) {
            cellContainer.getChildren().add(createGrassElement(size));
        }

        if (map instanceof TheEarthWithOwlBear theEarthWithOwlBear && theEarthWithOwlBear.isOwlBearAtPosition(pos)) {
            cellContainer.getChildren().add(createOwlBearElement(size));
        }

        List<Animal> animals = map.getAnimalsAtPos(pos);
        if (animals != null && !animals.isEmpty()) {
            cellContainer.getChildren().add(createAnimalElement(pos, size));
        }



        return cellContainer;
    }

    private Node createOwlBearElement(int size) {
        ImageView owlBearView = new ImageView(owlBearImage);
        owlBearView.setFitWidth(size * 0.9);
        owlBearView.setFitHeight(size * 0.9);
        return owlBearView;
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
        Animal firstAnimal = animals.get(0);
        Rectangle energyBar = new Rectangle(size * 0.8, size * 0.1);
        energyBar.setFill((Color) setEnergyBarColor(firstAnimal.getEnergy()));
        StackPane.setAlignment(energyBar, javafx.geometry.Pos.BOTTOM_CENTER);

        animalContainer.getChildren().addAll(animalView, energyBar);
        animalContainer.setOnMouseClicked(event -> showAnimalStats(firstAnimal));

        return animalContainer;
    }

    private Node createGrassElement(int size) {
        ImageView grassView = new ImageView(grassImage);
        grassView.setFitWidth(size * 0.75);
        grassView.setFitHeight(size * 0.75);
        return grassView;
    }


    /// Obsługa statystyk ///
    @Override
    public void mapChanged(WorldMap updatedMap) {
        Platform.runLater(() -> {
            renderMap();
            updateGlobalStats(updatedMap);
            updateSelectedAnimalStats();
            updateDayCounter();
            simulationCharts.updateChart(currentDay, map);

            for (AnimalEnergyChart chart : animalEnergyCharts) {
                chart.updateChart(currentDay);
            }

            updateOwlBearStats();
            currentDay++;
        });
    }

    private void updateDayCounter() {
        currentDayField.setText(String.valueOf(simulation.getDay()));
    }

    private void updateOwlBearStats() {
        if (map instanceof TheEarthWithOwlBear theEarthWithOwlBear) {
            owlBearKillsLabel.setText("OwlBear Kills: " + theEarthWithOwlBear.getOwlBearKillsCounter());
        }
    }

    private void updateGlobalStats(WorldMap updatedMap) {
        SimulationStatistics simulationStatistics = new SimulationStatistics((AbstractWorldMap) updatedMap);
        freeFieldsField.setText(String.valueOf(simulationStatistics.getFreeFieldsCount()));
        livingAnimalsField.setText(String.valueOf(simulationStatistics.getAnimalNumber()));
        totalPlantsField.setText(String.valueOf(simulationStatistics.getPlantsNumber()));
        averageEnergyField.setText(String.format("%.2f", simulationStatistics.getAverageEnergy()));
        averageLifeSpanField.setText(String.format("%.2f", simulationStatistics.getAverageLifeSpan()));
        averageChildrenCountField.setText(String.format("%.2f", simulationStatistics.getAverageChildrenCount()));
        mostCommonGenotypesField.setText(simulationStatistics.getMostCommonGenotypes().toString());
    }

    private void fillAnimalStats(AnimalStatistics animalStatistics) {
        energyField.setText(String.valueOf(animalStatistics.getEnergy()));
        eatenPlantsField.setText(String.valueOf(animalStatistics.getEatenPlants()));
        ageField.setText(String.valueOf(animalStatistics.getAge()));
        childrenCountField.setText(String.valueOf(animalStatistics.getChildrenCount()));
        genomeField.setText(animalStatistics.getGenome().toString());
        activePartField.setText(String.valueOf(animalStatistics.getActivePart()));
        deathDayField.setText(animalStatistics.getDeathDay() != null ? String.valueOf(animalStatistics.getDeathDay()) : "");
    }

    private void showAnimalStats(Animal animal) {
        if (!showAnimalStats) {
            selectedAnimalStats = new AnimalStatistics(animal);

            // Dodaj wykres energii dla zwierzęcia
            if (!animalEnergyCharts.stream().anyMatch(chart -> chart.getAnimal() == animal)) {
                animalEnergyCharts.add(new AnimalEnergyChart(animal, map, currentDay));
            }
        } else {
            selectedAnimalStats = null;
        }
        showAnimalStats = !showAnimalStats;
    }

    private void updateSelectedAnimalStats() {
        if (selectedAnimalStats != null) {
            fillAnimalStats(selectedAnimalStats);
        }
    }


    /// funkcje pomocnicze ///
    private void loadImages() {
        try {
            grassImage = new Image(Objects.requireNonNull(getClass().getResource("/images/grass-icon.png")).toExternalForm());
            animalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/deer_icon.png")).toExternalForm());
            doubleAnimalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/two_deer_icon.png")).toExternalForm());
            multiAnimalImage = new Image(Objects.requireNonNull(getClass().getResource("/images/deers_icon.png")).toExternalForm());
            owlBearImage = new Image(Objects.requireNonNull(getClass().getResource("/images/OwlBear-icon.png")).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Błąd: Nie znaleziono obrazków.");
        }
    }

    public Paint setEnergyBarColor(int energy) {
        if (energy == 0) return javafx.scene.paint.Color.rgb(255, 0, 0);
        if (energy < 0.25 * startEnergy) return javafx.scene.paint.Color.rgb(209, 113, 21);
        if (energy < 0.5 * startEnergy) return javafx.scene.paint.Color.rgb(209, 183, 34);
        if (energy < 0.75 * startEnergy) return javafx.scene.paint.Color.rgb(244, 237, 62);
        if (energy < startEnergy) return javafx.scene.paint.Color.rgb(131, 180, 31);
        return javafx.scene.paint.Color.rgb(18, 124, 0);
    }
}
