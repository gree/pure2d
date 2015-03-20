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

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.graphics.Bitmap;
import android.graphics.PointF;
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
    protected GL10 mGL;

    private int mMinFilter = GL10.GL_NEAREST;
    private int mMagFilter = GL10.GL_NEAREST;
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

    protected Texture(final GLState glState) {
        mGLState = glState;
        mGL = mGLState.mGL;
    }

    protected Texture(final GLState glState, final Bitmap bitmap) {
        mGLState = glState;
        mGL = mGLState.mGL;
        load(bitmap, 0, 0, 0);
    }

    protected Texture(final GLState glState, final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps) {
        mGLState = glState;
        mGL = mGLState.mGL;
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
        setBitmapSize(bitmap != null ? bitmap.getWidth() : actualWidth, bitmap != null ? bitmap.getHeight() : actualHeight, actualWidth, actualHeight);

        final int[] ids = new int[1];
        // clear the previous error(s), to make sure
        mGLState.clearErrors();
        mGL.glGenTextures(1, ids, 0);
        mTextureID = ids[0];

        int error = mGL.glGetError();
        if (LOG_ENABLED) {
            Log.v(TAG, String.format("load(%s, %d, %d); id: %d, error: %d", bitmap, actualWidth, actualHeight, mTextureID, error));
        }

        // error checking
        if (error == 0 && mTextureID != 0) {
            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
            mGLState.bindTexture(this);

            // load the bitmap into gl
            if (bitmap == null) {
                // create a blank texture
                mGL.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, actualWidth, actualHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
            } else {

                if (mipmaps > 0 && mGL instanceof GL11) {
                    // auto generate mipmaps
                    mGL.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
                    if (mGL.glGetError() == 0) {
                        mHasMipmaps = true;
                    }
                }
                // upload bitmap
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            }

            // default filters
            if (mHasMipmaps) {
                mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
            } else {
                setFilters(mMinFilter, mMagFilter);
            }

            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            // mGLState.unbindTexture();

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
        mMinFilter = minFilter;
        mMagFilter = magFilter;

        if (mTextureID != 0) {
            mGLState.bindTexture(this);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
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
            mGLState.bindTexture(this);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, mRepeatS);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, mRepeatT);
        }
    }

    // public void buildMipmaps() {
    // if (mGL instanceof GL11) {
    // mGL.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
    // GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
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
            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
            mGLState.bindTexture(this);

            // no longer idle
            mIdleTime = 0;
        } else {
            // texture is not ready, notify the glstate
            mGLState.unbindTexture();
        }
    }

    // public void unbind() {
    // if (mTextureID != 0) {
    // // mGL.glBindTexture(GL10.GL_TEXTURE_2D, 0);
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
            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
            mGLState.bindTexture(this);
            mGL.glDeleteTextures(1, ids, 0);
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

        // diff check
        if (mGL != mGLState.mGL) {
            mGL = mGLState.mGL;
            mTextureID = 0; // clear the previous id

            // reload now
            reload();
        }
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
