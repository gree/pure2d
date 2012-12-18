/**
 * 
 */
package com.funzio.pure2D.utils;

import android.util.FloatMath;

/**
 * @author long
 */

// NOTE: this is NOT actually faster than FloatMath yet
@Deprecated
public class FastMath {

    public static final float PI_D2 = (float) Math.PI / 2f;
    public static final float TO_RADIAN = (float) Math.PI / 180;
    public static final float TO_DEGREE = 180 / (float) Math.PI;

    private static final float[] SIN = new float[360];
    private static final float[] COS = new float[360];

    static {
        for (int i = 0; i < 180; i++) {
            COS[i] = FloatMath.cos(i * TO_RADIAN);
            if (i > 0) {
                COS[360 - i] = COS[i];
            }

            SIN[90 + i] = COS[i];
            if (i <= 90) {
                SIN[90 - i] = COS[i];
            } else {
                SIN[360 + 90 - i] = COS[i];
            }
        }
    }

    public static float sin(final int degree) {
        return SIN[(degree % 360 + 360) % 360];
    }

    public static float sin(final float radian) {
        return SIN[(Math.round(radian * TO_DEGREE) % 360 + 360) % 360];
    }

    public static float cos(final int degree) {
        return COS[(degree % 360 + 360) % 360];
    }

    public static float cos(final float radian) {
        return COS[(Math.round(radian * TO_DEGREE) % 360 + 360) % 360];
    }

    public static float tan(final int degree) {
        final int fixedDegree = (degree % 360 + 360) % 360;
        return SIN[fixedDegree] / COS[fixedDegree];
    }

    public static float tan(final float radian) {
        final int fixedDegree = (Math.round(radian * TO_DEGREE) % 360 + 360) % 360;
        return SIN[fixedDegree] / COS[fixedDegree];
    }
}
