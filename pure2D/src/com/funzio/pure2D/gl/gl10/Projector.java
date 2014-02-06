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

import android.graphics.PointF;

/**
 * @author long
 */
@Deprecated
public class Projector {
    // private static final String TAG = Projector.class.getSimpleName();

    private GLState mGLState;
    private GL10 mGL;
    private final int mProjection;
    private final int mWidth;
    private final int mHeight;

    private int[] mOriginalViewport = new int[4];
    private float[] mOriginalProjection = new float[5];
    private boolean mBinded = false;
    private int mPushMatrixError = 0;

    public Projector(final GLState glState, final int projection, final int width, final int height) {
        mGLState = glState;
        mGL = glState.mGL;
        mProjection = projection;
        mWidth = width;
        mHeight = height;
    }

    /**
     * Bind this frame buffer
     */
    public boolean bind() {
        if (mBinded) {
            return false;
        }

        // flag
        mBinded = true;

        // back up the viewport
        mGLState.getViewport(mOriginalViewport);

        // Select the projection matrix
        mGL.glMatrixMode(GL10.GL_PROJECTION);
        // test pushing
        if (mPushMatrixError == 0) {
            mGL.glPushMatrix(); // see glGetIntegerv(GL_MAX_PROJECTION_STACK_DEPTH, &result, 0);
            // check error
            mPushMatrixError = mGL.glGetError();
        }
        if (mPushMatrixError == GL10.GL_STACK_OVERFLOW) {
            // Log.e(TAG, "Error Push Matrix: " + mPushMatrixError + ": " + GLU.gluErrorString(mPushMatrixError));
            // fall-back solution: back up projection
            mGLState.getProjection(mOriginalProjection);
        } else {
            // clear
            mPushMatrixError = 0;
        }
        // Reset the projection matrix
        mGL.glLoadIdentity();

        // set new viewport
        mGLState.setViewport(0, 0, mWidth, mHeight);

        // set new projection matrix
        mGLState.setProjection(mProjection, 0, mWidth - 1, 0, mHeight - 1);

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPushMatrix();
        mGL.glLoadIdentity();

        return true;
    }

    /**
     * Unbind this frame buffer and switch back to the original buffer
     */
    public boolean unbind() {
        if (!mBinded) {
            return false;
        }

        // unflag
        mBinded = false;

        mGL.glMatrixMode(GL10.GL_PROJECTION);
        if (mPushMatrixError == 0) {
            // Restore the projection matrix
            mGL.glPopMatrix();
        } else {
            mGL.glLoadIdentity();
            // Use mOriginalProjection instead
            mGLState.setProjection(mOriginalProjection);
        }

        // Restore the view port
        mGLState.setViewport(mOriginalViewport);

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPopMatrix();

        return true;
    }

    public boolean isBinded() {
        return mBinded;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean hasSize(final PointF size) {
        return mWidth == Math.round(size.x) && mHeight == Math.round(size.y);
    }
}
