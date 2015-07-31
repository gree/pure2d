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

import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.ImageSequenceAtlas;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class CasinoTextureManager extends TextureManager {
    private static final String TAG = CasinoTextureManager.class.getSimpleName();
    private static final String DIR_SYMBOLS = "symbols";
    private static final String DIR_SYMBOLS_MAJORS = DIR_SYMBOLS + "/majors";
    // private static final String DIR_SYMBOLS_MINORS = DIR_SYMBOLS + "/minors";

    // XXX remove me
    private String mTheme = "mayan";

    public Texture mFrontTexture;
    public Texture mBackTexture;

    public final DisplayMetrics mDisplayMetrics;
    public final float mTextureScaleX;
    public final float mTextureScaleY;

    public final TextureOptions mTextureOptions;
    public ImageSequenceAtlas[] mMajorSymbols;

    public CasinoTextureManager(final Scene scene, final Resources resources) throws UnsupportedOperationException {
        super(scene, resources);

        // FrameBuffer extension is a requirement!
        if (!FrameBuffer.isSupported()) {
            throw new UnsupportedOperationException("FrameBuffer is not supported!");
        }

        // find the display metrics
        mDisplayMetrics = resources.getDisplayMetrics();

        // determine the global texture scale to save memory
        Bitmap sample = Pure2DUtils.getAssetBitmap(mAssets, mTheme + "/" + "mayan_machine_front.png", null, null);
        float scaleX = (float) mDisplayMetrics.widthPixels / sample.getWidth();
        float scaleY = (float) mDisplayMetrics.heightPixels / sample.getHeight();
        sample.recycle();
        if (scaleX > 1 || scaleY > 1) {
            // do not scale up
            mTextureScaleX = mTextureScaleY = 1;
        } else {
            // but scale down to save HUGE memory!
            mTextureScaleX = scaleX;
            mTextureScaleY = scaleY;
        }

        // the global texture options
        mTextureOptions = TextureOptions.getDefault();
        mTextureOptions.inScaleX = mTextureScaleX;
        mTextureOptions.inScaleY = mTextureScaleY;
    }

    public void loadTextures() {
        // load the textures and atlas
        mBackTexture = createAssetTexture(mTheme + "/" + "mayan_machine_back.jpg", mTextureOptions);
        mFrontTexture = createAssetTexture(mTheme + "/" + "mayan_machine_front.png", mTextureOptions);

        loadMajorSymbols();
        // loadMinorSymbols();
    }

    private String getSymbolsMajorsDir() {
        return mTheme + "/" + DIR_SYMBOLS_MAJORS;
    }

    // private String getSymbolsMinorsDir() {
    // return mTheme + "/" + DIR_SYMBOLS_MINORS;
    // }

    private void loadMajorSymbols() {
        try {
            String dir = getSymbolsMajorsDir();
            String files[] = mAssets.list(dir);
            mMajorSymbols = new ImageSequenceAtlas[files.length];
            for (int i = 0; i < files.length; i++) {
                mMajorSymbols[i] = new ImageSequenceAtlas(mScene.getGLState());
                mMajorSymbols[i].loadDir(mAssets, dir + "/" + files[i], mTextureOptions);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return;
        }
    }

    // private void loadMinorSymbols() {
    // try {
    // // TODO
    // String dir = getSymbolsMinorsDir();
    // String files[] = mAssets.list(dir);
    // } catch (IOException e) {
    // Log.e(TAG, e.getMessage());
    // return;
    // }
    // }
}
