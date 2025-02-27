package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        if (Double.isNaN(value) || Double.isInfinite(value)) throw new IllegalArgumentException("Invalid number");

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
