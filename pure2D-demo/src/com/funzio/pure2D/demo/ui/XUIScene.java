/**
 * 
 */
package com.funzio.pure2D.demo.ui;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.ui.BaseUITextureManager;

/**
 * @author long.ngo
 */
public class XUIScene extends BaseScene {

    public XUIScene() {
        super();
    }

    @Override
    protected TextureManager createDefaultTextureManager() {
        // use UI Texture Manager
        return new BaseUITextureManager(this, mStage.getResources());
    }

}
