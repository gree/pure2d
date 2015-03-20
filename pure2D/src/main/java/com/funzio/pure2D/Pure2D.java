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
package com.funzio.pure2D;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * This class contains global properties and settings for Pure2D.
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

    public static boolean AUTO_UPDATE_BOUNDS = false;

    /**
     * Globally turns on debug flags on all display objects.
     * 
     * @param flags
     * @see {@link #DEBUG_FLAG_WIREFRAME}, {@link #DEBUG_FLAG_GLOBAL_BOUNDS}
     */
    public static void setDebugFlags(final int flags) {
        DEBUG_FLAGS = flags;
    }

    /**
     * Globally sets auto update bounds on all display objects. This can be used in conjunction with {@link Camera.#setClipping(boolean)}
     * 
     * @see {@link Camera.#setClipping(boolean)}
     */
    public static void setAutoUpdateBounds(final boolean value) {
        AUTO_UPDATE_BOUNDS = value;
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
        drawDebugVertices(glState, flag, x1, y1, x1, y2, x2, y2, x2, y1);
    }

    /**
     * Draw debug vertices
     * 
     * @param glState
     * @param flag
     * @param vertices
     * @hide
     */
    public static void drawDebugVertices(final GLState glState, final int flag, final float... vertices) {
        DEBUG_VERTICES[0] = vertices[0];
        DEBUG_VERTICES[1] = vertices[1];
        DEBUG_VERTICES[2] = vertices[2];
        DEBUG_VERTICES[3] = vertices[3];
        DEBUG_VERTICES[4] = vertices[4];
        DEBUG_VERTICES[5] = vertices[5];
        DEBUG_VERTICES[6] = vertices[6];
        DEBUG_VERTICES[7] = vertices[7];

        DEBUG_VERTEX_BUFFER.setValues(DEBUG_VERTICES);

        // pre-draw
        final GLColor currentColor = glState.getColor();
        final boolean textureEnabled = glState.isTextureEnabled();
        final float currentLineWidth = glState.getLineWidth();
        glState.setColor((flag & DEBUG_FLAG_GLOBAL_BOUNDS) != 0 ? DEBUG_GLOBAL_BOUNDS_COLOR : DEBUG_WIREFRAME_COLOR);
        glState.setTextureEnabled(false);

        // draw
        DEBUG_VERTEX_BUFFER.setPrimitive((flag & DEBUG_FLAG_GLOBAL_BOUNDS) != 0 ? GL10.GL_LINE_LOOP : GL10.GL_LINE_STRIP);
        DEBUG_VERTEX_BUFFER.draw(glState);

        // post-draw
        glState.setTextureEnabled(textureEnabled);
        glState.setColor(currentColor);
        glState.setLineWidth(currentLineWidth);
    }
}
