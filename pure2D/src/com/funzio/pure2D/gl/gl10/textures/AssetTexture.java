/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class AssetTexture extends Texture {
    private AssetManager mAssetManager;
    private String mFilePath;
    private TextureOptions mOptions;
    private boolean mIsAsync = false;

    public AssetTexture(final GLState glState, final AssetManager assetManager, final String filePath, final TextureOptions options) {
        super(glState);

        mAssetManager = assetManager;

        load(filePath, options);
    }

    public AssetTexture(final GLState glState, final AssetManager assetManager, final String filePath, final TextureOptions options, final boolean async) {
        super(glState);

        mAssetManager = assetManager;

        if (async) {
            loadAsync(filePath, options);
        } else {
            load(filePath, options);
        }
    }

    /**
     * Load synchronously. This blocks GL thread until texture is loaded.
     * 
     * @param filePath
     * @param options
     * @param po2
     */
    public void load(final String filePath, final TextureOptions options) {
        mIsAsync = false;
        mFilePath = filePath;
        mOptions = options;

        final int[] dimensions = new int[2];
        final Bitmap bitmap = Pure2DUtils.getAssetBitmap(mAssetManager, mFilePath, mOptions, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        } else {
            Log.e(TAG, "Unable to load bitmap: " + filePath);
            // callback, regardless whether it's successful or not
            if (mListener != null) {
                mListener.onTextureLoad(this);
            }
        }
    }

    @Override
    public void reload() {
        if (mIsAsync) {
            loadAsync(mFilePath, mOptions);
        } else {
            load(mFilePath, mOptions);
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
            final int[] dimensions = new int[2];
            final Bitmap bitmap = Pure2DUtils.getAssetBitmap(mAssetManager, mFilePath, mOptions, dimensions);
            mGLState.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (bitmap != null) {
                        load(bitmap, dimensions[0], dimensions[1], mOptions != null ? mOptions.inMipmaps : 0);
                        bitmap.recycle();
                    } else {
                        Log.e(TAG, "Unable to load bitmap: " + mFilePath);
                        // callback, regardless whether it's successful or not
                        if (mListener != null) {
                            mListener.onTextureLoad(AssetTexture.this);
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
