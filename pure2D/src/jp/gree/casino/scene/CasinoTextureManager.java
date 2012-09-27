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

    public final Texture mFrontTexture;
    public final Texture mBackTexture;

    public final DisplayMetrics mDisplayMetrics;
    public final float mTextureScaleX;
    public final float mTextureScaleY;

    public final TextureOptions mTextureOptions;
    public ImageSequenceAtlas[] mMajorSymbols;

    public CasinoTextureManager(final Scene scene, final Resources resources) throws UnsupportedOperationException {
        super(scene, resources);

        // FrameBuffer extension is a requirement!
        if (!FrameBuffer.isSupported(mGL)) {
            throw new UnsupportedOperationException("FrameBuffer is not supported!");
        }

        // find the display metrics
        mDisplayMetrics = resources.getDisplayMetrics();

        // determine the global texture scale to save memory
        Bitmap sample = Pure2DUtils.getAssetBitmap(mAssets, mTheme + "/" + "mayan_machine_front.png", null, false, null);
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
                Log.v("long", dir + "/" + files[i]);
                mMajorSymbols[i] = new ImageSequenceAtlas(mScene.getGLState(), mAssets, dir + "/" + files[i], mTextureOptions);
                // unload the frame buffer
                mMajorSymbols[i].getFrameBuffer().unload();
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
