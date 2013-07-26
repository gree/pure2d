/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class DrawableTexture extends Texture {
    private int mDrawable;
    private TextureOptions mOptions;
    private Resources mResources;
    private boolean mIsAsync = false;

    public DrawableTexture(final GLState glState, final Resources res, final int drawable, final TextureOptions options) {
        super(glState);

        mResources = res;
        load(drawable, options);
    }

    public DrawableTexture(final GLState glState, final Resources res, final int drawable, final TextureOptions options, final boolean async) {
        super(glState);

        mResources = res;

        if (async) {
            loadAsync(drawable, options);
        } else {
            load(drawable, options);
        }
    }

    public void load(final int drawable, final TextureOptions options) {
        mIsAsync = false;
        mDrawable = drawable;
        mOptions = options;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getResourceBitmap(mResources, mDrawable, mOptions, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        }
    }

    @Override
    public void reload() {
        if (mIsAsync) {
            loadAsync(mDrawable, mOptions);
        } else {
            load(mDrawable, mOptions);
        }
    }

    /**
     * Load Asynchronously without block GL thread.
     * 
     * @param filePath
     * @param options
     * @param po2
     */
    @SuppressLint("NewApi")
    public void loadAsync(final int drawable, final TextureOptions options) {
        mIsAsync = true;
        mDrawable = drawable;
        mOptions = options;

        // AsyncTask can only be initialized on UI Thread, especially on Android 2.2
        mGLState.getStage().getHandler().post(new Runnable() {
            @Override
            public void run() {
                final AsyncLoader loader = new AsyncLoader();
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    loader.execute();
                }
            }
        });
    }

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            final int[] dimensions = new int[2];
            final Bitmap bitmap = Pure2DUtils.getResourceBitmap(mResources, mDrawable, mOptions, dimensions);
            mGLState.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (bitmap != null) {
                        load(bitmap, dimensions[0], dimensions[1], mOptions != null ? mOptions.inMipmaps : 0);
                        bitmap.recycle();
                    } else {
                        Log.e(TAG, "Unable to load bitmap: " + mDrawable);
                        // callback, regardless whether it's successful or not
                        if (mListener != null) {
                            mListener.onTextureLoad(DrawableTexture.this);
                        }
                    }
                }
            });

            return null;
        }
    }

    /**
     * @return the resources
     */
    public Resources getResources() {
        return mResources;
    }

    /**
     * @param resources the resources to set
     */
    public void setResources(final Resources resources) {
        mResources = resources;
    }
}
