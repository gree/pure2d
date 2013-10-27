/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import java.io.File;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.loaders.tasks.DownloadTask;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class URLCacheTexture extends Texture {
    private String mUrlDir;
    private String mCacheDir;
    private String mFilePath;
    private TextureOptions mOptions;
    private boolean mIsAsync = false;

    protected URLCacheTexture(final GLState glState, final String urlDir, final String cacheDir, final String filePath, final TextureOptions options) {
        super(glState);

        load(urlDir, cacheDir, filePath, options);
    }

    protected URLCacheTexture(final GLState glState, final String urlDir, final String cacheDir, final String filePath, final TextureOptions options, final boolean async) {
        super(glState);

        if (async) {
            loadAsync(urlDir, cacheDir, filePath, options);
        } else {
            load(urlDir, cacheDir, filePath, options);
        }
    }

    public void load(final String urlDir, final String cacheDir, final String filePath, final TextureOptions options) {
        mIsAsync = false;
        mUrlDir = urlDir;
        mCacheDir = cacheDir;
        mFilePath = filePath;
        mOptions = options;

        final String fullPath = cacheDir + filePath;
        int[] dimensions = new int[2];
        Bitmap bitmap = null;

        final File file = new File(fullPath);
        if (file.exists()) {
            bitmap = Pure2DUtils.getFileBitmap(fullPath, options, dimensions);
        } else if (urlDir != null && urlDir.length() > 0) {
            // try to download and cache
            final String fullUrl = urlDir + filePath;
            if (new DownloadTask(fullUrl, fullPath).run()) {
                bitmap = Pure2DUtils.getFileBitmap(fullPath, options, dimensions);
            }
        }

        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        } else {
            Log.e(TAG, "Unable to load bitmap: " + fullPath, new Exception());
            // callback, regardless whether it's successful or not
            if (mListener != null) {
                mListener.onTextureLoad(this);
            }
        }
    }

    @Override
    public void reload() {
        if (mIsAsync) {
            loadAsync(mUrlDir, mCacheDir, mFilePath, mOptions);
        } else {
            load(mUrlDir, mCacheDir, mFilePath, mOptions);
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
    public void loadAsync(final String urlDir, final String cacheDir, final String filePath, final TextureOptions options) {
        mIsAsync = true;
        mUrlDir = urlDir;
        mCacheDir = cacheDir;
        mFilePath = filePath;
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
            final String fullPath = mCacheDir + mFilePath;
            final int[] dimensions = new int[2];
            Bitmap bitmap = null;
            final File file = new File(fullPath);
            if (file.exists()) {
                bitmap = Pure2DUtils.getFileBitmap(fullPath, mOptions, dimensions);
            } else if (mUrlDir != null && mUrlDir.length() > 0) {
                // try to download and cache
                final String fullUrl = mUrlDir + mFilePath;
                if (new DownloadTask(fullUrl, fullPath).run()) {
                    bitmap = Pure2DUtils.getFileBitmap(fullPath, mOptions, dimensions);
                }
            }
            final Bitmap finalBitmap = bitmap;

            mGLState.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (finalBitmap != null) {
                        load(finalBitmap, dimensions[0], dimensions[1], mOptions != null ? mOptions.inMipmaps : 0);
                        finalBitmap.recycle();
                    } else {
                        Log.e(TAG, "Unable to load bitmap: " + fullPath, new Exception());
                        // callback, regardless whether it's successful or not
                        if (mListener != null) {
                            mListener.onTextureLoad(URLCacheTexture.this);
                        }
                    }
                }
            });

            return null;
        }
    }

    @Override
    public String toString() {
        return mFilePath;
    }
}
