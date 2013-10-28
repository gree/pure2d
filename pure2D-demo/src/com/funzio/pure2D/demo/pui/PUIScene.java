/**
 * 
 */
package com.funzio.pure2D.demo.pui;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.ui.UITextureManager;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class PUIScene extends BaseScene {

    protected UIManager mUIManager;

    public PUIScene(final UIManager manager) {
        super();

        mUIManager = manager;
    }

    @Override
    protected TextureManager createDefaultTextureManager() {
        // use UI Texture Manager
        return new UITextureManager(this, mUIManager);
    }

}
