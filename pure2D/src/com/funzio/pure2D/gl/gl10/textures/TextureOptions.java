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
    public int inMipmaps = 0; // for GL texture mipmapping

    public static TextureOptions getDefault() {
        TextureOptions options = new TextureOptions();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        options.inPurgeable = true;
        options.inScaleX = options.inScaleY = 1;
        options.inMipmaps = 0;

        return options;
    }

    public void set(final TextureOptions options) {
        inScaleX = options.inScaleX;
        inScaleY = options.inScaleY;
        inMipmaps = options.inMipmaps;

        inPreferredConfig = options.inPreferredConfig;
        inScaled = options.inScaled;
        inPurgeable = options.inPurgeable;
    }
}
