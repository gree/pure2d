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
package com.funzio.pure2D.particles.nova;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.funzio.pure2D.LoopModes;
import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public class NovaConfig {
    private static final String TAG = NovaConfig.class.getSimpleName();

    public static final Random RANDOM = new Random();

    public static final String $SD = "$sd/"; // location on sdcard, bingo compatibility
    public static final String $TEXT = "$text";
    public static final String $SPRITE = "$sprite";

    // interpolators
    private static final String INTER_ST_ACCELERATE = "accelerate";
    private static final String INTER_ST_ACCELERATE_DECELERATE = "accelerate_decelerate";
    private static final String INTER_ST_ANTICIPATE = "anticipate";
    private static final String INTER_ST_ANTICIPATE_OVERSHOOT = "anticipate_overshoot";
    private static final String INTER_ST_BOUNCE = "bounce";
    private static final String INTER_ST_CYCLE = "cycle";
    private static final String INTER_ST_DECELERATE = "decelerate";
    private static final String INTER_ST_OVERSHOOT = "overshoot";

    public static final AccelerateInterpolator INTER_ACCELARATE = new AccelerateInterpolator();
    public static final AccelerateDecelerateInterpolator INTER_ACCELARATE_DECELERATE = new AccelerateDecelerateInterpolator();
    public static final AnticipateInterpolator INTER_ANTICIPATE = new AnticipateInterpolator();
    public static final AnticipateOvershootInterpolator INTER_ANTICIPATE_OVERSHOOT = new AnticipateOvershootInterpolator();
    public static final BounceInterpolator INTER_BOUNCE = new BounceInterpolator();
    public static final CycleInterpolator INTER_CYCLE = new CycleInterpolator(1);
    public static final DecelerateInterpolator INTER_DECELERATE = new DecelerateInterpolator();
    public static final OvershootInterpolator INTER_OVERSHOOT = new OvershootInterpolator();

    // loop modes
    public static final String LOOP_REPEAT = "repeat";
    public static final String LOOP_REVERSE = "reverse";

    public static float getFloat(final ArrayList<Float> values, final int index, final float defaultValue) {
        final int size;
        if (values == null || (size = values.size()) == 0) {
            return defaultValue;
        }

        if (size == 1) {
            // fixed value
            return values.get(0);
        } else if (size == 2) {
            // random value within a range
            return values.get(0) + RANDOM.nextFloat() * (values.get(1) - values.get(0));
        } else {
            // randomly pick one of the given values
            return index < 0 ? values.get(RANDOM.nextInt(size)) : values.get(index % size);
        }
    }

    public static int getInt(final ArrayList<Integer> values, final int index, final int defaultValue) {
        final int size;
        if (values == null || (size = values.size()) == 0) {
            return defaultValue;
        }

        if (size == 1) {
            // fixed value
            return values.get(0);
        } else if (size == 2) {
            // random value within a range
            return values.get(0) + (int) (RANDOM.nextFloat() * (values.get(1) - values.get(0)));
        } else {
            // randomly pick one of the given values
            return index < 0 ? values.get(RANDOM.nextInt(size)) : values.get(index % size);
        }
    }

    public static String getString(final ArrayList<String> values, final int index) {
        final int size;
        if (values == null || (size = values.size()) == 0) {
            return null;
        }

        if (size == 1) {
            // fixed value
            return values.get(0);
        } else {
            // randomly pick one of the given values
            return index < 0 ? values.get(RANDOM.nextInt(size)) : values.get(index % size);
        }
    }

    public static GLColor getColor(final ArrayList<GLColor> values, final int index, final GLColor defaultValue) {
        final int size;
        if (values == null || (size = values.size()) == 0) {
            return defaultValue;
        }

        if (size == 1) {
            // fixed value
            return values.get(0);
            // } else if (size == 2) {
            // // random value within a range. Slow!!! lots of GC!
            // return GLColor.createInterpolatedColor(values.get(0), values.get(1), RANDOM.nextFloat());
        } else {
            // randomly pick one of the given values
            return index < 0 ? values.get(RANDOM.nextInt(size)) : values.get(index % size);
        }
    }

    public static Interpolator getInterpolator(final String name) {
        if (INTER_ST_ACCELERATE.equalsIgnoreCase(name)) {
            return INTER_ACCELARATE;
        } else if (INTER_ST_ANTICIPATE.equalsIgnoreCase(name)) {
            return INTER_ANTICIPATE;
        } else if (INTER_ST_ANTICIPATE_OVERSHOOT.equalsIgnoreCase(name)) {
            return INTER_ANTICIPATE_OVERSHOOT;
        } else if (INTER_ST_OVERSHOOT.equalsIgnoreCase(name)) {
            return INTER_OVERSHOOT;
        } else if (INTER_ST_DECELERATE.equalsIgnoreCase(name)) {
            return INTER_DECELERATE;
        } else if (INTER_ST_CYCLE.equalsIgnoreCase(name)) {
            return INTER_CYCLE;
        } else if (INTER_ST_ACCELERATE_DECELERATE.equalsIgnoreCase(name)) {
            return INTER_ACCELARATE_DECELERATE;
        } else if (INTER_ST_BOUNCE.equalsIgnoreCase(name)) {
            return INTER_BOUNCE;
        }

        return null;
    }

    /**
     * @param loop_mode
     * @return
     */
    public static int getLoopMode(final String mode) {
        if (LOOP_REPEAT.equalsIgnoreCase(mode)) {
            return LoopModes.LOOP_REPEAT;
        } else if (LOOP_REVERSE.equalsIgnoreCase(mode)) {
            return LoopModes.LOOP_REVERSE;
        }

        return LoopModes.LOOP_NONE;
    }

    /**
     * @param prefix
     * @param param
     * @return the param index. For example: $text returns 0, $text0 returns 0, $text3 returns 3
     */
    public static int getParamIndex(final String prefix, final String param) {
        // null check
        if (param == null) {
            // no index
            return -1;
        }

        if (param.equals(prefix)) {
            // default index
            return 0;
        } else if (param.indexOf(prefix) == 0) {
            final String indexString = param.substring(prefix.length());
            try {
                // variable index
                return Integer.valueOf(indexString);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid Param! " + param, e);

                // no idex
                return -1;
            }
        } else {
            // no index
            return -1;
        }
    }

    /**
     * @param prefix
     * @param param
     * @param values
     * @return the value for a param
     */
    public static Object getParamValue(final String prefix, final String param, final Object... values) {
        // null check
        if (values == null) {
            return null;
        }

        final int index = getParamIndex(prefix, param);
        if (index >= 0 && index < values.length) {
            return values[index];
        } else {
            return null;
        }
    }
}
