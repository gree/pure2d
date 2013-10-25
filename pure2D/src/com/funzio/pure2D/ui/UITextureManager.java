/**
 * 
 */
package com.funzio.pure2D.ui;

import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.ui.xml.UIConfig;

/**
 * @author long.ngo
 */
public interface UITextureManager {
    public void setUIConfig(UIConfig config);

    public BitmapFont getBitmapFont(String fontId);

    public Texture getTexture(String textureUri);
}
