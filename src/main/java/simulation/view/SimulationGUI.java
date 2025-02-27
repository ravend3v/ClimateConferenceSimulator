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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import simulation.controller.Controller;
import simulation.controller.IControllerV;
import simulation.model.OwnMotor;


public class SimulationGUI extends Application implements ISimulationUI{

    ServicePointView[] servicePointViews = new ServicePointView[4];
    Label statusLabel = new Label("Simulation not started");
    private IControllerV controller;



    public void updateStatusLabel(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Climate Conference Simulator");

        // Tyylit
        String buttonStyle = "-fx-background-color: linear-gradient(to right, #007bff, #0056b3); "
                + "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px; "
                + "-fx-border-radius: 12px; -fx-background-radius: 12px; -fx-cursor: hand;";

        String textFieldStyle = "-fx-background-color: #ffffff; -fx-border-color: #cccccc; "
                + "-fx-border-radius: 10px; -fx-padding: 8px; -fx-font-size: 14px;";

        // Simulaation keston syöttökenttä
        Label durationLabel = new Label("Simulation Duration:");
        durationLabel.setFont(new Font(16));

        TextField durationField = new TextField();
        durationField.setPromptText("Enter time...");
        durationField.setMaxWidth(200);
        durationField.setStyle(textFieldStyle);

        VBox searchBox = new VBox(10, durationLabel, durationField);
        searchBox.setAlignment(Pos.CENTER);

        // kapasiteetti valikot
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
                numberDropdowns[i].getItems().add(j); // Lisää numerot 1-10
            }
            numberDropdowns[i].setValue(1); // Aseta oletusarvoksi 1
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


        // Start Simulation -nappi
        Button startButton = new Button("Start Simulation");
        startButton.setFont(new Font(14));
        startButton.setStyle(buttonStyle);

        VBox buttonBox = new VBox(10, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Service Point -näkymät (kiinteä koko, ei venymistä)
        servicePointViews[0] = createStyledServicePoint("Event Entrance", "#42a5f5");
        servicePointViews[1] = createStyledServicePoint("Renewable Energy Stand", "#66bb6a");
        servicePointViews[2] = createStyledServicePoint("Climate Showcase Room", "#ffa726");
        servicePointViews[3] = createStyledServicePoint("Main Stage", "#ab47bc");

// Estetään venyminen
        for (ServicePointView spv : servicePointViews) {
            spv.setMaxSize(200, 150); // Maksimikoko
            spv.setPrefSize(200, 150); // Asetetaan kiinteä koko
            spv.setMinSize(200, 150); // Minimikoko, estää pienenemisenkin
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

        // Päälayout
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 50px; -fx-background-color: #f4f4f4;");
        layout.getChildren().addAll(searchBox, numberDropdownBox,buttonBox, statusLabel, serviceGrid);

        StackPane root = new StackPane(layout);
        StackPane.setAlignment(layout, Pos.CENTER);

        // Scene ja ikkuna-asetukset
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Napin toiminnallisuus
        startButton.setOnAction(e -> {
            try {
                double duration = Double.parseDouble(durationField.getText());
                int[] capacities = new int[numberDropdowns.length];
                for (int i = 0; i < numberDropdowns.length; i++) {
                    capacities[i] = numberDropdowns[i].getValue();
                }
                controller.startSimulation(duration, capacities);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid number!");
            }
        });
    }


    // Service Point -komponenttien tyylit
    private ServicePointView createStyledServicePoint(String name, String color) {
        ServicePointView spv = new ServicePointView(name);
        spv.setStyle("-fx-background-color: " + color + "; " +
                "-fx-padding: 20px; -fx-border-color: white; " +
                "-fx-border-radius: 15px; -fx-background-radius: 15px; " +
                "-fx-font-size: 14px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 3, 3);");

        // Estetään liiallinen määrä asiakkaita
        spv.setMaxHeight(150); // Rajoittaa korkeutta
        spv.setMinHeight(150); // Ei veny
        spv.setMaxWidth(200); // Rajoittaa leveyttä
        spv.setMinWidth(200);

        // Lisätään max 8 palloa per laatikko
        spv.setUserData(5);

        return spv;
    }

    public ServicePointView getEventEntrance(){
        return servicePointViews[0];
    }

    public ServicePointView getRenewable(){
        return servicePointViews[1];
    }

    public ServicePointView getShowRoom(){
        return servicePointViews[2];
    }

    public ServicePointView getMainStage(){
        return servicePointViews[3];
    }

    public CustomerView getCustomer(int id){
        return new CustomerView(id);
    }

   /* public static void main(String[] args) {
        launch(args);
    }

    */
}
