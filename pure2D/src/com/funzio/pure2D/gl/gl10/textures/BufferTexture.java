/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class BufferTexture extends Texture {

    /**
     * @param gl
     */
    public BufferTexture(final GLState glState, final float width, final float height) {
        super(glState);

        // create an blank texture
        load(null, Math.round(width), Math.round(height), 0);
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
