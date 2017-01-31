package finalStretch.util;

import battlecode.common.BodyInfo;
import battlecode.common.Direction;

public class Util {
    public static Direction randomDirection() {
        return new Direction((float) Math.random() * 2 * (float) Math.PI);
    }

    public static BodyInfo[] concatArrays(BodyInfo[]... arrays) {
        int length = 0;
        for (BodyInfo[] array : arrays) {
            length += array.length;
        }
        BodyInfo[] result = new BodyInfo[length];
        int pos = 0;
        for (BodyInfo[] array : arrays) {
            for (BodyInfo element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

    /*
        https://en.wikipedia.org/wiki/Fast_inverse_square_root
        The algorithm computes 1/sqrt(x) by performing the following steps:
            1. Alias the argument x to an integer, as a way to compute an approximation of log2(x)
            2. Use this approximation to compute an approximation of log2(1/sqrt(x))
            3. Alias back to a float, as a way to compute an approximation of the base-2 exponential
            4. Refine the approximation using a single iteration of the Newton's method.
     */
    //todo: use for vector normalization? could be what we need for scaling
    //todo: determine if this costs more or less than 1/Math.sqrt(x) with 1 and 2 iterations of Newton's method
    public static float invSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x);
//        i = 0x5f3759df - (i >> 1);
        i = 0x5f375a86 - (i >> 1); //slightly better magic number
        x = Float.intBitsToFloat(i);
        x *= (1.5f - xhalf * x * x);
//        x *= (1.5f - xhalf * x * x); //each iteration of Newton's is very slow, but drastically increases accuracy
        return x;
    }
}
