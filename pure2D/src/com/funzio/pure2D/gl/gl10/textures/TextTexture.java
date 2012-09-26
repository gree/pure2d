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
    private boolean mPo2;

    public TextTexture(final GLState glState, final String text, final TextOptions options, final boolean po2) {
        super(glState);

        load(text, options, po2);
    }

    public void load(final String text, final TextOptions options, final boolean po2) {
        mText = text;
        mOptions = options;
        mPo2 = po2;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getTextBitmap(mText, mOptions, mPo2, dimensions);
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
        load(mText, mOptions, mPo2);
    }

}
