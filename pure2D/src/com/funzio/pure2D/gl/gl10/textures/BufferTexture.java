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
    public BufferTexture(final GLState glState, final int width, final int height) {
        super(glState);

        // create an blank texture
        load(null, Math.round(width), Math.round(height), 0);
    }

    /**
     * @param gl
     */
    public BufferTexture(final GLState glState, final int width, final int height, final boolean checkPo2) {
        super(glState);

        // create an blank texture
        load(null, Math.round(width), Math.round(height), 0);

        if (checkPo2 && !Pure2D.GL_NPOT_TEXTURE_SUPPORTED) {
            setBitmapSize(Pure2DUtils.getNextPO2(width), Pure2DUtils.getNextPO2(height), width, height);
        }
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
