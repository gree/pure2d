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
    private int[] mOriginalProjection = new int[5];
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
        mGLState.setProjection(mProjection, 0, mWidth, 0, mHeight);

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
            mGLState.setProjection(mOriginalProjection[0], mOriginalProjection[1], mOriginalProjection[2], mOriginalProjection[3], mOriginalProjection[4]);
        }

        // Restore the view port
        mGLState.setViewport(mOriginalViewport[0], mOriginalViewport[1], mOriginalViewport[2], mOriginalViewport[3]);

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
