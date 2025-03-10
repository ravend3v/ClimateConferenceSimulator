package simulation.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import simulation.model.CustomerType;

public class CustomerView extends Circle {
    public CustomerView(int id, CustomerType type) {
        super(10);
        switch (type) {
            case STUDENT:
                setFill(Color.BLUE);
                break;
            case DECIDER:
                setFill(Color.GREEN);
                break;
            case EXPERT:
                setFill(Color.RED);
                break;
            default:
                setFill(Color.PINK);
                break;
        }
    }
}
