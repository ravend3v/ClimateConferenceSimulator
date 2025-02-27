// This file exists to avoid issues when running the tests for the terminal based components
package simulation.model;

import simulation.controller.IControllerM;
import simulation.view.ServicePointView;

public class TestOwnMotor extends OwnMotor{
    public TestOwnMotor(IControllerM controller) {
        super(controller, new int[4]);
        for (int i = 0; i < getServicePointViews().length; i++) {
            getServicePointViews()[i] = new ServicePointView("ServicePoint " + (i + 1));
        }
    }
}
