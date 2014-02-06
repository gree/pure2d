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

import android.graphics.PointF;
import android.opengl.GLU;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.textures.BufferTexture;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class FrameBuffer {
    private static final String TAG = FrameBuffer.class.getSimpleName();

    private GLState mGLState;
    private GL10 mGL;
    private GL11ExtensionPack mGL11Ex;
    private Texture mTexture;
    private int mFrameBuffer = 0;
    private int mDepthBuffer = 0;
    private int mOriginalBuffer = 0;
    private final float mWidth;
    private final float mHeight;
    private final boolean mDepthEnabled;

    private final int[] mScratch = new int[1];
    private int[] mOriginalViewport = new int[4];
    private float[] mOriginalProjection = new float[5];
    private boolean mBinded = false;
    private boolean mTextureAttached = false;
    private boolean mOriginalScissorEnabled = false;

    public FrameBuffer(final GLState glState, final float width, final float height, final boolean checkPo2) {
        this(glState, width, height, checkPo2, false);
    }

    public FrameBuffer(final GLState glState, final float width, final float height, final boolean checkPo2, final boolean depthEnabled) {
        mGLState = glState;
        mGL = glState.mGL;
        mGL11Ex = (GL11ExtensionPack) mGL;
        mDepthEnabled = depthEnabled;
        mWidth = width;
        mHeight = height;

        init();

        // auto create a new texture
        final int roundedWidth = Math.round(width);
        final int roundedHeight = Math.round(height);
        final BufferTexture texture = mGLState.getTextureManager().createBufferTexture(roundedWidth, roundedHeight, checkPo2);
        // texture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR); // better output, but don't set it here
        if (!attachTexture(texture) && !texture.isPo2()) {
            // NOTE: most of 2.2 devices have problem with NPOT texture (even thought it's supported) when attached to a FrameBuffer
            // so this is a work-around
            texture.unload();
            // force the size to be PO2
            texture.load(Pure2DUtils.getNextPO2(roundedWidth), Pure2DUtils.getNextPO2(roundedHeight), roundedWidth, roundedHeight, 0);
            // re-attach it
            attachTexture(texture);
        }
    }

    private void init() {
        mGLState.clearErrors();

        // this is a must for some certain devices such as Samsung S2
        mGLState.unbindTexture();

        // create a new frame buffer
        mGL11Ex.glGenFramebuffersOES(1, mScratch, 0);
        mFrameBuffer = mScratch[0];

        // error checking
        int error = mGL.glGetError();
        Log.v(TAG, "init(); id: " + mFrameBuffer + ", error: " + error + " - " + GLU.gluErrorString(error));
    }

    public boolean attachTexture(final Texture texture) {
        mTexture = texture;

        // get the original buffer
        mOriginalBuffer = mGLState.getFrameBuffer();

        // bind frame buffer temporarily
        mGLState.bindFrameBuffer(mFrameBuffer);

        // attach the texture
        mGL11Ex.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, mTexture.getTextureID(), 0);

        if (mDepthEnabled) {
            // create depth buffer
            mGL11Ex.glGenRenderbuffersOES(1, mScratch, 0);
            mDepthBuffer = mScratch[0];
            mGL11Ex.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);

            // Depth enable
            // A renderbuffer are just objects which are used to support offscreen rendering,
            // often for sections of the framebuffer which don't have a texture format associated with them such as the stencil or depth buffer.
            mGL11Ex.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_DEPTH_COMPONENT24, Math.round(mWidth), Math.round(mHeight));
            mGL11Ex.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES, GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);

            // stencil enable
            // mGL11Ex.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_STENCIL_INDEX8_OES, mWidth, mHeight);
            // mGL11Ex.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_STENCIL_ATTACHMENT_OES, GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);
        }

        final int status = mGL11Ex.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
            mTextureAttached = false;
            Log.e(TAG, "Failed to attach Texture: " + getStatusString(status) + "\n" + texture.toString(), new Exception());
        } else {
            mTextureAttached = true;
            // make sure to clear all the pixels initially. Needed for some devices such as Nexus 7 (Jelly Bean)
            mGL.glClearColor(0f, 0f, 0f, 0f);
            mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        }

        // restore back to the original buffer
        mGLState.bindFrameBuffer(mOriginalBuffer);

        return mTextureAttached;
    }

    public boolean verifyGLState(final GLState glState) {
        return glState.mGL == mGL;
    }

    /**
     * Bind this frame buffer
     */
    public boolean bind(final int projection) {
        Log.v(TAG, "bind(): " + mFrameBuffer + ", " + projection);
        if (!mTextureAttached || mBinded) {
            return false;
        }

        // flag
        mBinded = true;

        mOriginalScissorEnabled = mGLState.isScissorTestEnabled();
        // get the original buffer
        mOriginalBuffer = mGLState.getFrameBuffer();
        // back up the viewport
        mGLState.getViewport(mOriginalViewport);
        // fall-back solution: back up projection instead of glPushMatrix() which can cause Stack-Overflow
        mGLState.getProjection(mOriginalProjection);

        // no scissor test
        mGLState.setScissorTestEnabled(false);

        // bind me
        mGLState.bindFrameBuffer(mFrameBuffer);

        // Select the projection matrix
        mGL.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        mGL.glLoadIdentity();

        // set new viewport
        mGLState.setViewport(0, 0, Math.round(mWidth) - 1, Math.round(mHeight) - 1); // NOTE: there is 1 pixel off in relation to the scene, so we need -1
        // set new projection matrix
        mGLState.setProjection(projection, 0, mWidth - 1, 0, mHeight - 1);

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPushMatrix();
        mGL.glLoadIdentity();

        // toggle depth test
        // mGLState.setDepthTestEnabled(mDepthEnabled);

        return true;
    }

    /**
     * Unbind this frame buffer and switch back to the original buffer
     */
    public boolean unbind() {
        Log.v(TAG, "unbind(): " + mFrameBuffer);
        if (!mTextureAttached || !mBinded) {
            return false;
        }

        // unflag
        mBinded = false;

        // back to the previous buffer
        mGLState.bindFrameBuffer(mOriginalBuffer);

        mGL.glMatrixMode(GL10.GL_PROJECTION);
        mGL.glLoadIdentity();
        // Use mOriginalProjection instead of glPopMatrix()
        mGLState.setProjection(mOriginalProjection);
        // Restore the view port
        mGLState.setViewport(mOriginalViewport);

        // re-enable scissor test if any
        mGLState.setScissorTestEnabled(mOriginalScissorEnabled);

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPopMatrix();

        return true;
    }

    public boolean isBinded() {
        return mBinded;
    }

    public void clear() {
        Log.v(TAG, "clear(): " + mFrameBuffer);
        if (!mBinded) {
            return;
        }

        mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Unbind and delete the frame and depth buffers
     */
    public void unload() {
        Log.v(TAG, "unload(): " + mFrameBuffer);
        unbind();

        // delete depth buffer
        if (mDepthBuffer > 0) {
            int[] buffers = {
                mDepthBuffer
            };
            mGL11Ex.glDeleteRenderbuffersOES(1, buffers, 0);
            mDepthBuffer = 0;
        }

        // delete frame buffer
        if (mFrameBuffer > 0) {
            int[] buffers = {
                mFrameBuffer
            };
            mGL11Ex.glDeleteFramebuffersOES(1, buffers, 0);
            mFrameBuffer = 0;
        }

        // this is a must for some certain devices such as Samsung S2
        mGLState.unbindTexture();
    }

    public Texture getTexture() {
        return mTexture;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public boolean hasSize(final PointF size) {
        return mWidth == size.x && mHeight == size.y;
    }

    public static boolean isSupported() {
        return Pure2D.GL_FBO_SUPPORTED;
    }

    public static String getStatusString(final int status) {
        String msg = "UNKNOWN STATUS: " + status;
        if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES";
        }
        return msg;
    }

}
