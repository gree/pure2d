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

    public DrawableTexture(final GLState glState, final Resources res, final int drawable, final TextureOptions options) {
        super(glState);

        mResources = res;
        load(drawable, options);
    }

    public void load(final int drawable, final TextureOptions options) {
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
        load(mDrawable, mOptions);
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
