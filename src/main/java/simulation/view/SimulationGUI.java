package simulation.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import simulation.controller.Controller;
import simulation.controller.IControllerV;

public class SimulationGUI extends Application implements ISimulationUI {
    private IControllerV controller;
    private TextArea resultsArea;
    ServicePointView[] servicePointViews = new ServicePointView[4];
    Label statusLabel = new Label();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Climate Conference Simulator");

        // Styles
        String buttonStyle = "-fx-background-color: linear-gradient(to right, #007bff, #0056b3); "
                + "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px; "
                + "-fx-border-radius: 12px; -fx-background-radius: 12px; -fx-cursor: hand;";

        String textFieldStyle = "-fx-background-color: #ffffff; -fx-border-color: #cccccc; "
                + "-fx-border-radius: 10px; -fx-padding: 8px; -fx-font-size: 14px;";

        // Initialize the resultsTextArea
        resultsArea = new TextArea();
        resultsArea.setFont(new Font(16));
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setPrefHeight(200);

        // Simulation duration input field
        Label durationLabel = new Label("Simulation Duration:");
        durationLabel.setFont(new Font(16));

        TextField durationField = new TextField();
        durationField.setPromptText("Enter time...");
        durationField.setMaxWidth(200);
        durationField.setStyle(textFieldStyle);

        // Capacity dropdowns
        ComboBox<Integer>[] numberDropdowns = new ComboBox[4];
        Label[] dropdownLabels = new Label[4];
        String[] serviceNames = {
                "Event Entrance",
                "Renewable Energy Stand",
                "Climate Showcase Room",
                "Main Stage"
        };

        for (int i = 0; i < numberDropdowns.length; i++) {
            numberDropdowns[i] = new ComboBox<>();
            for (int j = 1; j <= 10; j++) {
                numberDropdowns[i].getItems().add(j); // Add numbers 1-10
            }
            numberDropdowns[i].setValue(1); // Set default value to 1
            dropdownLabels[i] = new Label(serviceNames[i]);
            dropdownLabels[i].setFont(new Font(16));
        }

        HBox numberDropdownBox1 = new HBox(10);
        numberDropdownBox1.setAlignment(Pos.CENTER);
        numberDropdownBox1.getChildren().addAll(dropdownLabels[0], numberDropdowns[0]);

        HBox numberDropdownBox2 = new HBox(10);
        numberDropdownBox2.setAlignment(Pos.CENTER);
        numberDropdownBox2.getChildren().addAll(dropdownLabels[1], numberDropdowns[1]);

        HBox numberDropdownBox3 = new HBox(10);
        numberDropdownBox3.setAlignment(Pos.CENTER);
        numberDropdownBox3.getChildren().addAll(dropdownLabels[2], numberDropdowns[2]);

        HBox numberDropdownBox4 = new HBox(10);
        numberDropdownBox4.setAlignment(Pos.CENTER);
        numberDropdownBox4.getChildren().addAll(dropdownLabels[3], numberDropdowns[3]);

        VBox numberDropdownBox = new VBox(10);
        numberDropdownBox.setAlignment(Pos.CENTER);
        numberDropdownBox.getChildren().addAll(
                numberDropdownBox1,
                numberDropdownBox2,
                numberDropdownBox3,
                numberDropdownBox4
        );

        // Add listeners to the dropdown boxes to update the capacity dynamically
        for (int i = 0; i < numberDropdowns.length; i++) {
            final int index = i;
            numberDropdowns[i].valueProperty().addListener((obs, oldVal, newVal) -> {
                servicePointViews[index].setUserData(newVal);
            });
        }

        VBox searchBox = new VBox(10, durationLabel, durationField);
        searchBox.setAlignment(Pos.CENTER);

        // Start Simulation button
        Button startButton = new Button("Start Simulation");
        startButton.setFont(new Font(14));
        startButton.setStyle(buttonStyle);

        VBox buttonBox = new VBox(10, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Service Point views (fixed size, no stretching)
        servicePointViews[0] = createStyledServicePoint("Event Entrance", "#42a5f5",numberDropdowns[0].getValue());
        servicePointViews[1] = createStyledServicePoint("Renewable Energy Stand", "#66bb6a",numberDropdowns[1].getValue());
        servicePointViews[2] = createStyledServicePoint("Climate Showcase Room", "#ffa726",numberDropdowns[2].getValue());
        servicePointViews[3] = createStyledServicePoint("Main Stage", "#ab47bc",numberDropdowns[3].getValue());

        // Prevent stretching
        for (ServicePointView spv : servicePointViews) {
            spv.setMaxSize(200, 150); // Max size
            spv.setPrefSize(200, 150); // Fixed size
            spv.setMinSize(200, 150); // Min size, prevents shrinking
        }

        GridPane serviceGrid = new GridPane();
        serviceGrid.setHgap(20);
        serviceGrid.setVgap(20);
        serviceGrid.setPadding(new Insets(20));
        serviceGrid.setAlignment(Pos.CENTER);
        serviceGrid.add(servicePointViews[0], 0, 0);
        serviceGrid.add(servicePointViews[1], 1, 0);
        serviceGrid.add(servicePointViews[2], 0, 1);
        serviceGrid.add(servicePointViews[3], 1, 1);

        // Main layout
        VBox rightLayout = new VBox(30);
        rightLayout.setAlignment(Pos.CENTER);
        rightLayout.setStyle("-fx-padding: 50px; -fx-background-color: #f4f4f4;");
        rightLayout.getChildren().addAll(searchBox, numberDropdownBox, buttonBox, statusLabel, serviceGrid);

        HBox mainLayout = new HBox(30);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-padding: 50px; -fx-background-color: #f4f4f4;");
        mainLayout.getChildren().addAll(resultsArea, rightLayout);

        StackPane root = new StackPane(mainLayout);
        StackPane.setAlignment(mainLayout, Pos.CENTER);

        // Scene and window settings
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();

        controller = new Controller(this, this);


        // Button functionality
        startButton.setOnAction(e -> {
            try {
                double duration = Double.parseDouble(durationField.getText());
                int[] capacities = new int[numberDropdowns.length];
                boolean validCapacities = true;

                for (int i = 0; i < numberDropdowns.length; i++) {
                    capacities[i] = numberDropdowns[i].getValue();

                    // Check that capacities are higher than 1, to avoid 0 results
                    if (capacities[i] <= 1) {
                        validCapacities = false;
                        break;
                    }
                }

                // Inform user if capacities are not higher than 1
                if (!validCapacities) {
                    statusLabel.setText("All capacities must be higher than 1");
                } else {
                    controller.startSimulation(duration, capacities);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid number!");
            }
        });
    }


    // Service Point component styles
    private ServicePointView createStyledServicePoint(String name, String color, int capacity) {
        ServicePointView spv = new ServicePointView(name);
        spv.setStyle("-fx-background-color: " + color + "; " +
                "-fx-padding: 20px; -fx-border-color: white; " +
                "-fx-border-radius: 15px; -fx-background-radius: 15px; " +
                "-fx-font-size: 14px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 3, 3);");

        // Prevent excessive number of customers
        spv.setMaxHeight(150); // Limit height
        spv.setMinHeight(150); // Prevent shrinking
        spv.setMaxWidth(200); // Limit width
        spv.setMinWidth(200);

        // Add max 8 balls per box
        spv.setUserData(capacity);

        return spv;
    }



    // Method to update the status label
    public void updateStatusLabel(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    @Override
    public ServicePointView getEventEntrance() {
        return servicePointViews[0];
    }

    @Override
    public ServicePointView getRenewable() {
        return servicePointViews[1];
    }

    @Override
    public ServicePointView getShowRoom() {
        return servicePointViews[2];
    }

    @Override
    public ServicePointView getMainStage() {
        return servicePointViews[3];
    }

    @Override
    public CustomerView getCustomer(int id) {
        return new CustomerView(id);
    }

    public void updateResults(String message) {
        Platform.runLater(() -> resultsArea.setText(message));
    }

    public static void main(String[] args) {
        launch(args);
    }
}