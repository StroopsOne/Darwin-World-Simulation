package agh.ics.oop.presenter;

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
    private CheckBox owlBearCheckBox; // Checkbox "Sowoniedźwiedź"
    @FXML
    private CheckBox slightCorrectionCheckBox; // Checkbox "Slight Correction"
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

    private static final String CONFIG_FILE = "configurations.json"; // Plik konfiguracji

    private void validateTextField(TextField textField, BiPredicate<String, Integer> validationFunction, int minVal) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                return;
            }

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
            saveConfigButton.setOnAction(event -> saveConfiguration());
            loadConfigButton.setOnAction(event -> loadConfiguration());
        } catch (Exception ignored) {
        }
    }

    private int parseTextFieldToInt(TextField textField) {
        return Integer.parseInt(textField.getText());
    }

    @FXML
    public void onStartClicked() {
        try {
            boolean generateCsvValue = generateCsvCheckBox.isSelected();
            boolean isOwlBearEnabled = owlBearCheckBox.isSelected();
            boolean isSlightCorrectionEnabled = slightCorrectionCheckBox.isSelected();

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

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulation.fxml"));
            Parent root = loader.load();

            SimulationPresenter simulationPresenter = loader.getController();
            simulationPresenter.setGenerateCsv(generateCsvValue);
            simulationPresenter.setNumberOfAnimals(animalCount);
            simulationPresenter.setInitialEnergy(startEnergy);
            simulationPresenter.setGenomeLength(genomeLength);
            simulationPresenter.setGrassValue(grassValue);
            simulationPresenter.setDailyGrass(dailyGrass);
            simulationPresenter.setInitialGrass(grassCount);

            if (isOwlBearEnabled) {
                TheEarthWithOwlBear worldMap = new TheEarthWithOwlBear(
                        mapHeight, mapWidth, minGeneMutation, maxGeneMutation,
                        reproductionEnergy, parentingEnergy, isSlightCorrectionEnabled
                );
                simulationPresenter.setWorldMap(worldMap);
                worldMap.addObserver(simulationPresenter);
            } else {
                TheEarth worldMap = new TheEarth(
                        mapHeight, mapWidth, minGeneMutation, maxGeneMutation,
                        reproductionEnergy, parentingEnergy, isSlightCorrectionEnabled
                );
                simulationPresenter.setWorldMap(worldMap);
                worldMap.addObserver(simulationPresenter);
            }

            simulationPresenter.onStartStopButtonClicked();

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
