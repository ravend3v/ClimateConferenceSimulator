package simulation.model;

public enum CustomerType {
    STUDENT,DECIDER,EXPERT;

    public static CustomerType fromInt(int num) {
        switch (num) {
            case 0: return STUDENT;
            case 1: return DECIDER;
            case 2: return EXPERT;
            default: throw new IllegalArgumentException();
        }
    }
}
