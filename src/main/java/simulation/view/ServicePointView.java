package simulation.view;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import simulation.model.Customer;

public class ServicePointView extends VBox {
    private final Text nameText;
    private final VBox queueBox;

    public ServicePointView(String name) {
        nameText = new Text(name);
        queueBox = new VBox();

        this.getChildren().addAll(nameText, queueBox);
    }

    public void addCustomerView(CustomerView customerView) {
        queueBox.getChildren().add(customerView);
    }

    public void removeCustomerView(CustomerView customerView) {
        queueBox.getChildren().remove(customerView);
    }
}
