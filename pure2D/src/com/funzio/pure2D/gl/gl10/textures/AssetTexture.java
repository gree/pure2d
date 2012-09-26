/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class AssetTexture extends Texture {
    private AssetManager mAssetManager;
    private String mFilePath;
    private TextureOptions mOptions;
    private boolean mPo2;

    public AssetTexture(final GLState glState, final AssetManager assetManager, final String filePath, final TextureOptions options, final boolean po2) {
        super(glState);

        mAssetManager = assetManager;
        load(filePath, options, po2);
    }

    public void load(final String filePath, final TextureOptions options, final boolean po2) {
        mFilePath = filePath;
        mOptions = options;
        mPo2 = po2;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getAssetBitmap(mAssetManager, mFilePath, mOptions, mPo2, dimensions);
        load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
        bitmap.recycle();
    }

    @Override
    public void reload() {
        load(mFilePath, mOptions, mPo2);
    }
}
