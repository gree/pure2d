/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;

/**
 * @author long
 */
public class NovaConfig {
    private static final String TAG = NovaConfig.class.getSimpleName();

    public static final Random RANDOM = new Random();

    // interpolators
    private static final String INTER_ST_ACCELERATE = "accelerate";
    private static final String INTER_ST_DECELERATE = "decelerate";
    private static final String INTER_ST_ACCELERATE_DECELERATE = "accelerate_decelerate";
    private static final String INTER_ST_BOUNCE = "bounce";

    public static final DecelerateInterpolator INTER_DECELERATE = new DecelerateInterpolator();
    public static final AccelerateInterpolator INTER_ACCELARATE = new AccelerateInterpolator();
    public static final AccelerateDecelerateInterpolator INTER_ACCELARATE_DECELERATE = new AccelerateDecelerateInterpolator();
    public static final BounceInterpolator INTER_BOUNCE = new BounceInterpolator();

    // blend modes and functions
    public static final String BF_ST_ADD = "add";
    public static final String BF_ST_SCREEN = "screen";
    public static final String BF_ST_SCREEN_ALPHA = "screen_alpha";
    public static final String BF_ST_MULTIPLY = "multiply";
    public static final String BF_ST_INTERPOLATE = "interpolate";
    public static final String BF_ST_PREMULTIPLIED_ALPHA = "premultiplied_alpha";
    public static final String BF_ST_INTERPOLATE_COLOR = "interpolate_color";

    public static final BlendFunc BF_ADD = BlendFunc.getAdd();
    public static final BlendFunc BF_SCREEN = BlendFunc.getScreen();
    public static final BlendFunc BF_SCREEN_ALPHA = BlendFunc.getScreenAlpha();
    public static final BlendFunc BF_MULTIPLY = BlendFunc.getMultiply();
    public static final BlendFunc BF_INTERPOLATE = BlendFunc.getInterpolate();
    public static final BlendFunc BF_PREMULTIPLIED_ALPHA = BlendFunc.getPremultipliedAlpha();
    public static final BlendFunc BF_INTERPOLATE_COLOR = BlendFunc.getInterpolateColor();

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
        } else {
            // randomly pick one of the given values
            return index < 0 ? values.get(RANDOM.nextInt(size)) : values.get(index % size);
        }
    }

    public static Interpolator getInterpolator(final String name) {
        if (INTER_ST_ACCELERATE.equalsIgnoreCase(name)) {
            return INTER_ACCELARATE;
        } else if (INTER_ST_DECELERATE.equalsIgnoreCase(name)) {
            return INTER_DECELERATE;
        } else if (INTER_ST_ACCELERATE_DECELERATE.equalsIgnoreCase(name)) {
            return INTER_ACCELARATE_DECELERATE;
        } else if (INTER_ST_BOUNCE.equalsIgnoreCase(name)) {
            return INTER_BOUNCE;
        }

        return null;
    }

    public static BlendFunc getBlendFunc(final String mode) {
        if (BF_ST_ADD.equalsIgnoreCase(mode)) {
            return BF_ADD;
        } else if (BF_ST_SCREEN.equalsIgnoreCase(mode)) {
            return BF_SCREEN;
        } else if (BF_ST_SCREEN_ALPHA.equalsIgnoreCase(mode)) {
            return BF_SCREEN_ALPHA;
        } else if (BF_ST_MULTIPLY.equalsIgnoreCase(mode)) {
            return BF_MULTIPLY;
        } else if (BF_ST_INTERPOLATE.equalsIgnoreCase(mode)) {
            return BF_INTERPOLATE;
        } else if (BF_ST_INTERPOLATE_COLOR.equalsIgnoreCase(mode)) {
            return BF_INTERPOLATE_COLOR;
        } else if (BF_ST_PREMULTIPLIED_ALPHA.equalsIgnoreCase(mode)) {
            return BF_PREMULTIPLIED_ALPHA;
        }

        return null;
    }

    /**
     * @param loop_mode
     * @return
     */
    public static int getLoopMode(final String mode) {
        if (LOOP_REPEAT.equalsIgnoreCase(mode)) {
            return Playable.LOOP_REPEAT;
        } else if (LOOP_REVERSE.equalsIgnoreCase(mode)) {
            return Playable.LOOP_REVERSE;
        }

        return Playable.LOOP_NONE;
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
