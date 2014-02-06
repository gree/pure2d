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

import android.graphics.PointF;
import android.opengl.GLU;

import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 * @see {@link Scene#setDepthRange(float, float)}
 */

public class PerspectiveCamera extends Camera {
    private float mZFar = 1000f;

    public PerspectiveCamera(final float width, final float height) {
        super(width, height);

        mZFar = height;
    }

    /**
     * @param size
     */
    public PerspectiveCamera(final PointF size) {
        super(size);

        // update z-far
        mZFar = size.y;
    }

    /**
     * @param center
     * @param size
     */
    public PerspectiveCamera(final PointF center, final PointF size) {
        super(center, size);

        // update z-far
        mZFar = size.y;
    }

    /**
     * @param scene
     */
    public PerspectiveCamera(final Scene scene) {
        super(scene);

        // update z-far
        mZFar = mSize.y;
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // update z-far
        mZFar = h;
    }

    @Override
    public void apply(final GLState glState) {
        if (mInvalidated) {
            validate();
        }

        final GL10 gl = glState.mGL;
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        // gl.glLoadIdentity();
        gl.glPushMatrix();

        // perspective projection
        // TODO: use glState.setProjection() instead
        GLU.gluPerspective(gl, Pure2D.GL_PERSPECTIVE_FOVY / mZoom.x, mSize.x / mSize.y, 0.001f, mZFar);
        // camera view and axis system
        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            // invert the y-axis
            GLU.gluLookAt(gl, mCenter.x, mCenter.y, mZFar, mCenter.x, mCenter.y, 0, 0, -1, 0);
        } else {
            GLU.gluLookAt(gl, mCenter.x, mCenter.y, mZFar, mCenter.x, mCenter.y, 0, 0, 1, 0);
        }

        // camera rotation
        if (mRotation != 0) {
            gl.glTranslatef(mCenter.x, mCenter.y, 0);
            gl.glRotatef(-mRotation, 0, 0, 1);
            gl.glTranslatef(-mCenter.x, -mCenter.y, 0);
        }

        // set matrix back to model view
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public float getZFar() {
        return mZFar;
    }

    public void setZFar(final float ZFar) {
        mZFar = ZFar;

        invalidate();
    }

}
