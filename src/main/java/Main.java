import simulation.view.SimulationGUI;

public class Main {

    public static void main(String[] args){
        try {
            SimulationGUI.launch(SimulationGUI.class);
        } catch (Exception e) {
            System.err.println("Error launching Simulation GUI");
            e.printStackTrace();
        }
    }
}
