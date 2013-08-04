/**
 * 
 */
package jp.gree.casino.scene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.gree.casino.machine.SlotMachine;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.Camera;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 */
public class CasinoScene extends BaseScene {
    private Camera mCamera;
    private CasinoTextureManager mTextureManager;
    private SlotMachine mMachine;

    public CasinoScene() {
        super();

        // don't need to clear since the bg is big enough. Hmm, Nexus 7 wants auto clear :(
        // setAutoClear(false);
    }

    @Override
    protected TextureManager createDefaultTextureManager() {
        return mTextureManager = new CasinoTextureManager(this, mStage.getResources());
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        super.onSurfaceCreated(gl, config);

        // load some textures
        mTextureManager.loadTextures();

        // zoom the camero to fit the screen
        mCamera = new Camera(mTextureManager.mFrontTexture.getSize());
        setCamera(mCamera);

        // add to scene
        mMachine = new SlotMachine(this, 5);
        addChild(mMachine);
    }

    public SlotMachine getMachine() {
        return mMachine;
    }
}
