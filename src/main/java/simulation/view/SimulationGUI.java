// File: src/main/java/simulation/view/SimulationGUI.java
package simulation.view;

import javafx.animation.*;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import simulation.controller.Controller;
import simulation.controller.IControllerV;
import simulation.model.CustomerType;

import database.DatabaseUtils;
import utils.ChatterDisplayUtils;

import java.net.URL;
import java.util.List;

public class SimulationGUI extends Application implements ISimulationUI {
    private IControllerV controller;
    private TextArea resultsArea;
    private final ServicePointView[] servicePointViews = new ServicePointView[4];
    private final Label statusLabel = new Label();
    private TextField delay;
    private TextArea chatterArea;
    private Label clockLabel;
    private Timeline timeline;
    private double simulationTime;

    // Database chatter
    private DatabaseUtils mongoDBConnection;
    private List<String> chatterMessages;
    private ChatterDisplayUtils chatterDisplayUtils;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Climate Conference Simulator");

        // Initialize the resultsTextArea
        resultsArea = new TextArea();
        resultsArea.setFont(new Font(16));
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setPrefHeight(700); // Adjust height to match the window
        resultsArea.setPrefWidth(300); // Adjust width as needed

        // Results Area Title
        Label resultsTitle = new Label("Results / Output Area");
        resultsTitle.setFont(new Font(18));
        resultsTitle.setStyle("-fx-text-fill: black;");

        // Simulation duration input field
        Label durationLabel = new Label("Simulation Duration:");
        durationLabel.setFont(new Font(18));
        durationLabel.setStyle("-fx-text-fill: black;");

        TextField durationField = new TextField();
        durationField.setPromptText("Enter time...");
        durationField.setMaxWidth(200);
        durationField.setStyle("-fx-text-fill: black;");

        // Capacity dropdowns
        ComboBox<Integer>[] numberDropdowns = new ComboBox[4];
        Label[] dropdownLabels = new Label[4];
        String[] serviceNames = {
                "Event Entrance",
                "Renewable Energy Stand",
                "Climate Showcase Room",
                "Main Stage"
        };

        // Perfect alignment settings
        double labelWidth = 200;
        double dropdownWidth = 70;

        // Loop through and style each dropdown
        for (int i = 0; i < numberDropdowns.length; i++) {
            numberDropdowns[i] = new ComboBox<>();
            for (int j = 1; j <= 10; j++) {
                numberDropdowns[i].getItems().add(j);
            }
            numberDropdowns[i].setValue(1);

            // Set dropdown text color
            numberDropdowns[i].setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(item.toString());
                        setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    }
                }
            });

            dropdownLabels[i] = new Label(serviceNames[i]);
            dropdownLabels[i].setFont(new Font(16));
            dropdownLabels[i].setMinWidth(labelWidth);
            dropdownLabels[i].setAlignment(Pos.BASELINE_LEFT);
            dropdownLabels[i].setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

            numberDropdowns[i].setMinWidth(dropdownWidth);
            numberDropdowns[i].setMaxWidth(dropdownWidth);
            numberDropdowns[i].setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #4A0066, #220033);" +
                            "-fx-border-color: #D269FF;" +
                            "    -fx-effect: dropshadow(gaussian, rgba(210, 105, 255, 0.9), 20, 0.7, 0, 0);" +
                            "-fx-border-radius: 5px;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;"
            );
        }

        for (int i = 0; i < numberDropdowns.length; i++) {
            final int index = i;
            numberDropdowns[i].valueProperty().addListener((obs, oldVal, newVal) -> {
                servicePointViews[index].setUserData(newVal);
            });
        }

        GridPane dropdownGrid = new GridPane();
        dropdownGrid.setHgap(15);
        dropdownGrid.setVgap(12);
        dropdownGrid.setAlignment(Pos.CENTER);
        dropdownGrid.getStyleClass().add("dropdown-container");
        dropdownGrid.setPadding(new Insets(10, 0, 10, 0));

        for (int i = 0; i < numberDropdowns.length; i++) {
            dropdownGrid.add(dropdownLabels[i], 0, i);
            dropdownGrid.add(numberDropdowns[i], 1, i);
        }

        VBox searchBox = new VBox(10, durationLabel, durationField, dropdownGrid);
        searchBox.setSpacing(8);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(0, 0, 20, 0));

        // Textfield for delay
        Label delayLabel = new Label("Delay:");
        delayLabel.setStyle("-fx-text-fill: black;");
        delay = new TextField();
        delay.setPromptText("Set delay...");
        delay.setStyle("-fx-text-fill: black;");
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle("-fx-text-fill: black;");

        // Button for slowing down simulation
        Button slowdownBtn = new Button("Slow down");
        slowdownBtn.setOnAction(e -> controller.slowDown());

        // Button for speeding up simulation
        Button speedupBtn = new Button("Speed up");
        speedupBtn.setOnAction(e -> controller.speedUp());

        // Start Simulation button
        Button startButton = new Button("Start Simulation");
        startButton.getStyleClass().add("start-button");

        // Use GridPane for better layout
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.add(delayLabel, 0, 0);
        buttonGrid.add(delay, 1, 0);
        buttonGrid.add(slowdownBtn, 0, 1);
        buttonGrid.add(speedupBtn, 1, 1);
        buttonGrid.add(startButton, 0, 2, 2, 1);

        VBox buttonContainer = new VBox(20, buttonGrid);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        int[] dropcapacities = new int[numberDropdowns.length];
        for (int i = 0; i < numberDropdowns.length; i++) {
            dropcapacities[i] = numberDropdowns[i].getValue();
        }

        // Create and apply correct colors for service point views with capacities
        servicePointViews[0] = createStyledServicePoint("Event Entrance", "event-entrance", dropcapacities[0]);
        servicePointViews[1] = createStyledServicePoint("Renewable Energy Stand", "renewable-energy", dropcapacities[1]);
        servicePointViews[2] = createStyledServicePoint("Climate Showcase Room", "climate-showcase", dropcapacities[2]);
        servicePointViews[3] = createStyledServicePoint("Main Stage", "main-stage", dropcapacities[3]);

        // Prevent stretching
        for (ServicePointView spv : servicePointViews) {
            spv.setMaxSize(200, 150);
            spv.setPrefSize(200, 150);
            spv.setMinSize(200, 150);
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

        // Clock Label
        clockLabel = new Label("Simulation Time: 0.0s");
        clockLabel.setFont(new Font(18));
        clockLabel.setStyle("-fx-text-fill: white;");
        clockLabel.getStyleClass().add("clock-label");

        // Chatter Area
        chatterArea = new TextArea();
        chatterArea.setFont(new Font(16));
        chatterArea.setEditable(false);
        chatterArea.setWrapText(true);
        chatterArea.setPrefHeight(200);
        chatterArea.setPrefWidth(300);
        chatterArea.getStyleClass().add("text-area");

        // Chatter Area Title
        Label chatterTitle = new Label("Conference Chatter");
        chatterTitle.setFont(new Font(18));
        chatterTitle.setStyle("-fx-text-fill: black;");

        // MAIN LAYOUT
        VBox leftLayout = new VBox(10);
        leftLayout.setAlignment(Pos.TOP_LEFT);
        leftLayout.getChildren().addAll(resultsTitle, resultsArea);

        VBox rightLayout = new VBox(20);
        rightLayout.setAlignment(Pos.TOP_RIGHT);
        rightLayout.getChildren().addAll(clockLabel, chatterTitle, chatterArea);

        VBox centerLayout = new VBox(20);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().addAll(searchBox, buttonContainer, statusLabel, serviceGrid);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(leftLayout);
        mainLayout.setCenter(centerLayout);
        mainLayout.setRight(rightLayout);
        mainLayout.setPadding(new Insets(20));

        // Scene and window settings
        Scene scene = new Scene(mainLayout, 1000, 700);

        URL cssURL = getClass().getResource("/styles/style.css");
        if (cssURL != null) {
            scene.getStylesheets().add(cssURL.toExternalForm());
            System.out.println("CSS Loaded: " + cssURL);
        } else {
            System.err.println("ERROR: style.css not found! Check file path.");
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();

        controller = new Controller(this, this);

        // Initialize MongoDB connection and fetch chatter messages
        mongoDBConnection = new DatabaseUtils();
        chatterMessages = mongoDBConnection.getChatter();

        // Initialize ChatterDisplayUtils
        chatterDisplayUtils = new ChatterDisplayUtils();

        // Button functionality
        startButton.setOnAction(e -> {
            try {
                double duration = Double.parseDouble(durationField.getText());
                int[] capacities = new int[numberDropdowns.length];

                for (int i = 0; i < numberDropdowns.length; i++) {
                    capacities[i] = numberDropdowns[i].getValue();
                }

                if (delay.getText().isEmpty()) {
                    statusLabel.setText("Delay field cannot be empty!");
                } else {
                    controller.startSimulation(duration, capacities);
                    startClock(duration);
                    chatterDisplayUtils.startChatterTimeline(chatterArea, chatterMessages, 1000);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid number!");
            }
        });
    }

    private ServicePointView createStyledServicePoint(String name, String cssClass, int capacity) {
        ServicePointView spv = new ServicePointView(name);
        spv.setUserData(capacity);

        spv.getStyleClass().add("service-point");
        spv.getStyleClass().add(cssClass);

        // Size restrictions
        spv.setMaxHeight(150);
        spv.setMinHeight(150);
        spv.setMaxWidth(200);
        spv.setMinWidth(200);

        return spv;
    }

    // Start the GUI clock
    private void startClock(double duration) {
        simulationTime = 0.0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            simulationTime += 1.0;
            controller.updateClock();
            if (simulationTime >= duration) {
                stopClock();
                chatterDisplayUtils.stopChatterTimeline();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void stopClock() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public void updateClockLabel(String time) {
        clockLabel.setText(time);
    }

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
    public CustomerView getCustomer(int id, CustomerType type) {
        return new CustomerView(id, type);
    }

    public void updateResults(String message) {
        Platform.runLater(() -> resultsArea.setText(message));
    }

    @Override
    public long getDelay() {
        return Long.parseLong(delay.getText());
    }

    public static void main(String[] args) {
        launch(args);
    }
}