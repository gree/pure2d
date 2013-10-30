/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import com.funzio.pure2D.atlas.AtlasFrameSet;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public interface NovaTextureManager {
    public Texture getUriTexture(final String textureUri, final boolean async);

    public AtlasFrameSet getUriAtlas(final String jsonUri, final boolean async);

    public NovaDelegator getNovaDelegator();

    // public Texture getFloatieTextTexture(final String text);

    // public AtlasFrameSet getUriAtlas(final String jsonUri, final boolean async);
}
