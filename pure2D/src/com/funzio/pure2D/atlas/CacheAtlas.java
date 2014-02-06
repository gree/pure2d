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
package com.funzio.pure2D.atlas;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animation.PlayableObject;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.BufferTexture;
import com.funzio.pure2D.utils.RectPacker;

/**
 * @author long
 */
public class CacheAtlas extends Atlas {

    public static final String TAG = CacheAtlas.class.getSimpleName();

    private GLState mGLState;
    private BufferTexture mTexture;
    private FrameBuffer mFrameBuffer;

    private RectPacker mPacker;
    private PlayableObject mTarget;

    public CacheAtlas(final GLState glState, final PlayableObject target, final int maxWidth) {
        Log.v(TAG, "CacheAtlas()");

        mGLState = glState;
        mTarget = target;

        initBuffer(maxWidth <= 0 ? Pure2D.GL_MAX_TEXTURE_SIZE : maxWidth);
        generateFrames();
    }

    /**
     * Creates a buffer texture to bind to the frame buffer
     */
    private void initBuffer(final int maxWidth) {
        mPacker = new RectPacker(maxWidth, !Pure2D.GL_NPOT_TEXTURE_SUPPORTED); // !Pure2D.GL_NPOT_TEXTURE_SUPPORTED
        mPacker.setQuickMode(true);
        final int frames = mTarget.getNumFrames();
        for (int i = 0; i < frames; i++) {
            final RectF frameRect = mTarget.getFrameRect(i);
            mPacker.occupy(Math.round(frameRect.width()), Math.round(frameRect.height()));
        }

        // update the size
        mWidth = mPacker.getWidth();
        mHeight = mPacker.getHeight();

        // Log.v(TAG, String.format("initBuffer(%d, %d)", mWidth, mHeight));

        // create a new texture
        mFrameBuffer = new FrameBuffer(mGLState, mWidth, mHeight, false);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();
    }

    /**
     * Render and generate the frames
     */
    private void generateFrames() {
        mFrameBuffer.bind(Scene.AXIS_TOP_LEFT); // invert

        // debug: draw bg
        // final Rectangular debugRect = new Rectangular();
        // debugRect.setSize(mWidth, mHeight);
        // debugRect.setColor(new GLColor(0, 0.3f, 0, 1f));
        // debugRect.draw(mGLState);

        final RectF posRect = new RectF();
        final int frames = mTarget.getNumFrames();
        for (int i = 0; i < frames; i++) {

            // prepare to draw the frame
            mTarget.stopAt(i);
            posRect.set(mPacker.getRect(i));
            final RectF frameRect = mTarget.getFrameRect(i);
            mTarget.setOrigin(frameRect.left, frameRect.top);

            // create frame
            final AtlasFrame frame = new AtlasFrame(this, i, "", posRect);
            frame.mOffset = new PointF(frameRect.left, frameRect.top);
            frame.setTexture(mTexture);
            addFrame(frame);

            if (Math.round(frameRect.width()) == posRect.width()) {
                mTarget.setRotation(0);
                mTarget.setPosition(posRect.left, mHeight - (posRect.top + posRect.height())); // top to bottom
                mTarget.draw(mGLState);
            } else {
                // rotate 90 CCW
                mTarget.setRotation(90);
                mTarget.setPosition(posRect.left + frameRect.height(), mHeight - (posRect.top + posRect.height())); // top to bottom
                mTarget.draw(mGLState);

                frame.rotateCW();
            }
        }

        // debug: draw dots
        // final int pixelWidth = 2;
        // final Rectangular r = new Rectangular();
        // r.setSize(pixelWidth, pixelWidth);
        // final TreeSet<Point> hots = mPacker.getHotPoints();
        // for (Point p : hots) {
        // r.setPosition(p.x, mHeight - p.y - pixelWidth);
        // r.draw(mGLState);
        // }

        // done
        mFrameBuffer.unbind();
    }

    /**
     * Only call this when GLSurface re-created
     */
    public void reload() {
        if (mTexture == null) {
            return;
        }

        // remove the old texture
        mGLState.getTextureManager().removeTexture(mTexture);

        // create a new texture
        mFrameBuffer = new FrameBuffer(mGLState, mWidth, mHeight, false);
        mTexture = (BufferTexture) mFrameBuffer.getTexture();

        // re-generate the frames
        getMasterFrameSet().removeAllFrames();
        generateFrames();
    }

    public BufferTexture getTexture() {
        return mTexture;
    }

    public FrameBuffer getFrameBuffer() {
        return mFrameBuffer;
    }
}
