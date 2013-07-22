package com.funzio.pure2D.demo.objects;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFManager;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFCharacterActivity extends StageActivity {

    private LWFManager mLWFManager;
    private LWFData mLWFData;
    private LWFObject mLWFObject;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LWF.loadLibrary())
            Log.e("LWFCharacterActivity", "ERROR: loadLibrary");

        mLWFManager = new LWFManager();

        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);

        mScene.setListener(new Scene.Listener() {

             @Override
             public void onSurfaceCreated(final GL10 gl) {
                try {
                    InputStream stream = getAssets().open("lwf/YetiBlue/YetiBlue.lwf");
                    mLWFData = mLWFManager.createLWFData(stream);
                } catch (Exception e) {
                    Log.e("LWFCharacterActivity", "ERROR: " + e);
                }
        
                int textureNum = mLWFData.getTextureNum();
                Texture[] textures = new Texture[textureNum];
                for (int i = 0; i < textureNum; ++i) {
                    String name = mLWFData.getTextureName(i);
                    textures[i] = mScene.getTextureManager().createAssetTexture("lwf/YetiBlue/" + name, null);
                }
                mLWFData.setTextures(textures);

                attachLWF(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
             }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLWFManager.dispose();
    }

    private void attachLWF(final float x, final float y) {
        if (mLWFData == null)
            return;

        // attach lwf
        LWF lwf = mLWFObject.attachLWF(mLWFData);

        // position in Flash coordinate
        lwf.moveTo("_root", x - 450, -y - 80);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    attachLWF(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}
