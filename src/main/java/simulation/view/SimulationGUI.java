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
import javafx.scene.control.ListCell;

import java.net.URL;

public class SimulationGUI extends Application implements ISimulationUI {
    private IControllerV controller;
    private TextArea resultsArea;
    private final ServicePointView[] servicePointViews = new ServicePointView[4];
    private final Label statusLabel = new Label();
    private TextField delay;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Climate Conference Simulator");

        // Initialize the resultsTextArea
        resultsArea = new TextArea();
        resultsArea.setFont(new Font(16));
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.setPrefHeight(200);

        // Simulation duration input field
        Label durationLabel = new Label("Simulation Duration:");
        durationLabel.setFont(new Font(18));

        TextField durationField = new TextField();
        durationField.setPromptText("Enter time...");
        durationField.setMaxWidth(200);

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
                        setStyle("-fx-text-fill: #C0C0C0; -fx-font-size: 14px;");
                    }
                }
            });

            dropdownLabels[i] = new Label(serviceNames[i]);
            dropdownLabels[i].setFont(new Font(16));
            dropdownLabels[i].setMinWidth(labelWidth);
            dropdownLabels[i].setAlignment(Pos.BASELINE_LEFT);
            dropdownLabels[i].setStyle("-fx-text-fill: #C0C0C0; -fx-font-weight: bold;"); // ✅ Bright blue text!

            numberDropdowns[i].setMinWidth(dropdownWidth);
            numberDropdowns[i].setMaxWidth(dropdownWidth);
            numberDropdowns[i].setStyle(
                    "-fx-background-color: #0D1A44;" +
                            "-fx-border-color: #C0C0C0;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-text-fill: #C0C0C0;" +
                            "-fx-font-size: 14px;"
            );
        }

        GridPane dropdownGrid = new GridPane();
        dropdownGrid.setHgap(15); // Välit
        dropdownGrid.setVgap(12);
        dropdownGrid.setAlignment(Pos.CENTER);
        dropdownGrid.getStyleClass().add("dropdown-container");
        dropdownGrid.setPadding(new Insets(-40, 0, 10, 0));

        for (int i = 0; i < numberDropdowns.length; i++) {
            dropdownGrid.add(dropdownLabels[i], 0, i);
            dropdownGrid.add(numberDropdowns[i], 1, i);
        }

        VBox searchBox = new VBox(10, durationLabel, durationField, dropdownGrid);
        searchBox.setSpacing(8);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(0, 0, 70, 0)); // Moves it UP

        // Textfield for delay
        Label delayLabel = new Label("Delay:");
        delay = new TextField();
        delay.setPromptText("Set delay...");
        statusLabel.setAlignment(Pos.CENTER);

        // Button for slowing down simulation
        Button slowdownBtn = new Button("Slow down");
        slowdownBtn.setOnAction(e -> controller.slowDown());

        // Button for speeding up simulation
        Button speedupBtn = new Button("Speed up");
        speedupBtn.setOnAction(e -> controller.speedUp());

        // Start Simulation button
        Button startButton = new Button("Start Simulation");
        startButton.getStyleClass().add("start-button");

        // Alkuperäinen buttonBox
        HBox buttonBox = new HBox(15, delayLabel, delay, slowdownBtn, speedupBtn, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Reduce spacing even more to bring buttons closer
        VBox buttonContainer = new VBox(40, buttonBox);
        buttonBox.setSpacing(8);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        // Create and apply correct colors for service point views
        servicePointViews[0] = createStyledServicePoint("Event Entrance", "event-entrance", numberDropdowns[0].getValue());
        servicePointViews[1] = createStyledServicePoint("Renewable Energy Stand", "renewable-energy", numberDropdowns[1].getValue());
        servicePointViews[2] = createStyledServicePoint("Climate Showcase Room", "climate-showcase", numberDropdowns[2].getValue());
        servicePointViews[3] = createStyledServicePoint("Main Stage", "main-stage", numberDropdowns[3].getValue());

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

        // MAIN LAYOUT
        VBox rightLayout = new VBox(5);
        rightLayout.setAlignment(Pos.CENTER);
        rightLayout.getChildren().addAll(searchBox, dropdownGrid, buttonContainer, statusLabel, serviceGrid);

        HBox mainLayout = new HBox(30);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(resultsArea, rightLayout);

        StackPane root = new StackPane(mainLayout);
        StackPane.setAlignment(mainLayout, Pos.CENTER);

        // Scene and window settings
        Scene scene = new Scene(root, 1000, 700);

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

    private ServicePointView createStyledServicePoint(String name, String cssClass, int capacity) {
        ServicePointView spv = new ServicePointView(name);
        spv.setAlignment(Pos.BASELINE_LEFT); // Force text alignment inside the box
        spv.setUserData(capacity);

        spv.getStyleClass().add("service-point");
        spv.getStyleClass().add(cssClass);

        spv.getChildren().get(0).setTranslateX(-10);

        // Size restrictions
        spv.setMaxHeight(150);
        spv.setMinHeight(150);
        spv.setMaxWidth(200);
        spv.setMinWidth(200);

        spv.setUserData(capacity);

        return spv;
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
    public CustomerView getCustomer(int id) {
        CustomerView customer = new CustomerView(id);

        // 🔹 Etsitään vapaa palvelupiste
        for (ServicePointView spv : servicePointViews) {
            if (spv.getChildren().size() < (int) spv.getUserData()) {
                Platform.runLater(() -> spv.addCustomerView(customer));
                return customer;
            }
        }

        System.out.println("⚠ Ei tilaa palvelupisteissä asiakkaalle " + id);
        return customer;
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
