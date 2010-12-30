/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vivae.cea;

/**
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
public class MathUtil {

    /**
     * Clips the value between max and min value.
     * Does not check whether the max parameter is bigger than min parameter.
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static double clip(double val, double min, double max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }
}
