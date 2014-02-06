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

    public static BlendFunc getBlendFunc(final String mode) {
        if (ADD.equalsIgnoreCase(mode)) {
            return ADD_FUNC;
        } else if (SCREEN.equalsIgnoreCase(mode)) {
            return SCREEN_FUNC;
        } else if (SCREEN_ALPHA.equalsIgnoreCase(mode)) {
            return SCREEN_ALPHA_FUNC;
        } else if (MULTIPLY.equalsIgnoreCase(mode)) {
            return MULTIPLY_FUNC;
        } else if (INTERPOLATE.equalsIgnoreCase(mode)) {
            return INTERPOLATE_FUNC;
        } else if (INTERPOLATE2.equalsIgnoreCase(mode)) {
            return INTERPOLATE2_FUNC;
        } else if (INTERPOLATE_COLOR.equalsIgnoreCase(mode)) {
            return INTERPOLATE_COLOR_FUNC;
        } else if (PREMULTIPLIED_ALPHA.equalsIgnoreCase(mode)) {
            return PREMULTIPLIED_ALPHA_FUNC;
        }

        return null;
    }
}
