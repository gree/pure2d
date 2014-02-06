/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

        // only create once
        if (mCamera == null) {
            // load some textures
            mTextureManager.loadTextures();

            // zoom the camero to fit the screen
            mCamera = new Camera(mTextureManager.mFrontTexture.getSize());
            setCamera(mCamera);

            // add to scene
            mMachine = new SlotMachine(this, 5);
            addChild(mMachine);
        }
    }

    public SlotMachine getMachine() {
        return mMachine;
    }
}
