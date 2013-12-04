/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.atlas.Atlas;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;
import com.funzio.pure2D.utils.RectPacker;

/**
 * @author long
 */
public class TexturePacker {
    protected static final String TAG = TexturePacker.class.getSimpleName();

    private final TextureOptions mTextureOptions;
    private Texture mTexture;

    private Atlas mAtlas;
    private RectPacker mRectPacker;
    private Resources mResources;
    private String mPackageName;

    public TexturePacker(final Resources res, final String packageName, final TextureOptions textureOptions) {
        this(res, packageName, textureOptions, 512);
    }

    public TexturePacker(final Resources res, final String packageName, final TextureOptions textureOptions, final int textureMaxSize) {
        mResources = res;
        mPackageName = packageName;
        mTextureOptions = (textureOptions == null) ? TextureOptions.getDefault() : textureOptions;

        mRectPacker = new RectPacker(Math.min(textureMaxSize, Pure2D.GL_MAX_TEXTURE_SIZE), mTextureOptions.inPo2);
        mRectPacker.setQuickMode(true);
        mRectPacker.setRotationEnabled(false);
    }

    /**
     * This can only called on GL Thread
     * 
     * @param glState
     * @return
     */
    public Texture createTexture(final GLState glState, final String... uris) {
        return createTexture(glState.getTextureManager(), uris);
    }

    /**
     * This can only called on GL Thread
     * 
     * @param textureManager
     * @return
     */
    public Texture createTexture(final TextureManager textureManager, final String... uris) {
        if (mTexture == null) {
            mTexture = textureManager.createDynamicTexture(new Runnable() {

                @Override
                public void run() {
                    final Bitmap bitmap = createBitmap(uris);
                    if (bitmap != null) {
                        mTexture.load(bitmap, bitmap.getWidth(), bitmap.getHeight(), 0);
                        bitmap.recycle();
                    }

                    // apply texture to atlas
                    mAtlas.getMasterFrameSet().setTexture(mTexture);
                }
            }, mTextureOptions);

            mTexture.reload();
            // mTexture.setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR); // better output
        }

        return mTexture;
    }

    public TextureOptions getTextureOptions() {
        return mTextureOptions;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public Atlas getAtlas() {
        return mAtlas;
    }

    public Bitmap createBitmap(final String... uris) {

        // find the bounds
        final int length = uris.length;
        final int[] dimens = new int[2];
        String uri;
        // pack the rects
        mRectPacker.reset();
        for (int i = 0; i < length; i++) {
            uri = uris[i];
            if (!Pure2DUtils.getUriBitmapDimensions(mResources, mPackageName, uri, mTextureOptions, dimens)) {
                Log.e(TAG, "Error loading bitmap: " + uri);
                return null;
            }
            // occupy
            mRectPacker.occupy(dimens[0], dimens[1]);
        }

        // prepare the atlas
        if (mAtlas == null) {
            mAtlas = new Atlas(mRectPacker.getWidth(), mRectPacker.getHeight());
        } else {
            mAtlas.setSize(mRectPacker.getWidth(), mRectPacker.getHeight());
            mAtlas.removeAllFrames();
        }

        // create a new bitmap
        final Bitmap bitmap = Bitmap.createBitmap(mRectPacker.getWidth(), mRectPacker.getHeight(), mTextureOptions.inPreferredConfig);
        // use a canvas to draw the bitmaps
        final Canvas canvas = new Canvas(bitmap);
        // draw the bitmaps
        for (int i = 0; i < length; i++) {
            uri = uris[i];
            final Bitmap subBitmap = Pure2DUtils.getUriBitmap(mResources, mPackageName, uri, mTextureOptions, dimens);
            // error check
            if (subBitmap == null) {
                Log.e(TAG, "Error loading bitmap: " + uri);
                bitmap.recycle();
                return null;
            }

            final Rect rect = mRectPacker.getRect(i);
            canvas.drawBitmap(subBitmap, rect.left, rect.top, null);

            // recycle the bitmap
            subBitmap.recycle();

            // add frame to atlas
            final AtlasFrame frame = new AtlasFrame(mAtlas, i, uri, new RectF(rect));
            mAtlas.addFrame(frame);
        }

        return bitmap;
    }
}
