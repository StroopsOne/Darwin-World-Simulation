package agh.ics.oop.presenter;

import agh.ics.oop.model.maps.AbstractWorldMap;
import agh.ics.oop.model.maps.TheEarth;
import agh.ics.oop.model.maps.TheEarthWithOwlBear;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPresenter {

    @FXML private Label infoLabel;
    @FXML private TextField widthField;
    @FXML private TextField heightField;
    @FXML private TextField grassCountField;
    @FXML private TextField dailyGrassField;
    @FXML private TextField grassValueField;
    @FXML private TextField animalCountField;
    @FXML private TextField startEnergyField;
    @FXML private TextField parentingEnergyField;
    @FXML private TextField reproductionEnergyField;
    @FXML private TextField genomeLengthField;
    @FXML private TextField minGeneMutationField;
    @FXML private TextField maxGeneMutationField;
    @FXML private CheckBox owlBearCheckBox;
    @FXML private CheckBox slightCorrectionCheckBox;
    @FXML private CheckBox generateCsvCheckBox;
    @FXML private Button loadConfigButton;
    @FXML private Button saveConfigButton;
    @FXML private TextField loadConfigIdField;
    @FXML private TextField saveConfigNameField;

    private static final String CONFIG_FILE = "/configurations.json";
    private static final String CONFIG_FILE_PATH = getConfigPath();

    private static String getConfigPath() {
        String devPath = "src/main/resources/configurations.json";
        String prodPath = System.getProperty("user.home") + "/configurations.json";
        return new File(devPath).exists() ? devPath : prodPath;
    }

    @FXML
    public void initialize() {
        setupValidation();
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
        validateTextField(parentingEnergyField, 0, 1000);
        validateTextField(reproductionEnergyField, 0, 1000);
        validateTextField(genomeLengthField, 1, 50);
        validateTextField(minGeneMutationField, 0, 15);
        validateTextField(maxGeneMutationField, 0, 15);
    }


    private void setupConfigButtons() {
        saveConfigButton.setOnAction(e -> saveConfiguration());
        loadConfigButton.setOnAction(e -> loadConfiguration());
    }


    @FXML
    public void onStartClicked() {


        List<TextField> fields = List.of(
                widthField, heightField, grassCountField, dailyGrassField, grassValueField,
                animalCountField, startEnergyField, parentingEnergyField, reproductionEnergyField,
                genomeLengthField, minGeneMutationField, maxGeneMutationField
        );

        // Sprawdzanie pustych pól
        for (TextField field : fields) {
            if (field.getText().isEmpty()) {
                infoLabel.setText("Error: All fields must be filled.");
                return;
            }
        }

        try {
            // Pobieranie wartości
            int mapWidth = Integer.parseInt(widthField.getText());
            int mapHeight = Integer.parseInt(heightField.getText());
            int grassCount = Integer.parseInt(grassCountField.getText());
            int dailyGrass = Integer.parseInt(dailyGrassField.getText());
            int grassValue = Integer.parseInt(grassValueField.getText());
            int animalCount = Integer.parseInt(animalCountField.getText());
            int startEnergy = Integer.parseInt(startEnergyField.getText());
            int parentingEnergy = Integer.parseInt(parentingEnergyField.getText());
            int reproductionEnergy = Integer.parseInt(reproductionEnergyField.getText());
            int genomeLength = Integer.parseInt(genomeLengthField.getText());
            int minGeneMutation = Integer.parseInt(minGeneMutationField.getText());
            int maxGeneMutation = Integer.parseInt(maxGeneMutationField.getText());
            boolean csvEnabled = generateCsvCheckBox.isSelected();
            boolean owlBearEnabled = owlBearCheckBox.isSelected();
            boolean slightCorrection = slightCorrectionCheckBox.isSelected();

            if (minGeneMutation > maxGeneMutation) {
                infoLabel.setText("Error: Min mutation cannot be greater than max mutation.");
                return;
            }

            // Wczytanie sceny symulacji
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
            infoLabel.setText("Error: Invalid number format.");
        } catch (Exception e) {
            infoLabel.setText("Unexpected error: " + e.getMessage());
            e.printStackTrace();
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
        config.put("dailyGrass", dailyGrassField.getText());
        config.put("grassValue", grassValueField.getText());
        config.put("animalCount", animalCountField.getText());
        config.put("startEnergy", startEnergyField.getText());
        config.put("parentingEnergy", parentingEnergyField.getText());
        config.put("reproductionEnergy", reproductionEnergyField.getText());
        config.put("genomeLength", genomeLengthField.getText());
        config.put("minGeneMutation", minGeneMutationField.getText());
        config.put("maxGeneMutation", maxGeneMutationField.getText());

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
            dailyGrassField.setText(config.get("dailyGrass"));
            grassValueField.setText(config.get("grassValue"));
            animalCountField.setText(config.get("animalCount"));
            startEnergyField.setText(config.get("startEnergy"));
            parentingEnergyField.setText(config.get("parentingEnergy"));
            reproductionEnergyField.setText(config.get("reproductionEnergy"));
            genomeLengthField.setText(config.get("genomeLength"));
            minGeneMutationField.setText(config.get("minGeneMutation"));
            maxGeneMutationField.setText(config.get("maxGeneMutation"));
            owlBearCheckBox.setSelected(Boolean.parseBoolean(config.get("owlBearEnabled")));
            slightCorrectionCheckBox.setSelected(Boolean.parseBoolean(config.get("slightCorrection")));

            infoLabel.setText("Configuration loaded successfully.");
        } catch (IOException e) {
            infoLabel.setText("Error: Could not load configuration.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        List<TextField> textFields = List.of(
                widthField, heightField, grassCountField, grassValueField, dailyGrassField,
                animalCountField, startEnergyField, parentingEnergyField, reproductionEnergyField,
                genomeLengthField, minGeneMutationField, maxGeneMutationField,
                saveConfigNameField, loadConfigIdField
        );

        textFields.forEach(TextField::clear);

        List<CheckBox> checkBoxes = List.of(owlBearCheckBox, generateCsvCheckBox, slightCorrectionCheckBox);
        checkBoxes.forEach(cb -> cb.setSelected(false));
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


}