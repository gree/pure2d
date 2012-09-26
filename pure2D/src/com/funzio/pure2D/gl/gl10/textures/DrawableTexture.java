/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class DrawableTexture extends Texture {
    private int mDrawable;
    private TextureOptions mOptions;
    private Resources mResources;
    private boolean mPo2 = false;

    public DrawableTexture(final GLState glState, final Resources res, final int drawable, final TextureOptions options, final boolean po2) {
        super(glState);

        mResources = res;
        load(drawable, options, po2);
    }

    public void load(final int drawable, final TextureOptions options, final boolean po2) {
        mDrawable = drawable;
        mOptions = options;
        mPo2 = po2;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getResourceBitmap(mResources, mDrawable, mOptions, mPo2, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        }
    }

    @Override
    public void reload() {
        load(mDrawable, mOptions, mPo2);
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
