package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.Statistics.AnimalStatistics;
import agh.ics.oop.Statistics.SimulationStatistics;
import agh.ics.oop.model.*;
import agh.ics.oop.model.mapElements.Animal;
import agh.ics.oop.model.mapElements.WorldElement;
import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import agh.ics.oop.model.maps.WorldMap;
import agh.ics.oop.model.util.MapChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Math.max;

public class SimulationPresenter implements MapChangeListener {

    private int grassCount;
    private int dailyGrass;
    private int grassValue;
    private int genomeLength;
    private int startEnergy;
    private int animalsCount;
    private AbstractWorldMap worldMap;
    private boolean isAnimalStatisticsDisplayed = false;
    private static final Color GRASS_COLOR = javafx.scene.paint.Color.GREEN;
    private static final Color EMPTY_CELL_COLOR = javafx.scene.paint.Color.rgb(69, 38, 38);
    private static final Color OWLBEAR_COLOR = javafx.scene.paint.Color.BLACK;
    private Simulation simulation;
    private AnimalStatistics animalStatistics;
    private PrintWriter csvWriter;
    private boolean generateCsv;



    ///                                             FXML fields                                              ///
    @FXML
    private TextField mostCommonGenotypesField;
    @FXML
    private TextField totalAnimalsField;

    @FXML
    private TextField totalPlantsField;

    @FXML
    private TextField freeFieldsField;

    @FXML
    private TextField averageEnergyField;

    @FXML
    private TextField averageLifeSpanField;

    @FXML
    private TextField averageChildrenCountField;

    @FXML
    private TextField genomeField;

    @FXML
    private TextField activePartField;

    @FXML
    private TextField energyField;

    @FXML
    private TextField eatenPlantsField;

    @FXML
    private TextField childrenCountField;


    @FXML
    private TextField ageField;

    @FXML
    private TextField deathDayField;

    @FXML
    private Label currentDayField;

    @FXML
    private Button startStopButton;
    @FXML
    private GridPane mapGrid;

    public void setGenerateCsv(boolean generateCsv) {
        this.generateCsv = generateCsv;
    }

    public void setAnimalsCount(int animalsCount) {
        this.animalsCount = animalsCount;
    }

    public void setStartEnergy(int startEnergy) {
        this.startEnergy = startEnergy;
    }

    public void setGenomeLength(int genomeLength) {
        this.genomeLength = genomeLength;
    }

    public void setGrassValue(int grassValue) {
        this.grassValue = grassValue;
    }

    public void setDailyGrass(int dailyGrass) {
        this.dailyGrass = dailyGrass;
    }

    public void setGrassCount(int grassCount) {
        this.grassCount = grassCount;
    }

    public void setWorldMap(AbstractWorldMap worldMap) {
        this.worldMap = worldMap;
    }




    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst()); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }



    @FXML
    public void drawMap() {
        clearGrid();
        Boundary boundary = worldMap.getCurrentBounds();
        int gridSize = calculateGridSize(boundary);
        drawGrid(boundary, gridSize);
    }

    private int calculateGridSize(Boundary boundary) {
        int mapWidth = boundary.upperRight().getX() - boundary.lowerLeft().getX() + 1;
        int mapHeight = boundary.upperRight().getY() - boundary.lowerLeft().getY() + 1;

        int maxGridSize = Math.max(mapWidth, mapHeight);
        int cellSize = Math.max(1, 800 / maxGridSize); // Zapewnienie, że cellSize >= 1

        return cellSize;
    }


    private void drawGrid(Boundary boundary, int cellSize) {
        for (int i = boundary.lowerLeft().getY(); i <= boundary.upperRight().getY(); i++) {
            for (int j = boundary.lowerLeft().getX(); j <= boundary.upperRight().getX(); j++) {
                Vector2d position = new Vector2d(j, i);
                drawGridCell(position, j - boundary.lowerLeft().getX() + 1, boundary.upperRight().getY() - i + 1, cellSize);
            }
        }
    }

    private void drawGridCell(Vector2d position, int column, int row, int cellSize) {
        WorldElement element = worldMap.objectAt(position);
        Node node = createNodeForElement(element, position, cellSize);
        mapGrid.add(node, column, row);
    }

    private Node createNodeForElement(WorldElement element, Vector2d position, int cellSize) {
        StackPane stackPane = createStackPane(cellSize);
        Rectangle cell = createCell(position, cellSize);
        stackPane.getChildren().add(cell);

        if (element instanceof Animal) {
            Circle circle = createAnimalCircle((Animal) element, cellSize);
            circle.setOnMouseClicked(event -> handleAnimalClick((Animal) element));
            stackPane.getChildren().add(circle);
        }

        if (worldMap instanceof TheEarthWithOwlBear && ((TheEarthWithOwlBear) worldMap).isOwlBearAtPosition(position)) {
            Rectangle owlBear = createOwlBearRectangle(cellSize);
            stackPane.getChildren().add(owlBear);
        }

        return stackPane;
    }

    private StackPane createStackPane(int cellSize) {
        StackPane stackPane = new StackPane();
        stackPane.setMinSize(cellSize, cellSize);
        stackPane.setMaxSize(cellSize, cellSize);
        return stackPane;
    }

    private Rectangle createCell(Vector2d position, int cellSize) {
        Rectangle cell = new Rectangle(cellSize, cellSize);
        if (worldMap.isGrassOnPosition(position)) {
            cell.setFill(GRASS_COLOR);
        } else {
            cell.setFill(EMPTY_CELL_COLOR);
        }
        return cell;
    }

    public void mapChanged(WorldMap worldMap) {
        Platform.runLater(() -> {
            drawMap();
            updateStatistics(worldMap);
            updateAnimalStatistics();
        });
    }

    private Circle createAnimalCircle(Animal animal, int cellSize) {
        Circle circle = new Circle(cellSize / 5);
        circle.setFill(animal.toColor(startEnergy));
        return circle;
    }

    private Rectangle createOwlBearRectangle(int cellSize) {
        Rectangle tunnel = new Rectangle(cellSize / 5, cellSize / 5);
        tunnel.setFill(OWLBEAR_COLOR);
        return tunnel;
    }

    private void updateStatistics(WorldMap map) {
        SimulationStatistics stats = new SimulationStatistics(simulation, (AbstractWorldMap) map);

        totalAnimalsField.setText(String.valueOf(stats.getTotalAnimals()));
        totalPlantsField.setText(String.valueOf(stats.getTotalPlants()));
        freeFieldsField.setText(String.valueOf(stats.getFreeFields()));
        averageEnergyField.setText(String.format("%.2f", stats.getAverageEnergy()));
        averageLifeSpanField.setText(String.format("%.2f", stats.getAverageLifeSpan()));
        averageChildrenCountField.setText(String.format("%.2f", stats.getAverageChildrenCount()));

        mostCommonGenotypesField.setText(stats.getMostCommonGenotypes().toString());

        if(generateCsv) {
            csvWriter.printf("%d,%d,%d,%d,\"%s\",%.2f,%.2f,%.2f%n",
                    simulation.getDay(),
                    stats.getTotalAnimals(),
                    stats.getTotalPlants(),
                    stats.getFreeFields(),
                    stats.getMostCommonGenotypes().toString(),
                    stats.getAverageEnergy(),
                    stats.getAverageLifeSpan(),
                    stats.getAverageChildrenCount()
            );
            csvWriter.flush();
        }
    }

    private void handleAnimalClick(Animal animal) {
        if (!isAnimalStatisticsDisplayed) {
            animalStatistics = new AnimalStatistics(animal, simulation);

            genomeField.setText(animalStatistics.getGenome().toString());
            activePartField.setText(String.valueOf(animalStatistics.getActivePart()));
            energyField.setText(String.valueOf(animalStatistics.getEnergy()));
            eatenPlantsField.setText(String.valueOf(animalStatistics.getEatenPlants()));
            childrenCountField.setText(String.valueOf(animalStatistics.getChildrenCount()));
            ageField.setText(String.valueOf(animalStatistics.getAge()));
            deathDayField.setText(String.valueOf(animalStatistics.getDeathDay()))   ;

        } else {
            updateStatistics(worldMap);
        }

        isAnimalStatisticsDisplayed = !isAnimalStatisticsDisplayed;
    }

    private void updateAnimalStatistics() {
        if (animalStatistics != null) {
            genomeField.setText(animalStatistics.getGenome().toString());
            activePartField.setText(String.valueOf(animalStatistics.getActivePart()));
            energyField.setText(String.valueOf(animalStatistics.getEnergy()));
            eatenPlantsField.setText(String.valueOf(animalStatistics.getEatenPlants()));
            childrenCountField.setText(String.valueOf(animalStatistics.getChildrenCount()));
            //offspringCountField.setText(String.valueOf(animalStatistics.getOffspringCount()));
            ageField.setText(String.valueOf(animalStatistics.getAge()));
            deathDayField.setText(String.valueOf(animalStatistics.getDeathDay()));
        }
    }

    @FXML
    public void initialize() {
        if (mapGrid == null) {
            System.out.println("mapGrid is null!");
        } else {
            System.out.println("mapGrid is initialized.");
        }
        // Ustawienie tekstu na przycisku
        startStopButton.setText("Start");

        // Ustawienie domyślnych wartości dla pól tekstowych
        mostCommonGenotypesField.setText("");
        totalAnimalsField.setText("0");
        totalPlantsField.setText("0");
        freeFieldsField.setText("0");
        averageEnergyField.setText("0.00");
        averageLifeSpanField.setText("0.00");
        averageChildrenCountField.setText("0.00");
        genomeField.setText("");
        activePartField.setText("0");
        energyField.setText("0");
        eatenPlantsField.setText("0");
        childrenCountField.setText("0");
        ageField.setText("0");
        deathDayField.setText("");

        // Aktualizacja widoku mapy, jeśli już istnieje
        if (worldMap != null) {
            drawMap();
        }

        // Inicjalizacja innych zmiennych lub struktur danych
        isAnimalStatisticsDisplayed = false;

        // Dodatkowe logi dla debugowania
        System.out.println("SimulationPresenter initialized.");
    }


    @FXML
    public void onStartStopButtonClicked() {
        System.out.println("Symulacje kliknieto");
        try {
            if (simulation == null) {
                if (generateCsv) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                    LocalDateTime now = LocalDateTime.now();
                    String filename = "simulation_data_" + dtf.format(now) + ".csv";
                    csvWriter = new PrintWriter(new FileWriter(filename, true));
                    csvWriter.println("Day,Total Animals,Total Plants,Free Fields,Most Common Genotypes,Average Energy,Average Life Span,Average Children Count");
                }
                simulation = new Simulation(worldMap, animalsCount, startEnergy, genomeLength, grassValue, grassCount, dailyGrass);

                // Uruchamianie symulacji w osobnym wątku
                Thread simulationThread = new Thread(simulation);
                simulationThread.setDaemon(true); // Ustawienie wątku jako daemona
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
            System.out.println("Inny blad");
        }
    }



}
