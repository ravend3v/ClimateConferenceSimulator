package simulation.view;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class ServicePointView extends VBox {
    private final Text nameText;
    private final VBox queueBox;

    public ServicePointView(String name) {
        nameText = new Text(name);
        queueBox = new VBox();

        this.getChildren().addAll(nameText, queueBox);
    }

    public void addCustomerView(CustomerView customerView) {

        if (!queueBox.getChildren().contains(customerView) && queueBox.getChildren().size() < (int) getUserData()) {
            queueBox.getChildren().add(customerView);
        }
    }

    public void removeCustomerView(CustomerView customerView) {
        queueBox.getChildren().remove(customerView);
    }
}
