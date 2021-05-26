// 
// Decompiled by Procyon v0.5.36
// 


import javafx.util.Pair;
import java.math.BigDecimal;

public class Util
{
    static double euclidean(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    
    static BigDecimal getPackagePrice(final int type, final BigDecimal weight, final double distance) {
        switch (type) {
            case 0: {
                return new BigDecimal(115.0 * distance);
            }
            case 1: {
                return new BigDecimal((175.0 + weight.doubleValue() * 100.0) * distance);
            }
            case 2: {
                return new BigDecimal((250.0 + weight.doubleValue() * 100.0) * distance);
            }
            case 3: {
                return new BigDecimal((350.0 + weight.doubleValue() * 500.0) * distance);
            }
            default: {
                return null;
            }
        }
    }
    
    static double getDistance(final Pair<Integer, Integer>... addresses) {
        double distance = 0.0;
        for (int i = 1; i < addresses.length; ++i) {
            distance += euclidean((int)addresses[i - 1].getKey(), (int)addresses[i - 1].getValue(), (int)addresses[i].getKey(), (int)addresses[i].getValue());
        }
        return distance;
    }
}
