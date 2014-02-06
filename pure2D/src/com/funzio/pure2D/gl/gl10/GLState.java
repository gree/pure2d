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
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.opengl.GLES11Ext;
import android.opengl.GLU;
import android.util.Log;

import com.funzio.pure2D.Maskable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.Stage;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 * @category This class is used to manage and also limit the number of JNI calls, for optimization purpose.
 */
public class GLState {
    private static final String TAG = GLState.class.getSimpleName();

    public GL10 mGL;
    private Stage mStage;

    // texture
    private Texture mTexture = null;
    private boolean mTextureEnabled = false;
    private TextureCoordBuffer mTextureCoordBuffer;
    private boolean mTextureCoordArrayEnabled = false;

    // array toggles
    private VertexBuffer mVertexBuffer;
    private boolean mVertexArrayEnabled = false;
    private boolean mDepthTestEnabled = false;
    private boolean mScissorTestEnabled = false;

    // colors
    private boolean mColorArrayEnabled = false;
    private boolean mAlphaTestEnabled = false;
    private GLColor mColor;
    // blending
    private BlendFunc mDefaultBlendFunc = BlendFunc.getInterpolate();
    private BlendFunc mBlendFunc;
    // masking
    private Maskable mMask;
    private float mLineWidth = 0;
    // frame buffer
    private int mFrameBuffer = 0;

    // viewport and camera
    private float[] mProjection = new float[5];
    private int[] mViewport = new int[4];
    private int[] mScissor = new int[4];

    private TextureManager mTextureManager;
    private int mAxisSystem = Scene.AXIS_BOTTOM_LEFT;

    // public Camera mCamera;

    // private int mInvalidateFlags = 0;

    public GLState(final GL10 gl) {
        reset(gl);
    }

    public GLState(final GL10 gl, final Stage stage) {
        mStage = stage;
        reset(gl);
    }

    public void reset(final GL10 gl) {
        mGL = gl;

        // invalidate surface
        // mInvalidateFlags = InvalidateFlags.SURFACE;

        mTexture = null;
        mTextureEnabled = false;
        mTextureCoordBuffer = null;
        mTextureCoordArrayEnabled = false;

        mVertexBuffer = null;
        mVertexArrayEnabled = false;
        mDepthTestEnabled = false;
        mScissorTestEnabled = false;

        mColorArrayEnabled = false;
        mAlphaTestEnabled = false;
        mColor = new GLColor(1f, 1f, 1f, 1f);

        mBlendFunc = new BlendFunc(0, 0);

        mMask = null;
        mLineWidth = 0;
        mFrameBuffer = 0;

        clearErrors();
    }

    // public void validate() {
    // mInvalidateFlags = 0;
    // }
    //
    // public boolean isInvalidated(final int flags) {
    // return (mInvalidateFlags & flags) == flags;
    // }

    public void queueEvent(final Runnable r) {
        if (mStage != null) {
            mStage.queueEvent(r);
        }
    }

    public int getAxisSystem() {
        return mAxisSystem;
    }

    public void setAxisSystem(final int axisSystem) {
        mAxisSystem = axisSystem;
    }

    /**
     * Set Projection mode
     * 
     * @param projection
     * @param right
     * @param top
     * @see #Scene , Scene.AXIS_BOTTOM_LEFT, Scene.AXIS_TOP_LEFT
     */
    public void setProjection(final float projection, final float left, final float right, final float bottom, final float top) {
        if (projection == Scene.PROJECTION_PERSPECTIVE) {
            final float width = right - left + 1;
            final float height = top - bottom + 1;
            GLU.gluPerspective(mGL, Pure2D.GL_PERSPECTIVE_FOVY, width / height, 0.001f, Math.max(width, height));
            GLU.gluLookAt(mGL, 0, 0, height, 0, 0, 0, 0, 1, 0); // always based on Screen-Y
            mGL.glTranslatef(-width * 0.5f, -height * 0.5f, 0);
        } else if (projection == Scene.AXIS_TOP_LEFT) {
            // NOTE: frame-buffer has the Axis inverted
            mGL.glOrthof(left, right, top, bottom, -1, 1);
        } else if (projection == Scene.AXIS_BOTTOM_LEFT) {
            mGL.glOrthof(left, right, bottom, top, -1, 1);
        } else {
            Log.e(TAG, "Unknown Projection mode: " + projection, new Exception());
        }

        mProjection[0] = projection;
        mProjection[1] = left;
        mProjection[2] = right;
        mProjection[3] = bottom;
        mProjection[4] = top;
    }

    public void setProjection(final float[] projection) {
        setProjection(projection[0], projection[1], projection[2], projection[3], projection[4]);
    }

    public void getProjection(final float[] projection) {
        projection[0] = mProjection[0];
        projection[1] = mProjection[1];
        projection[2] = mProjection[2];
        projection[3] = mProjection[3];
        projection[4] = mProjection[4];
    }

    public void setViewport(final int x, final int y, final int width, final int height) {
        mGL.glViewport(x, y, width, height);
        mViewport[0] = x;
        mViewport[1] = y;
        mViewport[2] = width;
        mViewport[3] = height;
    }

    public void setViewport(final int[] viewport) {
        setViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    public void getViewport(final int[] viewport) {
        viewport[0] = mViewport[0];
        viewport[1] = mViewport[1];
        viewport[2] = mViewport[2];
        viewport[3] = mViewport[3];
    }

    public void setLineWidth(final float width) {
        // diff check
        if (mLineWidth == width) {
            return;
        }

        mLineWidth = width;
        mGL.glLineWidth(width);
    }

    public float getLineWidth() {
        return mLineWidth;
    }

    public boolean bindFrameBuffer(final int frameBuffer) {
        // diff check
        if (mFrameBuffer == frameBuffer) {
            return false;
        }
        mFrameBuffer = frameBuffer;

        // only works for GLES11
        ((GL11ExtensionPack) mGL).glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFrameBuffer);

        return true;
    }

    public int getFrameBuffer() {
        return mFrameBuffer;
    }

    public boolean bindTexture(final Texture texture) {
        // diff check
        if (mTexture == texture) {
            return false;
        }

        // make sure it's enabled
        setTextureEnabled(true);

        // bind to gl
        mTexture = texture;
        mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.mTextureID);

        return true;
    }

    public boolean unbindTexture() {
        // diff check
        if (mTexture == null) {
            return false;
        }

        // unbind to gl
        mTexture = null;
        // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        // make sure it's disabled
        setTextureEnabled(false);
        return true;
    }

    /**
     * @return the vertexArrayEnabled
     */
    public boolean isVertexArrayEnabled() {
        return mVertexArrayEnabled;
    }

    /**
     * @param vertexArrayEnabled the vertexArrayEnabled to set
     */
    public boolean setVertexArrayEnabled(final boolean vertexArrayEnabled) {
        // diff check
        if (mVertexArrayEnabled == vertexArrayEnabled) {
            return false;
        }

        // apply
        mVertexArrayEnabled = vertexArrayEnabled;
        if (vertexArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        return true;
    }

    public boolean setVertexBuffer(final VertexBuffer buffer) {
        // diff check, nope! doesn't work for Uni stuff
        // if (mVertexBuffer == buffer) {
        // return false;
        // }

        // values check
        final boolean same = mVertexBuffer instanceof QuadBuffer && buffer instanceof QuadBuffer && QuadBuffer.compare((QuadBuffer) mVertexBuffer, (QuadBuffer) buffer);
        if (buffer != null && !same) {
            mGL.glVertexPointer(buffer.mVertexPointerSize, GL10.GL_FLOAT, 0, buffer.mBuffer);
        }

        // now keep
        mVertexBuffer = buffer;
        return true;
    }

    /**
     * @return the textureEnabled
     */
    public boolean isTextureEnabled() {
        return mTextureEnabled;
    }

    /**
     * @param textureEnabled the textureEnabled to set
     */
    public boolean setTextureEnabled(final boolean textureEnabled) {
        // diff check
        if (mTextureEnabled == textureEnabled) {
            return false;
        }

        mTextureEnabled = textureEnabled;

        if (textureEnabled) {
            // Enable Texture
            mGL.glEnable(GL10.GL_TEXTURE_2D);
        } else {
            mGL.glDisable(GL10.GL_TEXTURE_2D);
        }

        return true;
    }

    public boolean setTextureCoordBuffer(final TextureCoordBuffer buffer) {
        // diff check, nope! doesn't work for Uni stuff
        // if (mTextureCoordBuffer == buffer) {
        // return false;
        // }

        // values check
        if (buffer != null) { // && !TextureCoordBuffer.compare(mTextureCoordBuffer, buffer) // not correct for all cases
            mGL.glTexCoordPointer(2, GL10.GL_FLOAT, 0, buffer.mBuffer);
        }

        // now keep
        mTextureCoordBuffer = buffer;
        return true;
    }

    public TextureCoordBuffer getTextureCoordBuffer() {
        return mTextureCoordBuffer;
    }

    /**
     * @return the textureCoordArrayEnabled
     */
    public boolean isTextureCoordArrayEnabled() {
        return mTextureCoordArrayEnabled;
    }

    /**
     * @param textureCoordArrayEnabled the textureCoordArrayEnabled to set
     */
    public boolean setTextureCoordArrayEnabled(final boolean textureCoordArrayEnabled) {
        // diff check
        if (mTextureCoordArrayEnabled == textureCoordArrayEnabled) {
            return false;
        }

        // apply
        mTextureCoordArrayEnabled = textureCoordArrayEnabled;
        if (textureCoordArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }

        return true;
    }

    /**
     * @return the colorArrayEnabled
     */
    public boolean isColorArrayEnabled() {
        return mColorArrayEnabled;
    }

    /**
     * @param colorArrayEnabled the colorArrayEnabled to set
     */
    public boolean setColorArrayEnabled(final boolean colorArrayEnabled) {
        // diff check
        if (mColorArrayEnabled == colorArrayEnabled) {
            return false;
        }

        // apply
        mColorArrayEnabled = colorArrayEnabled;
        if (colorArrayEnabled) {
            mGL.glEnableClientState(GL10.GL_COLOR_ARRAY);
        } else {
            mGL.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }

        return true;
    }

    public boolean isAlphaTestEnabled() {
        return mAlphaTestEnabled;
    }

    public void setAlphaTestEnabled(final boolean alphaTestEnabled) {
        // diff check
        if (mAlphaTestEnabled == alphaTestEnabled) {
            return;
        }

        mAlphaTestEnabled = alphaTestEnabled;

        if (alphaTestEnabled) {
            mGL.glEnable(GL10.GL_ALPHA_TEST);
        } else {
            mGL.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    /**
     * @return the color
     */
    public GLColor getColor() {
        return mColor;
    }

    /**
     * @param color the color to set
     */
    public boolean setColor(final GLColor color) {
        // diff check
        if (mColor.equals(color)) {
            return false;
        }

        if (color == null) {
            mColor.setValues(1f, 1f, 1f, 1f);
        } else {
            mColor.setValues(color.r, color.g, color.b, color.a);
        }

        // apply
        mGL.glColor4f(mColor.r, mColor.g, mColor.b, mColor.a);

        return true;
    }

    /**
     * @param color the color to set
     */
    public boolean setColor(final float r, final float g, final float b, final float a) {
        // diff check
        if (mColor.equals(r, g, b, a)) {
            return false;
        }

        // apply
        mColor.setValues(r, g, b, a);
        mGL.glColor4f(mColor.r, mColor.g, mColor.b, mColor.a);

        return true;
    }

    public BlendFunc getDefaultBlendFunc() {
        return mDefaultBlendFunc;
    }

    public void setDefaultBlendFunc(final BlendFunc defaultBlendFunc) {
        mDefaultBlendFunc.set(defaultBlendFunc);
    }

    /**
     * @return the blendFunc
     */
    public BlendFunc getBlendFunc() {
        return mBlendFunc;
    }

    /**
     * @param blendFunc the blendFunc to set
     */
    public boolean setBlendFunc(final BlendFunc blendFunc) {
        // null check
        if (blendFunc == null) {
            if (!mBlendFunc.equals(mDefaultBlendFunc)) {
                // set to default
                mBlendFunc.set(mDefaultBlendFunc);
                // apply
                if (mBlendFunc.src_alpha < 0 || mBlendFunc.dst_alpha < 0) {
                    mGL.glBlendFunc(mBlendFunc.src, mBlendFunc.dst);
                } else {
                    GLES11Ext.glBlendFuncSeparateOES(mBlendFunc.src, mBlendFunc.dst, mBlendFunc.src_alpha, mBlendFunc.dst_alpha);
                }
                return true;
            } else {
                return false;
            }
            // diff check
        } else if (mBlendFunc.equals(blendFunc)) {
            return false;
        }

        // apply
        mBlendFunc.set(blendFunc);
        if (mBlendFunc.src_alpha < 0 || mBlendFunc.dst_alpha < 0) {
            mGL.glBlendFunc(blendFunc.src, blendFunc.dst);
        } else {
            GLES11Ext.glBlendFuncSeparateOES(blendFunc.src, blendFunc.dst, blendFunc.src_alpha, blendFunc.dst_alpha);
        }

        return true;
    }

    public int getError() {
        return mGL.glGetError();
    }

    public void clearErrors() {
        // clear the previous error(s), to make sure
        while (mGL.glGetError() != GL10.GL_NO_ERROR) {
        }
    }

    /**
     * @return the camera
     */
    // public Camera getCamera() {
    // return mCamera;
    // }

    /**
     * For internal use only
     * 
     * @hide
     * @param camera the camera to set
     */
    // public void setCamera(final Camera camera) {
    // mCamera = camera;
    // }

    public Maskable getMask() {
        return mMask;
    }

    /**
     * This does not work like it's supposed to yet :(
     * 
     * @param mask
     */
    @Deprecated
    public void setMask(final Maskable mask) {
        // diff check
        if (mMask != mask) {
            if (mMask != null) {
                // disable the previous mask
                mMask.disableMask();
            }

            if (mask != null) {
                // enable the new one
                mask.enableMask();
            }

            mMask = mask;
        }
    }

    public boolean isDepthTestEnabled() {
        return mDepthTestEnabled;
    }

    public void setDepthTestEnabled(final boolean depthTestEnabled) {
        // diff check
        if (mDepthTestEnabled == depthTestEnabled) {
            return;
        }

        mDepthTestEnabled = depthTestEnabled;

        if (depthTestEnabled) {
            mGL.glEnable(GL10.GL_DEPTH_TEST);
        } else {
            mGL.glDisable(GL10.GL_DEPTH_TEST);
        }
    }

    public boolean isScissorTestEnabled() {
        return mScissorTestEnabled;
    }

    public void setScissorTestEnabled(final boolean scissorEnabled) {
        // diff check
        if (mScissorTestEnabled == scissorEnabled) {
            return;
        }

        mScissorTestEnabled = scissorEnabled;

        if (scissorEnabled) {
            mGL.glEnable(GL10.GL_SCISSOR_TEST);
        } else {
            mGL.glDisable(GL10.GL_SCISSOR_TEST);
        }
    }

    public void setScissor(final int x, final int y, final int width, final int height) {
        mGL.glScissor(x, y, width, height);
        mScissor[0] = x;
        mScissor[1] = y;
        mScissor[2] = width;
        mScissor[3] = height;
    }

    public void setScissor(final int[] scissor) {
        setScissor(scissor[0], scissor[1], scissor[2], scissor[3]);
    }

    public void getScissor(final int[] scissor) {
        scissor[0] = mScissor[0];
        scissor[1] = mScissor[1];
        scissor[2] = mScissor[2];
        scissor[3] = mScissor[3];
    }

    public TextureManager getTextureManager() {
        return mTextureManager;
    }

    /**
     * For internal use only
     * 
     * @hide
     * @param textureManager
     */
    public void setTextureManager(final TextureManager textureManager) {
        mTextureManager = textureManager;
    }

    public Stage getStage() {
        return mStage;
    }
}
