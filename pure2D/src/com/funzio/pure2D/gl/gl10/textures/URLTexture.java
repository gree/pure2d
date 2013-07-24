/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.loaders.tasks.URLLoadBitmapTask;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class URLTexture extends Texture {
    private String mURL;
    private TextureOptions mOptions;
    private boolean mIsAsync = false;

    public URLTexture(final GLState glState, final String url, final TextureOptions options) {
        super(glState);

        load(url, options);
    }

    public URLTexture(final GLState glState, final String url, final TextureOptions options, final boolean async) {
        super(glState);

        if (async) {
            loadAsync(url, options);
        } else {
            load(url, options);
        }
    }

    public void load(final String url, final TextureOptions options) {
        mIsAsync = false;
        mURL = url;
        mOptions = options;

        int[] dimensions = new int[2];

        final Bitmap bitmap = loadURL(url, options, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        } else {
            Log.e(TAG, "Unable to load bitmap: " + url, new Exception());
            // callback, regardless whether it's successful or not
            if (mListener != null) {
                mListener.onTextureLoad(this);
            }
        }
    }

    @Override
    public void reload() {
        if (mIsAsync) {
            loadAsync(mURL, mOptions);
        } else {
            load(mURL, mOptions);
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
    public void loadAsync(final String filePath, final TextureOptions options) {
        mIsAsync = true;
        mURL = filePath;
        mOptions = options;

        final AsyncLoader loader = new AsyncLoader();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            loader.execute();
        }
    }

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(final Void... params) {
            final int[] dimensions = new int[2];
            final Bitmap bitmap = loadURL(mURL, mOptions, dimensions);
            mGLState.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (bitmap != null) {
                        load(bitmap, dimensions[0], dimensions[1], mOptions != null ? mOptions.inMipmaps : 0);
                        bitmap.recycle();
                    } else {
                        Log.e(TAG, "Unable to load bitmap: " + mURL, new Exception());
                        // callback, regardless whether it's successful or not
                        if (mListener != null) {
                            mListener.onTextureLoad(URLTexture.this);
                        }
                    }
                }
            });

            return null;
        }
    }

    private static Bitmap loadURL(final String url, final TextureOptions options, final int[] outDimensions) {
        final URLLoadBitmapTask task = new URLLoadBitmapTask(url, options);
        if (task.run()) {
            Bitmap bitmap = task.getContent();
            bitmap = bitmap != null ? Pure2DUtils.convertBitmap(bitmap, options, outDimensions) : null;

            return bitmap;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return mURL;
    }
}
