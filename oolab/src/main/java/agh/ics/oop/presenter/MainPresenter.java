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

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

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

    private static final String CONFIG_FILE = "/configurations.json";
    private static final String CONFIG_FILE_PATH = getConfigPath();

    private static String getConfigPath() {
        String devPath = "src/main/resources/configurations.json"; // Działa podczas developmentu
        String prodPath = System.getProperty("user.home") + "/configurations.json"; // Działa po zapakowaniu do JAR
        return new File(devPath).exists() ? devPath : prodPath;
    }



    @FXML
    public void initialize() {
        setupValidation();
        setupBindings();
        setupConfigButtons();
    }

    private void setupValidation() {
        validateTextField(widthField, 1, 100);
        validateTextField(heightField, 1, 100);
        validateTextField(grassCountField, 0, 1000);
        validateTextField(dailyGrassField, 0, 100);
        validateTextField(grassValueField, 0, 1000);
        validateTextField(animalCountField, 0, 1000);
        validateTextField(startEnergyField, 0, 1000);
        validateTextField(genomeLengthField, 1, 50);
        validateTextField(parentingEnergyField, 0, 1000);
        validateTextField(reproductionEnergyField, 0, 1000);
        validateTextField(minGeneMutationField, 0, 15);
        validateTextField(maxGeneMutationField, 0, 15);
    }

    private void validateTextField(TextField field, int minVal, int maxVal) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                try {
                    int value = Integer.parseInt(newVal);
                    if (value < minVal || value > maxVal) {
                        field.setText(oldVal);
                    }
                } catch (NumberFormatException e) {
                    field.setText(oldVal);
                }
            }
        });
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




    private int parse(TextField field) {
        return Integer.parseInt(field.getText());
    }


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

            simPresenter.setInitialParams(animalCount, startEnergy, genomeLength, grassValue, grassCount, dailyGrass);

            AbstractWorldMap worldMap = owlBearEnabled
                    ? new TheEarthWithOwlBear(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, slightCorrection)
                    : new TheEarth(mapHeight, mapWidth, minGeneMutation, maxGeneMutation, reproductionEnergy, parentingEnergy, slightCorrection);
            simPresenter.configureMap(worldMap);
            worldMap.addObserver(simPresenter);

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

        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            configFile.createNewFile();
            allConfigs = new HashMap<>();
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                allConfigs = gson.fromJson(reader, type);
                if (allConfigs == null) {
                    allConfigs = new HashMap<>();
                }
            }
        }
        allConfigs.put(configName, config);

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(allConfigs, writer);
        }
    }


    private Map<String, String> loadFromJson(String configName) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

        try (InputStream inputStream = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                System.out.println("Configuration file not found in resources.");
                return null;
            }
            Reader reader = new InputStreamReader(inputStream);
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