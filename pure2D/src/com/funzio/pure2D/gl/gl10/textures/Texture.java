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

/**
 * @author long
 */
public abstract class Texture {
    public static final String TAG = Texture.class.getSimpleName();

    private GLState mGLState;
    private GL10 mGL;
    private int mMinFilter;
    private int mMagFilter;
    private int mRepeatS;
    private int mRepeatT;
    private boolean mHasMipmaps = false;

    public int mTextureID = 0; // note: valid texture id can be negative (on Kindle Fire)
    public float mCoordScaleX = 1;
    public float mCoordScaleY = 1;

    protected PointF mSize = new PointF(0, 0);

    public Texture(final GLState glState) {
        mGLState = glState;
        mGL = mGLState.mGL;
    }

    public Texture(final GLState glState, final Bitmap bitmap) {
        mGLState = glState;
        mGL = mGLState.mGL;
        load(bitmap, 0, 0, 0);
    }

    public Texture(final GLState glState, final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps) {
        mGLState = glState;
        mGL = mGLState.mGL;
        load(bitmap, actualWidth, actualHeight, mipmaps);
    }

    public void load(final Bitmap bitmap, final int actualWidth, final int actualHeight, final int mipmaps) {
        mSize.x = actualWidth == 0 && bitmap != null ? bitmap.getWidth() : actualWidth;
        mSize.y = actualHeight == 0 && bitmap != null ? bitmap.getHeight() : actualHeight;
        // find the coordinates' scales
        mCoordScaleX = bitmap == null ? 1 : mSize.x / bitmap.getWidth();
        mCoordScaleY = bitmap == null ? 1 : mSize.y / bitmap.getHeight();

        final int[] ids = new int[1];
        // clear the previous error(s), to make sure
        mGLState.clearErrors();
        mGL.glGenTextures(1, ids, 0);
        mTextureID = ids[0];

        int error = mGL.glGetError();
        Log.v(TAG, String.format("load(%s, %d, %d); id: %d, error: %d", bitmap, actualWidth, actualHeight, mTextureID, error));

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
                setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);
            }

            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            // mGLState.unbindTexture();
        } else {
            Log.e(TAG, "Failed to generate Texture: " + GLU.gluErrorString(error) + "\n" + Log.getStackTraceString(new Exception()));
            // TODO maybe throw an Exception here
        }
    }

    public PointF getSize() {
        return mSize;
    }

    public void setFilters(final int minFilter, final int magFilter) {
        mMinFilter = minFilter;
        mMagFilter = magFilter;

        if (mTextureID != 0) {
            mGLState.bindTexture(this);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, mMinFilter);
            mGL.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, mMagFilter);
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
        if (mTextureID != 0) {
            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
            mGLState.bindTexture(this);
        }
    }

    // public void unbind() {
    // if (mTextureID != 0) {
    // // mGL.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    // mGLState.unbindTexture();
    // }
    // }

    public void unload() {
        Log.v(TAG, "unload(): " + mTextureID);

        if (mTextureID != 0) {
            // clean up
            final int[] ids = {
                mTextureID
            };
            // mGL.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
            mGLState.bindTexture(this);
            mGL.glDeleteTextures(1, ids, 0);
            mTextureID = 0;
        }
    }

    public int getTextureID() {
        return mTextureID;
    }

    public void reload(final GLState glState) {
        mGLState = glState;
        mGL = mGLState.mGL;

        reload();
    }

    public abstract void reload();
}
