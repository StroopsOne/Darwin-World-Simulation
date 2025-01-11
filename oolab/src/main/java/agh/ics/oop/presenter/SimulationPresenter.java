package agh.ics.oop.presenter;

import agh.ics.oop.Simulation;
import agh.ics.oop.SimulationEngine;
import agh.ics.oop.model.*;
import agh.ics.oop.model.maps.TheEarth;
import agh.ics.oop.model.maps.WorldMap;
import agh.ics.oop.model.util.MapChangeListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.List;

import static java.lang.Math.max;

public class SimulationPresenter implements MapChangeListener {

    @FXML
    public TextField textField;

    @FXML
    public Button startButton;

    @FXML
    private Label infoLabel;

    @FXML
    private Label movesDescriptionLabel;

    @FXML
    private GridPane mapGrid;


    @FXML
    private void initialize() {
        System.out.println("initialize() called");
        infoLabel.setText("There will be a simulation...");
    }

    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().get(0)); // hack to retain visible grid lines
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            movesDescriptionLabel.setText(message);
            drawMap(worldMap);
        });
    }

    private void drawMap(WorldMap worldMap) {
        clearGrid();

        Boundary bounds = worldMap.getCurrentBounds();
        int rows = bounds.upperRight().getX() - bounds.lowerLeft().getX() + 1;
        int cols = bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1;
        int maxBonds = max(rows,cols);


        for (int i = 0; i < rows+1; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(50));
        }
        for (int i = 0; i < cols+1; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(50));
        }
        Label firstCell = new Label("y/x");
        mapGrid.add(firstCell, 0, 0);
        GridPane.setHalignment(firstCell, HPos.CENTER);

        int colsCtr = bounds.lowerLeft().getX();
        for(int c = 1; c < cols+1; c++){
            Label colsCell = new Label(String.valueOf(colsCtr));
            mapGrid.add(colsCell, c, 0);
            GridPane.setHalignment(colsCell, HPos.CENTER);
            colsCtr++;
        }

        int rowsCtr = bounds.upperRight().getY();
        for(int r = 1; r < rows+1; r++){
            Label rowsCell = new Label(String.valueOf(rowsCtr));
            mapGrid.add(rowsCell, 0, r);
            GridPane.setHalignment(rowsCell, HPos.CENTER);
            rowsCtr++;
        }

        for (int r = 1; r < rows+1; r++) {
            for (int c = 1; c < cols+1; c++) {
                Vector2d position = new Vector2d(c, r);
                String object = worldMap.objectAt(position) != null ? worldMap.objectAt(position).toString() : "";
                Label cellLabel = new Label(object);
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                mapGrid.add(cellLabel, c, r);
            }
        }
    }

    public void onSimulationStartClicked(ActionEvent actionEvent){
        String input = textField.getText();
        if (input == null || input.isEmpty()) {
            movesDescriptionLabel.setText("Enter list of moves like: 'f f b f r'");
            return;
        }

        String[] moves = input.split(" ");
        try {
            List<MoveDirection> directions = OptionsParser.parse(moves);
            WorldMap worldMap = new TheEarth(10);
            List<Vector2d> initialPositions = List.of(new Vector2d(2, 2), new Vector2d(4, 4));

            try {
                Simulation simulation = new Simulation(initialPositions, directions, worldMap);
                worldMap.addObserver(this);

                SimulationEngine engine = new SimulationEngine(List.of(simulation));
                engine.runAsyncinThreadPool();
                infoLabel.setText(worldMap.toString());
                movesDescriptionLabel.setText("Simulation finished!");
            } catch (IncorrectPositionException e) {
                movesDescriptionLabel.setText(e.getMessage());
            }

        } catch(IllegalArgumentException e) {
            movesDescriptionLabel.setText(e.getMessage() + ". Enter a valid move like: 'f b r l'");
        }
    }
}
