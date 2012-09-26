/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import java.io.InputStream;

import android.graphics.Bitmap;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class StreamTexture extends Texture {
    private TextureOptions mOptions;
    private InputStream mStream;
    private boolean mPo2;

    public StreamTexture(final GLState glState, final InputStream stream, final TextureOptions options, final boolean po2) {
        super(glState);

        load(stream, options, po2);
    }

    public void load(final InputStream stream, final TextureOptions options, final boolean po2) {
        mStream = stream;
        mOptions = options;
        mPo2 = po2;

        int[] dimensions = new int[2];
        Bitmap bitmap = Pure2DUtils.getStreamBitmap(mStream, mOptions, mPo2, dimensions);
        if (bitmap != null) {
            load(bitmap, dimensions[0], dimensions[1], options != null ? options.inMipmaps : 0);
            bitmap.recycle();
        }
    }

    @Override
    public void reload() {
        load(mStream, mOptions, mPo2);
    }
}
