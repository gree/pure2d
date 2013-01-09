/**
 * 
 */
package com.funzio.pure2D;

/**
 * This class is intentionally for being used ONLY outside the engine.
 * 
 * @author long
 */
public class Pure2D {
    public static final String TAG = Pure2D.class.getSimpleName();

    // do not modify this
    public static String GL_EXTENSIONS = null;
    public static boolean GL_NPOT_TEXTURE_SUPPORTED = false;
    public static boolean GL_STENCIL8_SUPPORTED = false;

    // for non-pure2d engine
    public static Adapter ADAPTER = null;
}
