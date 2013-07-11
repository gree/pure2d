/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.funzio.pure2D.Pure2D;

/**
 * @author long
 */
public class TextureOptions extends BitmapFactory.Options {
    public float inScaleX = 1;
    public float inScaleY = 1;
    public boolean inPo2 = !Pure2D.GL_NPOT_TEXTURE_SUPPORTED; // power of 2 dimensions
    public int inMipmaps = 0; // for GL texture mipmapping

    /**
     * Use {@link #getDefault()} to create a default instance
     */
    protected TextureOptions() {
        super();
    }

    /**
     * @return a new instance with default configuration
     */
    public static TextureOptions getDefault() {
        TextureOptions options = new TextureOptions();

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        options.inDither = false;
        options.inPurgeable = true; // for gc

        options.inScaleX = options.inScaleY = 1;
        options.inPo2 = !Pure2D.GL_NPOT_TEXTURE_SUPPORTED;
        options.inMipmaps = 0;

        return options;
    }

    public void set(final TextureOptions options) {
        inPreferredConfig = options.inPreferredConfig;
        inScaled = options.inScaled;
        inDither = options.inDither;
        inPurgeable = options.inPurgeable;

        inScaleX = options.inScaleX;
        inScaleY = options.inScaleY;
        inPo2 = options.inPo2;
        inMipmaps = options.inMipmaps;
    }
}
