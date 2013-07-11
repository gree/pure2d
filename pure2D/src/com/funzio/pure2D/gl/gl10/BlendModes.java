/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class BlendModes {
    public static final String ADD = "add";
    public static final String SCREEN = "screen";
    public static final String SCREEN_ALPHA = "screen_alpha";
    public static final String MULTIPLY = "multiply";
    public static final String INTERPOLATE = "interpolate";
    public static final String INTERPOLATE2 = "interpolate2"; // with additive alpha (separate)
    public static final String PREMULTIPLIED_ALPHA = "premultiplied_alpha";
    public static final String INTERPOLATE_COLOR = "interpolate_color";

    public static final BlendFunc ADD_FUNC = BlendFunc.getAdd();
    public static final BlendFunc SCREEN_FUNC = BlendFunc.getScreen();
    public static final BlendFunc SCREEN_ALPHA_FUNC = BlendFunc.getScreenAlpha();
    public static final BlendFunc MULTIPLY_FUNC = BlendFunc.getMultiply();
    public static final BlendFunc INTERPOLATE_FUNC = BlendFunc.getInterpolate();
    public static final BlendFunc INTERPOLATE2_FUNC = BlendFunc.getInterpolate2();
    public static final BlendFunc PREMULTIPLIED_ALPHA_FUNC = BlendFunc.getPremultipliedAlpha();
    public static final BlendFunc INTERPOLATE_COLOR_FUNC = BlendFunc.getInterpolateColor();

    public static boolean isInterpolate(final BlendFunc bf) {
        return bf == null || (bf.src == GL10.GL_SRC_ALPHA && bf.dst == GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static boolean isPremultipliedAlpha(final BlendFunc bf) {
        return bf != null && (bf.src == GL10.GL_ONE && bf.dst == GL10.GL_ONE_MINUS_SRC_ALPHA);
    }
}
