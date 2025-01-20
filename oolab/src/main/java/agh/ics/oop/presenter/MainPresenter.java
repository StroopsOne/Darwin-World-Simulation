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
    private CheckBox mutationVariant;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField grassCountField;
    @FXML
    private ChoiceBox<String> mapVariant;
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
            mapVariant.setItems(FXCollections.observableArrayList("Earth", "Secret Tunnels"));
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
                                    maxGeneMutationField.getText().isEmpty() ||
                                    mapVariant.getValue() == null,
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
                    maxGeneMutationField.textProperty(),
                    mapVariant.valueProperty()
            );

            startButton.disableProperty().bind(areFieldsEmpty);
        } catch (Exception ignored) {
        }

        loadConfigButton.setOnAction(event -> {
            String configId = loadConfigIdField.getText();
            if (!configId.isEmpty()) {
                try {
                    loadConfigurations(configId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        saveConfigButton.setOnAction(event -> {
            String configName = saveConfigNameField.getText();
            if (!configName.isEmpty()) {
                Map<String, String> configs = new HashMap<>();
                configs.put("mapWidth", widthField.getText());
                configs.put("mapHeight", heightField.getText());
                configs.put("startEnergy", startEnergyField.getText());
                configs.put("grassEnergy", grassValueField.getText());
                configs.put("animalCount", animalCountField.getText());
                configs.put("grassCount", grassCountField.getText());
                configs.put("dailyGrass", dailyGrassField.getText());
                configs.put("reproductionEnergy", reproductionEnergyField.getText());
                configs.put("parentingEnergy", parentingEnergyField.getText());
                configs.put("minGeneMutation", minGeneMutationField.getText());
                configs.put("maxGeneMutation", maxGeneMutationField.getText());
                configs.put("genomeLength", genomeLengthField.getText());
                configs.put("mapVariant", mapVariant.getValue());
                configs.put("mutationVariant", mutationVariant.isSelected() ? "true" : "false");
                configs.put("generateCsv", String.valueOf(generateCsvCheckBox.isSelected()));

                try {
                    saveConfigurations(configName, configs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveConfigurations(String configName, Map<String, String> configs) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> allConfigs;

        try (FileReader reader = new FileReader("configurations.json")) {
            allConfigs = gson.fromJson(reader, type);
            if (allConfigs == null) {
                allConfigs = new HashMap<>();
            }
        }

        allConfigs.put(configName, configs);

        try (FileWriter writer = new FileWriter("configurations.json")) {
            gson.toJson(allConfigs, writer);
        }
    }

    private void loadConfigurations(String configName) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

        try (FileReader reader = new FileReader("configurations.json")) {
            if (reader.ready()) {
                Map<String, Map<String, String>> allConfigs = gson.fromJson(reader, type);

                if (allConfigs != null) {
                    Map<String, String> configs = allConfigs.get(configName);

                    if (configs != null) {
                        widthField.setText(configs.get("mapWidth"));
                        heightField.setText(configs.get("mapHeight"));
                        startEnergyField.setText(configs.get("startEnergy"));
                        grassValueField.setText(configs.get("plantEnergy"));
                        animalCountField.setText(configs.get("animalCount"));
                        grassCountField.setText(configs.get("grassCount"));
                        dailyGrassField.setText(configs.get("plantSpawnRate"));
                        reproductionEnergyField.setText(configs.get("reproductionEnergy"));
                        parentingEnergyField.setText(configs.get("parentEnergy"));
                        minGeneMutationField.setText(configs.get("minMutation"));
                        maxGeneMutationField.setText(configs.get("maxMutation"));
                        genomeLengthField.setText(configs.get("genomeLength"));
                        mapVariant.getSelectionModel().select(configs.get("mapVariant"));
                        mutationVariant.setSelected(Boolean.parseBoolean(configs.get("mutationVariant")));
                        generateCsvCheckBox.setSelected(Boolean.parseBoolean(configs.get("generateCsv")));
                    }
                }
            }
        }
    }

    private int parseTextFieldToInt(TextField textField) {
        return Integer.parseInt(textField.getText());
    }

    @FXML
    public void onStartClicked() {
        System.out.println("Symulacje kliknieto");
        try {
            // Walidacja pól tekstowych
            if (mapVariant.getValue() == null) {
                infoLabel.setText("Error: Map variant must be selected.");
                return;
            }

            // Pobieranie wartości z pól
            boolean generateCsvValue = generateCsvCheckBox.isSelected();
            String mapVariantValue = mapVariant.getValue();
            boolean mutationVariantValue = mutationVariant.isSelected();
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

            // Tworzenie mapy
            if (mapVariantValue.equals("Earth")) {
                TheEarth worldMap = new TheEarth(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, mutationVariantValue);
                simulationPresenter.setWorldMap(worldMap);
                worldMap.addObserver(simulationPresenter);
            } else {
                TheEarthWithOwlBear worldMap = new TheEarthWithOwlBear(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, mutationVariantValue);
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