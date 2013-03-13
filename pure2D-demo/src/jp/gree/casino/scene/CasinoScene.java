/**
 * 
 */
package jp.gree.casino.scene;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.Camera;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 */
public class CasinoScene extends BaseScene {
    private Camera mCamera;

    public CasinoScene() {
        super();

        // don't need to clear since the bg is big enough. Hmm, Nexus 7 wants auto clear :(
        // setAutoClear(false);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseScene#createDefaultTextureManager(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    protected TextureManager createDefaultTextureManager() {
        CasinoTextureManager manager = new CasinoTextureManager(this, mStage.getResources());

        // zoom the camero to fit the screen
        mCamera = new Camera(manager.mFrontTexture.getSize());
        setCamera(mCamera);

        return manager;
    }
}
