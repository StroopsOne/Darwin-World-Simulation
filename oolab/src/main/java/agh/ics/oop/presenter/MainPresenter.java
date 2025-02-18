package agh.ics.oop.presenter;

import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarth;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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

    @FXML private Label infoLabel;
    @FXML private TextField parentingEnergyField;
    @FXML private TextField reproductionEnergyField;
    @FXML private TextField minGeneMutationField;
    @FXML private TextField maxGeneMutationField;
    @FXML private CheckBox owlBearCheckBox;
    @FXML private CheckBox slightCorrectionCheckBox;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField grassCountField;
    @FXML private TextField animalCountField;
    @FXML private TextField startEnergyField;
    @FXML private TextField grassValueField;
    @FXML private TextField genomeLengthField;
    @FXML private TextField dailyGrassField;
    @FXML private Button startButton;
    @FXML private CheckBox generateCsvCheckBox;
    @FXML private Button loadConfigButton;
    @FXML private Button saveConfigButton;
    @FXML private TextField loadConfigIdField;
    @FXML private TextField saveConfigNameField;

    private static final String CONFIG_FILE = "configurations.json";

    @FXML
    public void initialize() {
        setupValidation();
        setupBindings();
        setupConfigButtons();
    }

    private void setupValidation() {
        validateTextField(startEnergyField, this::isNonNegativeInt, 0);
        validateTextField(grassValueField, this::isNonNegativeInt, 0);
        validateTextField(widthField, this::isNonNegativeInt, 1);
        validateTextField(heightField, this::isNonNegativeInt, 1);
        validateTextField(grassCountField, this::isNonNegativeInt, 0);
        validateTextField(animalCountField, this::isNonNegativeInt, 0);
        validateTextField(genomeLengthField, this::isNonNegativeInt, 1);
        validateTextField(dailyGrassField, this::isNonNegativeInt, 0);
        validateTextField(parentingEnergyField, this::isNonNegativeInt, 0);
        validateTextField(reproductionEnergyField, this::isNonNegativeInt, 0);
        validateTextField(minGeneMutationField, this::isNonNegativeInt, 0);
        validateTextField(maxGeneMutationField, this::isNonNegativeInt, 0);
    }

    private void setupBindings() {
        BooleanBinding fieldsEmpty = Bindings.createBooleanBinding(() ->
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
        startButton.disableProperty().bind(fieldsEmpty);
    }

    private void setupConfigButtons() {
        saveConfigButton.setOnAction(e -> saveConfiguration());
        loadConfigButton.setOnAction(e -> loadConfiguration());
    }

    private void validateTextField(TextField field, BiPredicate<String, Integer> validator, int minVal) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !validator.test(newVal, minVal)) {
                field.setText(oldVal);
            }
        });
    }

    private boolean isNonNegativeInt(String value, int min) {
        return value.matches("\\d+") && Integer.parseInt(value) >= min;
    }

    private int parse(TextField field) {
        return Integer.parseInt(field.getText());
    }

    // Metoda wywoływana przez przycisk Start Simulation (onAction="#onStartClicked")
    @FXML
    public void onStartClicked() {
        System.out.println("Symulacja została uruchomiona");
        try {
            boolean csvEnabled = generateCsvCheckBox.isSelected();
            boolean owlBearEnabled = owlBearCheckBox.isSelected();
            boolean slightCorrection = slightCorrectionCheckBox.isSelected();

            int mapWidth = parse(widthField);
            int mapHeight = parse(heightField);
            int grassCount = parse(grassCountField);
            int animalCount = parse(animalCountField);
            int startEnergy = parse(startEnergyField);
            int grassValue = parse(grassValueField);
            int dailyGrass = parse(dailyGrassField);
            int genomeLength = parse(genomeLengthField);
            int parentingEnergy = parse(parentingEnergyField);
            int reproductionEnergy = parse(reproductionEnergyField);
            int minGeneMutation = parse(minGeneMutationField);
            int maxGeneMutation = parse(maxGeneMutationField);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            Parent simRoot = loader.load();
            SimulationPresenter simPresenter = loader.getController();
            simPresenter.enableCsvExport(csvEnabled);
            // Ustawienie wszystkich parametrów symulacji jednorazowo
            simPresenter.setInitialParams(animalCount, startEnergy, genomeLength, grassValue, grassCount, dailyGrass);

            // Wybór mapy w zależności od ustawienia (z OwlBear lub bez)
            AbstractWorldMap worldMap = owlBearEnabled
                    ? new TheEarthWithOwlBear(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, slightCorrection)
                    : new TheEarth(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, slightCorrection);
            simPresenter.configureMap(worldMap);
            worldMap.addObserver(simPresenter);

            // Uruchomienie symulacji (metoda onStartStopButtonClicked w SimulationPresenter)
            simPresenter.onStartStopButtonClicked();

            Stage simStage = new Stage();
            simStage.setScene(new Scene(simRoot));
            simStage.show();
        } catch (NumberFormatException e) {
            infoLabel.setText("Error: All fields must contain valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("An unexpected error occurred.");
        }
    }

    // Metody zapisu/odczytu konfiguracji

    private void saveConfiguration() {
        String configName = saveConfigNameField.getText();
        if (configName.isEmpty()) {
            infoLabel.setText("Error: Configuration name cannot be empty.");
            return;
        }
        Map<String, String> config = new HashMap<>();
        config.put("mapWidth", widthField.getText());
        config.put("mapHeight", heightField.getText());
        config.put("grassCount", grassCountField.getText());
        config.put("animalCount", animalCountField.getText());
        config.put("startEnergy", startEnergyField.getText());
        config.put("grassValue", grassValueField.getText());
        config.put("dailyGrass", dailyGrassField.getText());
        config.put("parentingEnergy", parentingEnergyField.getText());
        config.put("reproductionEnergy", reproductionEnergyField.getText());
        config.put("minGeneMutation", minGeneMutationField.getText());
        config.put("maxGeneMutation", maxGeneMutationField.getText());
        config.put("genomeLength", genomeLengthField.getText());
        config.put("owlBearEnabled", String.valueOf(owlBearCheckBox.isSelected()));
        config.put("slightCorrection", String.valueOf(slightCorrectionCheckBox.isSelected()));

        try {
            saveToJson(configName, config);
            infoLabel.setText("Configuration saved successfully.");
            clearFields();
        } catch (IOException e) {
            infoLabel.setText("Error: Could not save configuration.");
            e.printStackTrace();
        }
    }

    private void loadConfiguration() {
        String configName = loadConfigIdField.getText();
        if (configName.isEmpty()) {
            infoLabel.setText("Error: Configuration ID cannot be empty.");
            return;
        }
        try {
            Map<String, String> config = loadFromJson(configName);
            if (config == null) {
                infoLabel.setText("Error: Configuration not found.");
                return;
            }
            widthField.setText(config.get("mapWidth"));
            heightField.setText(config.get("mapHeight"));
            grassCountField.setText(config.get("grassCount"));
            animalCountField.setText(config.get("animalCount"));
            startEnergyField.setText(config.get("startEnergy"));
            grassValueField.setText(config.get("grassValue"));
            dailyGrassField.setText(config.get("dailyGrass"));
            parentingEnergyField.setText(config.get("parentingEnergy"));
            reproductionEnergyField.setText(config.get("reproductionEnergy"));
            minGeneMutationField.setText(config.get("minGeneMutation"));
            maxGeneMutationField.setText(config.get("maxGeneMutation"));
            genomeLengthField.setText(config.get("genomeLength"));
            owlBearCheckBox.setSelected(Boolean.parseBoolean(config.get("owlBearEnabled")));
            slightCorrectionCheckBox.setSelected(Boolean.parseBoolean(config.get("slightCorrection")));

            infoLabel.setText("Configuration loaded successfully.");
        } catch (IOException e) {
            infoLabel.setText("Error: Could not load configuration.");
            e.printStackTrace();
        }
    }

    private void saveToJson(String configName, Map<String, String> config) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> allConfigs;
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            allConfigs = gson.fromJson(reader, type);
            if (allConfigs == null) {
                allConfigs = new HashMap<>();
            }
        } catch (IOException e) {
            allConfigs = new HashMap<>();
        }
        allConfigs.put(configName, config);
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(allConfigs, writer);
        }
    }

    private Map<String, String> loadFromJson(String configName) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Map<String, Map<String, String>> allConfigs = gson.fromJson(reader, type);
            if (allConfigs != null) {
                return allConfigs.get(configName);
            }
        }
        return null;
    }

    private void clearFields() {
        widthField.clear();
        heightField.clear();
        grassCountField.clear();
        animalCountField.clear();
        startEnergyField.clear();
        grassValueField.clear();
        dailyGrassField.clear();
        parentingEnergyField.clear();
        reproductionEnergyField.clear();
        minGeneMutationField.clear();
        maxGeneMutationField.clear();
        genomeLengthField.clear();
        saveConfigNameField.clear();
        loadConfigIdField.clear();
        owlBearCheckBox.setSelected(false);
        generateCsvCheckBox.setSelected(false);
        slightCorrectionCheckBox.setSelected(false);
    }
}
