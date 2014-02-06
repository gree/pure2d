/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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
