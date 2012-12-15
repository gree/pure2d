/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author long
 */
public class TextureOptions extends BitmapFactory.Options {
    public float inScaleX = 1;
    public float inScaleY = 1;
    public boolean inPo2 = true; // power of 2 dimensions
    public int inMipmaps = 0; // for GL texture mipmapping

    public static TextureOptions getDefault() {
        TextureOptions options = new TextureOptions();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        options.inPurgeable = true; // for gc
        options.inScaleX = options.inScaleY = 1;
        options.inPo2 = true;
        options.inMipmaps = 0;

        return options;
    }

    public void set(final TextureOptions options) {
        inScaleX = options.inScaleX;
        inScaleY = options.inScaleY;
        inPo2 = options.inPo2;
        inMipmaps = options.inMipmaps;

        inPreferredConfig = options.inPreferredConfig;
        inScaled = options.inScaled;
        inPurgeable = options.inPurgeable;
    }
}
