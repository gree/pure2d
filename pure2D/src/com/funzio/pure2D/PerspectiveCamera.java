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
 */
public class PerspectiveCamera extends Camera {
    private float mZFar = 1000f;

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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Camera#setSize(float, float)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // update z-far
        mZFar = h;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Camera#project(com.funzio.pure2D.gl.gl10.GLState)
     */
    @Override
    protected void project(final GLState glState) {
        final GL10 gl = glState.mGL;
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();

        // perspective projection
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
