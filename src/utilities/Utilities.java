package utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Utilities class.
 *
 * Provides some general purpose functions.
 */
public class Utilities {

    private final Random r = new Random();

    /**
     * Sigmoid function.
     *
     * @param x Argument x.
     * @return sigmoid(x)
     */
    public static float sigmoid(double x) {
        return (float) ((float) 1d / (1 + Math.exp(-x)));
    }

    /**
     * Function that returns a random double in range [l, u].
     *
     * @param l Lower bound for double value.
     * @param u Upper bound for double value.
     * @return Random double in range.
     */
    public double randomDouble(double l, double u) {
        return (l + Math.random()* (u - l));
    }

    /**
     * Returns a random integer in range [0, u].
     *
     * @param u Upper limit integer.
     * @return Random integer.
     */
    public int randomInt(int u) {
        return r.nextInt(u);
    }

    /**
     * Returns random Gaussian distribution double.
     *
     * @return The random double.
     */
    public double gaussian() {
        return r.nextGaussian();
    }

    /**
     * Copy a list of integers.
     *
     * @param original The original list.
     * @return The copied list.
     */
    public List<Integer> copy(List<Integer> original) {
        return new LinkedList<>(original);
    }
}
