/**
 * 
 */
package com.funzio.pure2D;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

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
    public static final String VERSION = "1.5";

    // do not modify this
    public static String GL_EXTENSIONS = null;
    public static boolean GL_NPOT_TEXTURE_SUPPORTED = false;
    public static boolean GL_FBO_SUPPORTED = false;
    public static boolean GL_VBO_SUPPORTED = false;
    public static boolean GL_STENCIL8_SUPPORTED = false;
    public static boolean GL_DEPTH24_SUPPORTED = false;
    public static int GL_MAX_TEXTURE_SIZE = 0;
    public static int GL_MAX_MODELVIEW_STACK_DEPTH = 0;
    public static int GL_MAX_PROJECTION_STACK_DEPTH = 0;
    public static final float GL_PERSPECTIVE_FOVY = 53.125f; // this perfectly matches the ortho projection, try 53.075f

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
     * @param flags
     * @see Pure2D.DEBUG_FLAG_SHAPE, Pure2D.DEBUG_FLAG_BOUNDS
     */
    public static void setDebugFlags(final int flags) {
        DEBUG_FLAGS = flags;
    }

    /**
     * This is used internally to load GL properties. Do not ever call.
     * 
     * @param glState
     * @hide
     */
    public static void initGLProperties(final GL10 gl) {
        // get all extensions
        GL_EXTENSIONS = gl.glGetString(GL10.GL_EXTENSIONS);

        GL_NPOT_TEXTURE_SUPPORTED = GL_EXTENSIONS.contains("GL_OES_texture_npot");
        // || GL_EXTENSIONS.contains("GL_ARB_texture_non_power_of_two"); // this might not be good enough
        // || GL_EXTENSIONS.contains("GL_APPLE_texture_2D_limited_npot"); // this is bad!

        GL_FBO_SUPPORTED = GL_EXTENSIONS.contains("GL_OES_framebuffer_object");
        GL_VBO_SUPPORTED = GL_EXTENSIONS.contains("GL_ARB_vertex_buffer_object");
        GL_STENCIL8_SUPPORTED = GL_EXTENSIONS.contains("GL_OES_stencil8");
        GL_DEPTH24_SUPPORTED = GL_EXTENSIONS.contains("GL_OES_depth24");

        // find max texture size
        final int[] scratch = new int[1];
        gl.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, scratch, 0);
        GL_MAX_TEXTURE_SIZE = scratch[0];

        // find stack sizes. @See glPushMatrix()
        gl.glGetIntegerv(GL10.GL_MAX_MODELVIEW_STACK_DEPTH, scratch, 0);
        GL_MAX_MODELVIEW_STACK_DEPTH = scratch[0];
        gl.glGetIntegerv(GL10.GL_MAX_PROJECTION_STACK_DEPTH, scratch, 0);
        GL_MAX_PROJECTION_STACK_DEPTH = scratch[0];

        Log.i(TAG, "initGLProperties():\n" //
                + "Version: " + VERSION + "\n" //
                + GL_EXTENSIONS + "\n" //
                + "NPOT Texture: " + GL_NPOT_TEXTURE_SUPPORTED + "\n" //
                + "GL_MAX_TEXTURE_SIZE: " + GL_MAX_TEXTURE_SIZE);
    }

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
    public static void drawDebugRect(final GLState glState, final float x1, final float y1, final float x2, final float y2, final int flag) {
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
