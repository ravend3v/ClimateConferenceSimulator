package simulation.model;
import eduni.distributions.*;

public class CustomerDistribution {
    private Uniform typeDistribution;

    public CustomerDistribution() {
        typeDistribution = new Uniform(0, 2, System.currentTimeMillis());
    }

    public int getNextCustomerType() {
        int number =  (int) Math.round(typeDistribution.sample());
        return number;
    }
}
