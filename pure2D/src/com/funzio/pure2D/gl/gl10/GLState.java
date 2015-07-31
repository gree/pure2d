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

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Maskable;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.Stage;
import com.funzio.pure2D.geom.Matrix4;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl20.DefaultAlias;
import com.funzio.pure2D.gl.gl20.DefaultShaderProgram;
import com.funzio.pure2D.gl.gl20.ShaderProgram;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 * @category This class is used to manage and also limit the number of JNI calls, for optimization purpose.
 */
public class GLState {
    private static final String TAG = GLState.class.getSimpleName();

    public GL10 mGL;
    private Stage mStage;

    // program
    private ShaderProgram mRequestedProgram = null;      // GLState.useShaderProgram()
    private ShaderProgram mBoundProgram = null;         // the actual program doing the work
    private int mBoundProgramVariant = -1;              //
    private ShaderProgram mDefaultProgram;

    // texture
    private Texture mTexture = null;
    private boolean mTextureEnabled = false;
    private TextureCoordBuffer mTextureCoordBuffer;
    public boolean mTextureCoordArrayEnabled = false;

    // array toggles
    private VertexBuffer mVertexBuffer;
    private boolean mVertexArrayEnabled = false;
    private boolean mDepthTestEnabled = false;
    private boolean mScissorTestEnabled = false;

    // colors
    private ColorBuffer mColorBuffer;
    public boolean mColorArrayEnabled = false;
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

        useShaderProgram(getDefaultShaderProgram());

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

        mViewMatrixStackPointer = 0;
        mProjectionMatrixStackPointer = 0;

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


    /*
    MATRIX SUPPORT
     */
    private boolean mIsProjection = false;
    private final Matrix4 mProjectionMatrix = new Matrix4();
    private final Matrix4 mViewMatrix = new Matrix4();
    private final Matrix4 mWorkMatrix = new Matrix4();

    public void matrixProjectionMode(boolean isProjection) {
        mIsProjection = isProjection;
    }

    private List<Matrix4> mViewMatrixStack = new ArrayList<Matrix4>();
    private int mViewMatrixStackPointer = 0;        // points at the next available slot in the stack
    private List<Matrix4> mProjectionMatrixStack = new ArrayList<Matrix4>();
    private int mProjectionMatrixStackPointer = 0;        // points at the next available slot in the stack
    public void matrixPush() {
        if (mIsProjection) {
            mProjectionMatrixStackPointer++;
if (mProjectionMatrixStackPointer > 100) {
    Log.e(TAG, "PMATRIX STACK OVERFLOW "+mProjectionMatrixStackPointer, new Exception());
    mProjectionMatrixStackPointer = 100;
}
            if (mProjectionMatrixStackPointer > mProjectionMatrixStack.size()) {
                mProjectionMatrixStack.add(new Matrix4());
            }
            mProjectionMatrixStack.get(mProjectionMatrixStackPointer - 1).set(mProjectionMatrix);
        } else {
            mViewMatrixStackPointer++;
if (mViewMatrixStackPointer > 100) {
    Log.e(TAG, "MATRIX STACK OVERFLOW " + mViewMatrixStackPointer, new Exception());
    mViewMatrixStackPointer = 100;
}
            if (mViewMatrixStackPointer > mViewMatrixStack.size()) {
                mViewMatrixStack.add(new Matrix4());
            }
            mViewMatrixStack.get(mViewMatrixStackPointer - 1).set(mViewMatrix);
        }
    }

    public void matrixPop() {
        if (mIsProjection) {
            if (mProjectionMatrixStackPointer > 0) mProjectionMatrixStackPointer--;
            else Log.e(TAG, "PMATRIX STACK UNDERFLOW", new Exception());
            mProjectionMatrix.set(mProjectionMatrixStack.get(mProjectionMatrixStackPointer));
        } else {
            if (mViewMatrixStackPointer > 0) mViewMatrixStackPointer--;
            else Log.e(TAG, "MATRIX STACK UNDERFLOW", new Exception());
            mViewMatrix.set(mViewMatrixStack.get(mViewMatrixStackPointer));
        }
    }

    public void matrixLoadIdentity() {
        if (mIsProjection) {
            mProjectionMatrix.identity();
        } else {
            mViewMatrix.identity();
        }
    }

    public void matrixFrustum(final float left, final float right, final float bottom, final float top, final float zNear, final float zFar) {
        try {
            mWorkMatrix.frustum(left, right, bottom, top, zNear, zFar);
        } catch (Exception e) {
    }
        if (mIsProjection) {
            mProjectionMatrix.multiply(mWorkMatrix);
        } else {
            mViewMatrix.multiply(mWorkMatrix);
        }
    }

    public void matrixOrtho(final float left, final float right, final float bottom, final float top) {
        try {
            mWorkMatrix.ortho(left, right, bottom, top, -1f, 1f);
        } catch (Exception e) {
    }
        if (mIsProjection) {
            mProjectionMatrix.multiply(mWorkMatrix);
        } else {
            mViewMatrix.multiply(mWorkMatrix);
        }
    }

    public void matrixLookAt(final float eyeX, final float eyeY, final float eyeZ, final float centerX, final float centerY, final float centerZ, final float upX, final float upY, final float upZ) {
        mWorkMatrix.lookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        if (mIsProjection) {
            mProjectionMatrix.multiplyRev(mWorkMatrix);
        } else {
            mViewMatrix.multiplyRev(mWorkMatrix);
        }
    }

    public void matrixMultiply(Matrix4 mat) {
        if (mIsProjection) {
            mProjectionMatrix.multiply(mat);
        } else {
            mViewMatrix.multiply(mat);
        }
    }

    public void matrixMultiply(float[] mat) {
        if (mIsProjection) {
            mProjectionMatrix.multiply(mat);
        } else {
            mViewMatrix.multiply(mat);
        }
    }

    public void matrixLoad(Matrix4 mat) {
        if (mIsProjection) {
            mProjectionMatrix.set(mat);
        } else {
            mViewMatrix.set(mat);
        }
    }

    public void matrixTranslate(final float x, final float y, final float z) {
        if (mIsProjection) {
            mProjectionMatrix.translate(x, y, z);
        } else {
            mViewMatrix.translate(x, y, z);
        }
    }

    public void matrixScale(final float x, final float y, final float z) {
        if (mIsProjection) {
            mProjectionMatrix.scale(x, y, z);
        } else {
            mViewMatrix.scale(x, y, z);
        }
    }

    public void matrixRotate(final float r, final float x, final float y, final float z) {
        if (mIsProjection) {
            mProjectionMatrix.rotate(r, x, y, z);
        } else {
            mViewMatrix.rotate(r, x, y, z);
        }
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
/*MATRIX
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
 */
/*
        matrixProjectionMode(false);
        if (projection == Scene.PROJECTION_PERSPECTIVE) {
            final float width = right - left + 1;
            final float height = top - bottom + 1;
            matrixLookAt(0.0f, 0.0f, height, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f); // always based on Screen-Y
        } else if (projection == Scene.AXIS_TOP_LEFT) {
            matrixLookAt(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        } else if (projection == Scene.AXIS_BOTTOM_LEFT) {
            matrixLookAt(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
        } else {
            Log.e(TAG, "Unknown Projection mode: " + projection, new Exception());
        }
        matrixProjectionMode(true);

        if (projection == Scene.PROJECTION_PERSPECTIVE) {
//            final float width = right - left + 1;
//            final float height = top - bottom + 1;
//            mProjectionMatrix.frustum(left, right, bottom, top, 0.001f, 10f);
//            mProjectionMatrix.translate(-width * 0.5f, -height * 0.5f, 0);
            try {
            matrixFrustum(left, right, bottom, top, 10f);
            } catch (Exception e) {
            }
        } else if (projection == Scene.AXIS_TOP_LEFT) {
            try {
            matrixOrtho(left, right, top, bottom);
            } catch (Exception e) {
            }
//            mProjectionMatrix.ortho(left, right, top, bottom, 1f, 2f);
        } else if (projection == Scene.AXIS_BOTTOM_LEFT) {
            try {
            matrixOrtho(left, right, bottom, top);
            } catch (Exception e) {
            }
//            mProjectionMatrix.ortho(left, right, bottom, top, 1f, 2f);
        } else {
            Log.e(TAG, "Unknown Projection mode: " + projection, new Exception());
        }
*/
//        if (projection == Scene.PROJECTION_PERSPECTIVE) {
//            final float width = right - left + 1;
//            final float height = top - bottom + 1;
//            mProjectionMatrix.frustum(left, right, bottom, top, 0.001f, 10f);
//            mViewMatrix.lookAt(0, height, 0, 0, 0, 0, 0, 1f, 0);
//            mViewMatrix.translate(-width * 0.5f, -height * 0.5f, 0);
//        } else if (projection == Scene.AXIS_TOP_LEFT) {
//            mViewMatrix.lookAt(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
//            mProjectionMatrix.ortho(left, right, top, bottom, 1f, 2f);
//        } else if (projection == Scene.AXIS_BOTTOM_LEFT) {
//            mViewMatrix.lookAt(0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
//            mProjectionMatrix.ortho(left, right, bottom, top, 1f, 2f);
//        } else {
//            Log.e(TAG, "Unknown Projection mode: " + projection, new Exception());
//        }
        if (projection == Scene.PROJECTION_PERSPECTIVE) {
            final float width = right - left + 1;
            final float height = top - bottom + 1;
//            GLU.gluPerspective(mGL, Pure2D.GL_PERSPECTIVE_FOVY, width / height, 0.001f, Math.max(width, height));
            final float zNear = 0.001f;
            final float zFar = Math.max(width, height);
            final float fovy = Pure2D.GL_PERSPECTIVE_FOVY;
            final float aspect = width / height;
            final float ntop = zNear * (float) Math.tan(fovy * (Math.PI / 360.0));
            final float nbottom = -ntop;
            final float nleft = nbottom * aspect;
            final float nright = ntop * aspect;
            matrixFrustum(nleft, nright, nbottom, ntop, zNear, zFar);
//            GLU.gluLookAt(mGL, 0, 0, height, 0, 0, 0, 0, 1, 0); // always based on Screen-Y
            matrixLookAt(0, 0, height, 0, 0, 0, 0, 1, 0);
//            mGL.glTranslatef(-width * 0.5f, -height * 0.5f, 0);
            matrixTranslate(-width * 0.5f, -height * 0.5f, 0);
        } else if (projection == Scene.AXIS_TOP_LEFT) {
            matrixOrtho(left, right, top, bottom);
        } else if (projection == Scene.AXIS_BOTTOM_LEFT) {
            matrixOrtho(left, right, bottom, top);
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
        GLES20.glViewport(x, y, width, height);
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
        GLES20.glLineWidth(width);
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
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);

        return true;
    }

    public int getFrameBuffer() {
        return mFrameBuffer;
    }

    // public boolean bindTexture(final Texture texture) {
    // return bindTexture(texture, GLES20.GL_TEXTURE0);
    // }

    public boolean bindTextureUnit(final Texture texture, final int textureUnit) {  // call before bindTexture: may change the results
        // diff check
//-BAS
//        if (mTexture == texture) {
//            return false;
//        }

        // make sure it's enabled
        setTextureEnabled(true);

        // bind to gl
        mTexture = texture;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.mTextureID);

        return true;
    }

    public boolean setupAlternateTextureUnit(final Texture texture, final int textureUnit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.mTextureID);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 0);

        return true;
    }

    public boolean unbindTexture() {
        // diff check
        if (mTexture == null) {
            return false;
        }

        // unbind to gl
        mTexture = null;

        // make sure it's disabled
        setTextureEnabled(false);
        return true;
    }

    public Texture getTexture() {
        return mTexture;
    }

//    private final HashSet<Integer> mEnabledVertexAttribArrays = new HashSet<Integer>();
//
//    public boolean setVertexAttribArrayEnabled(final int index, final boolean enabled) {
//        final Integer indexInteger = Integer.valueOf(index);
//        final boolean wasEnabled = mEnabledVertexAttribArrays.contains(indexInteger);
//        if (enabled == wasEnabled) {
//            return false;
//        }
//
//        if (enabled) {
//            GLES20.glEnableVertexAttribArray(index);
//            mEnabledVertexAttribArrays.add(indexInteger);
//        } else {
//            GLES20.glDisableVertexAttribArray(index);
//            mEnabledVertexAttribArrays.remove(indexInteger);
//        }
//
//        return true;
//    }
//
//    public boolean setVertexAttribArray(final int index, final int size, final VertexBuffer buffer) {
//        final boolean same = mVertexBuffer instanceof QuadBuffer && buffer instanceof QuadBuffer && QuadBuffer.compare((QuadBuffer) mVertexBuffer, (QuadBuffer) buffer);
//        if (buffer != null && !same) {
//            GLES20.glVertexAttribPointer(index, size, GLES20.GL_FLOAT, false, size, buffer.mBuffer);
//        }
//
//        mVertexBuffer = buffer;
//        return true;
//    }

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
        return true;
    }

    public boolean setVertexBuffer(final VertexBuffer buffer) {
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

        return true;
    }

    public boolean setTextureCoordBuffer(final TextureCoordBuffer buffer) {
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

        return true;
    }

    public boolean setColorBuffer(final ColorBuffer buffer) {
        mColorBuffer = buffer;
        return true;
    }

    public ColorBuffer getColorBuffer() {
        return mColorBuffer;
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

        return true;
    }

    public BlendFunc getDefaultBlendFunc() {
        return mDefaultBlendFunc;
    }

    public void setDefaultBlendFunc(final BlendFunc defaultBlendFunc) {
        mDefaultBlendFunc.set(defaultBlendFunc);
//        setBlendFunc(null);
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
                    GLES20.glBlendFunc(mBlendFunc.src, mBlendFunc.dst);
                } else {
                    GLES20.glBlendFuncSeparate(mBlendFunc.src, mBlendFunc.dst, mBlendFunc.src_alpha, mBlendFunc.dst_alpha);
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
            GLES20.glBlendFunc(blendFunc.src, blendFunc.dst);
        } else {
            GLES20.glBlendFuncSeparate(blendFunc.src, blendFunc.dst, blendFunc.src_alpha, blendFunc.dst_alpha);
        }

        return true;
    }

    public void drawArrays(int mode, int first, int count) {
/*
        final ShaderProgram program = getShaderProgram();
        if (!bindTransformMatrix(program)) return;
        if (!bindVertices(program)) return;
        bindTexture(program);
        bindColor(program);
        bindConstColor(program);
*/
        GLES20.glDrawArrays(mode, first, count);
    }

    public void drawElements(int mode, int count, int type, java.nio.Buffer indices) {
/*
        final ShaderProgram program = getShaderProgram();
        if (!bindTransformMatrix(program)) return;
        if (!bindVertices(program)) return;
        bindTexture(program);
        bindColor(program);
        bindConstColor(program);
*/
        GLES20.glDrawElements(mode, count, type, indices);
    }


    public int getError() {
        return GLES20.glGetError();
    }

    public void clearErrors() {
        // clear the previous error(s), to make sure
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
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
//-BAS
//        if (mDepthTestEnabled == depthTestEnabled) {
//            return;
//        }

        mDepthTestEnabled = depthTestEnabled;

        if (depthTestEnabled) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        } else {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
    }

    public boolean isScissorTestEnabled() {
        return mScissorTestEnabled;
    }

    public void setScissorTestEnabled(final boolean scissorEnabled) {
        // diff check
//-BAS
//        if (mScissorTestEnabled == scissorEnabled) {
//            return;
//        }

        mScissorTestEnabled = scissorEnabled;

        if (scissorEnabled) {
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        } else {
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }
    }

    public void setScissor(final int x, final int y, final int width, final int height) {
        GLES20.glScissor(x, y, width, height);
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

    public Matrix4 getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public Matrix4 getViewMatrix() {
        return mViewMatrix;
    }

    public ShaderProgram getShaderProgram() {
        return mRequestedProgram;
    }

    public ShaderProgram getDefaultShaderProgram() {
        if (mDefaultProgram == null) {
            mDefaultProgram = new DefaultShaderProgram();
        }

        return mDefaultProgram;
    }

    public void useShaderProgram(final ShaderProgram program) { // call before bindShaderProgram()
        if (program == null) {
            // Continue to use old one
            return;
        }

        if (!program.isLoaded()) {
            program.load();
            if (!program.isLoaded()) {
                Log.w(TAG, "Program failed to load.");
                return;
            }
        }

        mRequestedProgram = program;
    }

    public void bindShaderProgram() {
        // adjust for the actual program, based on texture, colors, etc
        int variant = mRequestedProgram.getVariantIdx(this);

        if ((mBoundProgramVariant == variant) && (mBoundProgram == mRequestedProgram)) return;

        if (mBoundProgram != null) mBoundProgram.unbind();

        mBoundProgram = mRequestedProgram;
        mBoundProgramVariant = variant;

        mBoundProgram.bind(mBoundProgramVariant);
    }

    public int getUniformLocation(String uname) {
        return mBoundProgram.getUniformLocation(mBoundProgramVariant, uname);
    }

    public int getAttribLocation(String uname) {        // call after bindShaderProgram()
        return mBoundProgram.getAttribLocation(mBoundProgramVariant, uname);
    }

    public boolean bindColor() {
        final int uniform = mBoundProgram.getUniformLocation(mBoundProgramVariant, DefaultAlias.UNIFORM_VEC4_COLOR);
        final int attribute = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC4_VERTEXCOLOR);

        if (uniform >= 0) {
            final GLColor color = mColor;

            if (color != null) {
                GLES20.glUniform4f(uniform, color.r, color.g, color.b, color.a);
            } else {
                GLES20.glUniform4f(uniform, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }

        if (attribute >= 0) {
            final ColorBuffer colorBuffer = mColorBuffer;

                if ((colorBuffer != null) && mColorArrayEnabled) {
                final Buffer data = colorBuffer.mBuffer;
                data.position(0);

                GLES20.glVertexAttribPointer(attribute, 4, GLES20.GL_FLOAT, false, 0, data);
                GLES20.glEnableVertexAttribArray(attribute);
            } else {
                GLES20.glVertexAttrib4f(attribute, 1.0f, 1.0f, 1.0f, 1.0f);
                GLES20.glDisableVertexAttribArray(attribute);
            }
        }

        return true;
    }

    private Matrix4 mMVPMatrix = new Matrix4();
    public boolean bindTransformMatrix() {
        final int uniform = mBoundProgram.getUniformLocation(mBoundProgramVariant, DefaultAlias.UNIFORM_MAT4_TRANSFORM);
        if (uniform < 0) {
            Log.w(TAG, "Could not bind transform matrix; ShaderProgram is missing alias: " + DefaultAlias.UNIFORM_MAT4_TRANSFORM);
            return false;
        }

        final Matrix4 proj = getProjectionMatrix();
        final Matrix4 view = getViewMatrix();
        final Matrix4 mvp = mMVPMatrix;
        mvp.identity();

        mvp.multiply(view); // V * M
        mvp.multiply(proj); // P * V * M

        GLES20.glUniformMatrix4fv(uniform, 1, false, mvp.values, 0);

        return true;
    }

    public boolean bindTexture() {
        final int a_TexCoords = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC2_TEXCOORDS);
        final int u_Texture = mBoundProgram.getUniformLocation(mBoundProgramVariant, DefaultAlias.UNIFORM_SAMPLER2D_TEXTURE);
        if (a_TexCoords < 0 || u_Texture < 0) {
            if (a_TexCoords >= 0) {
                GLES20.glVertexAttrib2f(a_TexCoords, 0.5f, 0.5f);
                GLES20.glDisableVertexAttribArray(a_TexCoords);
            }
            return false;
        }

        final Texture t = mTexture;

        if ((t != null) && (mTextureCoordBuffer != null) && mTextureCoordArrayEnabled) {
            bindTextureUnit(t, 0); // TODO: Define texture unit values as constants somewhere
            GLES20.glUniform1i(u_Texture, 0);

            TextureCoordBuffer tcb = mTextureCoordBuffer;
            final Buffer texCoordBuffer = tcb.mBuffer;
            texCoordBuffer.position(0);
            GLES20.glVertexAttribPointer(a_TexCoords, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
            GLES20.glEnableVertexAttribArray(a_TexCoords);
        } else {
            GLES20.glUniform1i(u_Texture, 0); // TODO: Define texture unit values as constants somewhere

            GLES20.glVertexAttrib2f(a_TexCoords, 0.5f, 0.5f);
            GLES20.glDisableVertexAttribArray(a_TexCoords);
        }

        return true;
    }

    public boolean bindVertices() {
        final int a_Position = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC2_POSITION);
        if (a_Position < 0) {
            Log.w(TAG, "Could not bind vertices; ShaderProgram is missing alias: " + DefaultAlias.ATTRIB_VEC2_POSITION);
            return false;
        }

        final Buffer vertexBuffer = mVertexBuffer.mBuffer;
        vertexBuffer.position(0);

        GLES20.glVertexAttribPointer(a_Position, mVertexBuffer.mVertexPointerSize, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(a_Position);
        return true;
    }

    public void unbind() {
        final int a_Position = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC2_POSITION);
        final int a_TexCoords = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC2_TEXCOORDS);
        final int attribute = mBoundProgram.getAttribLocation(mBoundProgramVariant, DefaultAlias.ATTRIB_VEC4_VERTEXCOLOR);
        GLES20.glDisableVertexAttribArray(a_Position);
        if (a_TexCoords >= 0) GLES20.glDisableVertexAttribArray(a_TexCoords);
        if (attribute >= 0) GLES20.glDisableVertexAttribArray(attribute);
    }
}
