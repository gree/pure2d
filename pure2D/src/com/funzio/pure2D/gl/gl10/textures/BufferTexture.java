/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class BufferTexture extends Texture {

    /**
     * @param gl
     */
    protected BufferTexture(final GLState glState, final int width, final int height) {
        super(glState);

        // create an blank texture
        load(null, Math.round(width), Math.round(height), 0);
    }

    /**
     * @param gl
     */
    protected BufferTexture(final GLState glState, final int actualWidth, final int actualHeight, final boolean checkPo2) {
        super(glState);

        final int bitmapWidth;
        final int bitmapHeight;
        // check for power of 2
        if (checkPo2 && !Pure2D.GL_NPOT_TEXTURE_SUPPORTED) {
            bitmapWidth = Pure2DUtils.getNextPO2(actualWidth);
            bitmapHeight = Pure2DUtils.getNextPO2(actualHeight);
        } else {
            bitmapWidth = actualWidth;
            bitmapHeight = actualHeight;
        }

        // BitmapSize >= ActualSize!
        load(bitmapWidth, bitmapHeight, actualWidth, actualHeight, 0);
    }

    public void load(final int bitmapWidth, final int bitmapHeight, final int actualWidth, final int actualHeight, final int mipmaps) {
        // create an blank texture
        load(null, bitmapWidth, bitmapHeight, mipmaps);

        // update the bitmap size to re-calculate the scale. BitmapSize >= ActualSize!
        setBitmapSize(bitmapWidth, bitmapHeight, actualWidth, actualHeight);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.gl.gl10.Texture#reload()
     */
    @Override
    public void reload() {
        // create an blank texture
        load(null, (int) mSize.x, (int) mSize.y, 0);
    }

}
