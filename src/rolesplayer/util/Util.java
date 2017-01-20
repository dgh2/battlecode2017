package rolesplayer.util;

import battlecode.common.BodyInfo;
import battlecode.common.Direction;

public class Util {
    /**
     * Returns a random Direction
     *
     * @return a random Direction
     */
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
}
