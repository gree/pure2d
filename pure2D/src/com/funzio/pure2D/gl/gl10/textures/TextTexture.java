/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.Bitmap;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class TextTexture extends Texture {

    private String mText = "";
    private TextOptions mOptions;

    public TextTexture(final GLState glState, final String text, final TextOptions options) {
        super(glState);

        load(text, options);
    }

    public void load(final String text, final TextOptions options) {
        mText = text;
        mOptions = options;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getTextBitmap(mText, mOptions, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.textures.Texture#reload()
     */
    @Override
    public void reload() {
        load(mText, mOptions);
    }

}
