/**
 * 
 */
package com.funzio.pure2D;

import com.funzio.pure2D.gl.GLColor;

/**
 * This class is intentionally for being used ONLY outside the engine.
 * 
 * @author long
 */
public class Pure2D {
    public static final String TAG = Pure2D.class.getSimpleName();

    // for debugging
    public static boolean DEBUG_ENALBLED = false;
    public static float DEBUG_LINE_WIDTH = 2;
    public static GLColor DEBUG_LINE_COLOR = new GLColor(1f, 0f, 0f, 1f);

    // do not modify this
    public static String GL_EXTENSIONS = null;
    public static boolean GL_NPOT_TEXTURE_SUPPORTED = false;
    public static boolean GL_STENCIL8_SUPPORTED = false;

    // for non-pure2d engine
    public static Adapter ADAPTER = null;
}
