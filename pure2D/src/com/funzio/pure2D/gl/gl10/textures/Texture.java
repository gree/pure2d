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
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public abstract class Texture {
    public static boolean LOG_ENABLED = true;
    public static final String TAG = Texture.class.getSimpleName();

    protected GLState mGLState;

    private int mMinFilter = GLES20.GL_NEAREST;
    private int mMagFilter = GLES20.GL_NEAREST;
    private int mRepeatS;
    private int mRepeatT;
    private boolean mHasMipmaps = false;

    public int mTextureID = 0; // note: valid texture id can be negative (on Kindle Fire)
    public float mCoordScaleX = 1;
    public float mCoordScaleY = 1;

    protected PointF mSize = new PointF(0, 0);
    protected Listener mListener;

    // expiration stuff
    protected int mExpirationTime = 0; // <=0 means no expiration
    protected int mIdleTime = 0;
    protected boolean mExpired = false;

    private int mTextureUnit;

    protected Texture(final GLState glState) {
        mGLState = glState;
    }

    protected Texture(final GLState glState, final Bitmap bitmap) {
        mGLState = glState;
        load(bitmap, 0, 0, 0);
    }

    protected Texture(final GLState glState, final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps) {
        mGLState = glState;
        load(bitmap, actualWidth, actualHeight, mipmaps);
    }

    /**
     * This MUST be executed on GL Thread.
     * 
     * @param bitmap
     * @param actualWidth
     * @param actualHeight
     * @param mipmaps
     */
    public void load(final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps) {
        load(bitmap, actualWidth, actualHeight, mipmaps, 0);
    }

    /**
     * This MUST be executed on GL Thread.
     * 
     * @param bitmap
     * @param actualWidth
     * @param actualHeight
     * @param mipmaps
     * @param textureUnit
     */
    public void load(final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps, final int textureUnit) {
        setBitmapSize(bitmap != null ? bitmap.getWidth() : actualWidth, bitmap != null ? bitmap.getHeight() : actualHeight, actualWidth, actualHeight);
        mTextureUnit = textureUnit;

        final int[] ids = new int[1];
        // clear the previous error(s), to make sure
        mGLState.clearErrors();
        GLES20.glGenTextures(1, ids, 0);
        mTextureID = ids[0];

        int error = mGLState.getError();
        if (LOG_ENABLED) {
            Log.v(TAG, String.format("load(%s, %d, %d); id: %d, error: %d", bitmap, actualWidth, actualHeight, mTextureID, error));
        }

        // error checking
        if (error == 0 && mTextureID != 0) {
            mGLState.bindTextureUnit(this, textureUnit);

            // load the bitmap into gl
            if (bitmap == null) {
                // create a blank texture
                final Bitmap bmp = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
                bmp.eraseColor(Color.WHITE);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
                bmp.recycle();
            } else {
                // upload bitmap
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                if (mipmaps > 0) {
                    // auto generate mipmaps
                    GLES20.glHint(GLES20.GL_GENERATE_MIPMAP_HINT, GLES20.GL_NICEST);
                    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
                    if (mGLState.getError() == 0) {
                        mHasMipmaps = true;
                    }
                }
            }

            // default filters
            if (mHasMipmaps) {
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_NEAREST);
            } else {
                setFilters(mMinFilter, mMagFilter);
            }

            // unexpire
            mExpired = false;
            mIdleTime = 0;
        } else {
            Log.e(TAG, "Failed to generate Texture: " + GLU.gluErrorString(error), new Exception());
            // TODO maybe throw an Exception here
        }

        // callback, regardless whether it's successful or not
        if (mListener != null) {
            mListener.onTextureLoad(this);
        }
    }

    protected void setBitmapSize(final int bitmapWidth, final int bitmapHeight, final int actualWidth, final int actualHeight) {
        mSize.x = actualWidth == 0 ? bitmapWidth : actualWidth;
        mSize.y = actualHeight == 0 ? bitmapHeight : actualHeight;
        // find the coordinates' scales
        mCoordScaleX = mSize.x / bitmapWidth;
        mCoordScaleY = mSize.y / bitmapHeight;
    }

    public PointF getSize() {
        return mSize;
    }

    public void setFilters(final int minFilter, final int magFilter) {
        mMinFilter = (mHasMipmaps ? GLES20.GL_LINEAR_MIPMAP_NEAREST : minFilter);
        mMagFilter = magFilter;

        if (mTextureID != 0) {
            mGLState.bindTextureUnit(this, mTextureUnit);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, mMinFilter);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, mMagFilter);
        }
    }

    /**
     * Set repeating mode
     * 
     * @param s can be GL_CLAMP_TO_EDGE or GL_REPEAT
     * @param t can be GL_CLAMP_TO_EDGE or GL_REPEAT
     */
    public void setRepeat(final int s, final int t) {
        mRepeatS = s;
        mRepeatT = t;

        if (mTextureID != 0) {
            mGLState.bindTextureUnit(this, mTextureUnit);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, mRepeatS);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, mRepeatT);
        }
    }

    // public void buildMipmaps() {
    // if (mGL instanceof GL11) {
    // mGL.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
    // GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    // }
    // }

    public void bind() {

        // if previously expired, auto reload it!
        if (mExpired && mTextureID == 0) {
            reload();

            // unexpire
            mExpired = false;
            mIdleTime = 0;
        }

        if (mTextureID != 0) {
            mGLState.bindTextureUnit(this, mTextureUnit);

            // no longer idle
            mIdleTime = 0;
        } else {
            // texture is not ready, notify the glstate
            mGLState.unbindTexture();
        }
    }

    // public void unbind() {
    // if (mTextureID != 0) {
    // // mGL.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    // mGLState.unbindTexture();
    // }
    // }

    public void unload() {
        if (LOG_ENABLED) {
            Log.v(TAG, "unload(): " + mTextureID);
        }

        if (mTextureID != 0) {
            // clean up
            final int[] ids = {
                mTextureID
            };
            mGLState.bindTextureUnit(this, mTextureUnit);
            GLES20.glDeleteTextures(1, ids, 0);
            mTextureID = 0;

            // now unbind me
            mGLState.unbindTexture();
        }
    }

    public int getTextureID() {
        return mTextureID;
    }

    public boolean isLoaded() {
        return mTextureID != 0;
    }

    public void reload(final GLState glState) {
        mGLState = glState;

        mTextureID = 0; // clear the previous id

        // reload now
        reload();
    }

    public abstract void reload();

    public boolean isPo2() {
        return Pure2DUtils.isPO2((int) mSize.x) && Pure2DUtils.isPO2((int) mSize.y);
    }

    /**
     * @hide For internal use only. Do not ever call this!
     * @param deltaTime
     */
    public void update(final int deltaTime) {
        if (!mExpired && mExpirationTime > 0) {

            // add more idle time
            mIdleTime += deltaTime;

            // expiration check
            if (mIdleTime >= mExpirationTime) {
                // expire it now!
                unload();

                // flag
                mExpired = true;
            }
        }
    }

    /**
     * Get the expiration time (in ms)
     * 
     * @return
     */
    public int getExpirationTime() {
        return mExpirationTime;
    }

    /**
     * Set how long (in ms) of idle/inactive time before this Texture should be expired and unloaded automatically.
     * 
     * @param expirationTime
     * @see TextureManager.#setExpirationCheckInterval(int)
     */
    public void setExpirationTime(final int expirationTime) {
        mExpirationTime = expirationTime;
    }

    /**
     * @return whether this Texture is expired or not
     */
    public boolean isExpired() {
        return mExpired;
    }

    @Override
    public String toString() {
        return "Texture {id: " + mTextureID + ", size: " + mSize.x + " x " + mSize.y + "}";
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onTextureLoad(Texture texture);
    }
}
