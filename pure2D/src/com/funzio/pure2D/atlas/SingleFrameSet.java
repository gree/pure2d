/**
 * 
 */
package com.funzio.pure2D.atlas;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class SingleFrameSet extends AtlasFrameSet {

    public SingleFrameSet(final String name, final Texture texture) {
        super(name);

        addFrame(new AtlasFrame(texture, 0, name));
    }
}
