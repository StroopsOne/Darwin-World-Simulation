package agh.ics.oop.presenter;

import agh.ics.oop.model.maps.TheEarth;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class MainPresenter {

    @FXML
    public Label infoLabel;
    @FXML
    public TextField parentingEnergyField;
    @FXML
    public TextField reproductionEnergyField;
    @FXML
    public TextField minGeneMutationField;
    @FXML
    public TextField maxGeneMutationField;
    @FXML
    private CheckBox owlBearCheckBox; // Zmieniony checkbox na "Sowoniedźwiedź"
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField grassCountField;
    @FXML
    private TextField animalCountField;
    @FXML
    private TextField startEnergyField;
    @FXML
    private TextField grassValueField;
    @FXML
    private TextField genomeLengthField;
    @FXML
    private TextField dailyGrassField;
    @FXML
    private Button startButton;
    @FXML
    private CheckBox generateCsvCheckBox;
    @FXML
    private Button loadConfigButton;
    @FXML
    private Button saveConfigButton;
    @FXML
    private TextField loadConfigIdField;
    @FXML
    private TextField saveConfigNameField;

    private void validateTextField(TextField textField, BiPredicate<String, Integer> validationFunction, int minVal) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!validationFunction.test(newValue, minVal)) {
                textField.setText(oldValue);
            }
        });
    }

    private boolean isNonNegativeInteger(String value, int minVal) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (!value.matches("\\d*")) {
            return false;
        }
        return Integer.parseInt(value) >= minVal;
    }

    @FXML
    public void initialize() {
        try {
            validateTextField(startEnergyField, this::isNonNegativeInteger, 0);
            validateTextField(grassValueField, this::isNonNegativeInteger, 0);
            validateTextField(widthField, this::isNonNegativeInteger, 1);
            validateTextField(heightField, this::isNonNegativeInteger, 1);
            validateTextField(grassCountField, this::isNonNegativeInteger, 0);
            validateTextField(animalCountField, this::isNonNegativeInteger, 0);
            validateTextField(genomeLengthField, this::isNonNegativeInteger, 1);
            validateTextField(dailyGrassField, this::isNonNegativeInteger, 0);
            validateTextField(parentingEnergyField, this::isNonNegativeInteger, 0);
            validateTextField(reproductionEnergyField, this::isNonNegativeInteger, 0);
            validateTextField(minGeneMutationField, this::isNonNegativeInteger, 0);
            validateTextField(maxGeneMutationField, this::isNonNegativeInteger, 0);

            BooleanBinding areFieldsEmpty = Bindings.createBooleanBinding(() ->
                            startEnergyField.getText().isEmpty() ||
                                    grassValueField.getText().isEmpty() ||
                                    widthField.getText().isEmpty() ||
                                    heightField.getText().isEmpty() ||
                                    grassCountField.getText().isEmpty() ||
                                    animalCountField.getText().isEmpty() ||
                                    genomeLengthField.getText().isEmpty() ||
                                    dailyGrassField.getText().isEmpty() ||
                                    parentingEnergyField.getText().isEmpty() ||
                                    reproductionEnergyField.getText().isEmpty() ||
                                    minGeneMutationField.getText().isEmpty() ||
                                    maxGeneMutationField.getText().isEmpty(),
                    startEnergyField.textProperty(),
                    grassValueField.textProperty(),
                    widthField.textProperty(),
                    heightField.textProperty(),
                    grassCountField.textProperty(),
                    animalCountField.textProperty(),
                    genomeLengthField.textProperty(),
                    dailyGrassField.textProperty(),
                    parentingEnergyField.textProperty(),
                    reproductionEnergyField.textProperty(),
                    minGeneMutationField.textProperty(),
                    maxGeneMutationField.textProperty()
            );

            startButton.disableProperty().bind(areFieldsEmpty);
        } catch (Exception ignored) {
        }
    }

    private int parseTextFieldToInt(TextField textField) {
        return Integer.parseInt(textField.getText());
    }

    @FXML
    public void onStartClicked() {
        System.out.println("Symulacje kliknieto");
        try {
            // Pobieranie wartości z pól
            boolean generateCsvValue = generateCsvCheckBox.isSelected();
            boolean isOwlBearEnabled = owlBearCheckBox.isSelected(); // Sprawdzenie checkboxa "Sowoniedźwiedź"
            int mapWidth = parseTextFieldToInt(widthField);
            int mapHeight = parseTextFieldToInt(heightField);
            int grassCount = parseTextFieldToInt(grassCountField);
            int grassValue = parseTextFieldToInt(grassValueField);
            int dailyGrass = parseTextFieldToInt(dailyGrassField);
            int animalCount = parseTextFieldToInt(animalCountField);
            int startEnergy = parseTextFieldToInt(startEnergyField);
            int parentingEnergy = parseTextFieldToInt(parentingEnergyField);
            int reproductionEnergy = parseTextFieldToInt(reproductionEnergyField);
            int genomeLength = parseTextFieldToInt(genomeLengthField);
            int minGeneMutation = parseTextFieldToInt(minGeneMutationField);
            int maxGeneMutation = parseTextFieldToInt(maxGeneMutationField);

            // Ładowanie pliku FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            Parent root = loader.load();

            // Pobranie kontrolera i ustawienie wartości
            SimulationPresenter simulationPresenter = loader.getController();
            simulationPresenter.setGenerateCsv(generateCsvValue);
            simulationPresenter.setNumberOfAnimals(animalCount);
            simulationPresenter.setInitialEnergy(startEnergy);
            simulationPresenter.setGenomeLength(genomeLength);
            simulationPresenter.setGrassValue(grassValue);
            simulationPresenter.setDailyGrass(dailyGrass);
            simulationPresenter.setInitialGrass(grassCount);

            // Tworzenie mapy w zależności od stanu checkboxa
            if (isOwlBearEnabled) {
                TheEarthWithOwlBear worldMap = new TheEarthWithOwlBear(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, true);
                simulationPresenter.setWorldMap(worldMap);
                worldMap.addObserver(simulationPresenter);
            } else {
                TheEarth worldMap = new TheEarth(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, false);
                simulationPresenter.setWorldMap(worldMap);
                worldMap.addObserver(simulationPresenter);
            }

            // Rozpoczęcie symulacji
            simulationPresenter.onStartStopButtonClicked();

            // Ustawienie nowego okna
            Stage simulationStage = new Stage();
            simulationStage.setScene(new Scene(root));
            simulationStage.show();
        } catch (NumberFormatException e) {
            infoLabel.setText("Error: All fields must contain valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("An unexpected error occurred.");
        }
    }
}
