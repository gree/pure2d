/**
 * 
 */
package com.funzio.pure2D;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;

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
    public static final float GL_PERSPECTIVE_FOVY = 53.125f; // this perfectly matches the ortho projection

    // for non-pure2d engine to plug in
    public static Adapter ADAPTER = null;

    // for debugging
    public static final int DEBUG_FLAG_WIREFRAME = 1 << 0;
    public static final int DEBUG_FLAG_GLOBAL_BOUNDS = 1 << 1;
    public static int DEBUG_FLAGS = 0; // DEBUG_FLAG_GLOBAL_BOUNDS | DEBUG_FLAG_WIREFRAME;

    public static GLColor DEBUG_WIREFRAME_COLOR = new GLColor(1f, 0f, 0f, 1f);
    public static GLColor DEBUG_GLOBAL_BOUNDS_COLOR = new GLColor(1f, 1f, 0f, 1f);
    private static final float[] DEBUG_VERTICES = {
            0, 0, // BL
            0, 0, // TL
            0, 0, // TR
            0, 0, // BR
    };
    private static final VertexBuffer DEBUG_VERTEX_BUFFER = new VertexBuffer(GL10.GL_LINE_LOOP, 4, DEBUG_VERTICES);

    /**
     * Draw a debug rectangle
     * 
     * @param glState
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     * @hide
     */
    protected static void drawDebugRect(final GLState glState, final float x1, final float y1, final float x2, final float y2, final int flag) {
        DEBUG_VERTICES[0] = x1;
        DEBUG_VERTICES[1] = y1;
        DEBUG_VERTICES[2] = x1;
        DEBUG_VERTICES[3] = y2;
        DEBUG_VERTICES[4] = x2;
        DEBUG_VERTICES[5] = y2;
        DEBUG_VERTICES[6] = x2;
        DEBUG_VERTICES[7] = y1;

        DEBUG_VERTEX_BUFFER.setValues(DEBUG_VERTICES);

        // pre-draw
        final GLColor currentColor = glState.getColor();
        final boolean textureEnabled = glState.isTextureEnabled();
        final float currentLineWidth = glState.getLineWidth();
        glState.setColor((flag & DEBUG_FLAG_GLOBAL_BOUNDS) != 0 ? DEBUG_GLOBAL_BOUNDS_COLOR : DEBUG_WIREFRAME_COLOR);
        glState.setTextureEnabled(false);

        // draw
        DEBUG_VERTEX_BUFFER.draw(glState);

        // post-draw
        glState.setTextureEnabled(textureEnabled);
        glState.setColor(currentColor);
        glState.setLineWidth(currentLineWidth);
    }
}
