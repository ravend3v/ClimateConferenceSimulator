// This file exists to avoid issues when running the tests for the terminal based components
package simulation.model;

import simulation.view.ServicePointView;

public class TestOwnMotor extends OwnMotor{
    public TestOwnMotor() {
        super(new ServicePointView[4]);
        for (int i = 0; i < servicePointViews.length; i++) {
            servicePointViews[i] = new ServicePointView("ServicePoint " + (i + 1));
        }
    }
}
