package simulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simulation.model.OwnMotor;

import simulation.view.ServicePointView;

public class SimulationApp extends Application {

    ServicePointView[] servicePointViews = new ServicePointView[4];
    Label statusLabel = new Label("Simulation not started");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Climate Conference Simulator");

        // Create UI elements
        Label durationLabel = new Label("Simulation Duration");
        TextField durationField = new TextField();
        Button startButton = new Button("Start Simulation");

        // Create service point views
        servicePointViews[0] = new ServicePointView("Event Entrance");
        servicePointViews[1] = new ServicePointView("Renewable Energt Stand");
        servicePointViews[2] = new ServicePointView("Climate Showcase Room");
        servicePointViews[3] = new ServicePointView("Main Stage");

        HBox servicePointsBox = new HBox(10);
        servicePointsBox.getChildren().addAll(servicePointViews);

        // Set up a layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(durationLabel, durationField, startButton, statusLabel, servicePointsBox);

        // Set up the scene
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start simulation on button click
        startButton.setOnAction(e -> {
            double duration = Double.parseDouble(durationField.getText());
            startSimulation(duration);
        });
    }

    // Method to start the simulation
    private void startSimulation(double duration) {
        // Initialize the simulator motor
        OwnMotor motor = new OwnMotor(servicePointViews);
        motor.setSimulationTime(duration);

        // Run the simulation on a seperate thread
        new Thread(() -> {
            motor.run();

            // Update the UI on the javafx application thread
            Platform.runLater(() -> {
                statusLabel.setText("Simulation Completed");
            });
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
