package com.funzio.pure2D.demo.objects;

import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFManager;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFCinematicActivity extends StageActivity {
    private static final String TAG = LWFCinematicActivity.class.getSimpleName();

    private LWFManager mLWFManager;
    private LWFObject mLWFObject;
    private LWFData mLWFData;
    private LWF mLWF;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LWF.loadLibrary()) {
            Log.e(TAG, "ERROR: loadLibrary");
        }

        mLWFManager = new LWFManager();

        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState gl, final boolean firstTime) {
                if (firstTime) {
                    try {
                        InputStream stream = getAssets().open("lwf/evolve/evolve.lwf");
                        mLWFData = mLWFManager.createLWFData(stream);
                    } catch (Exception e) {
                        Log.e(TAG, "ERROR: " + e);
                    }

                    int textureNum = mLWFData.getTextureNum();
                    Texture[] textures = new Texture[textureNum];
                    for (int i = 0; i < textureNum; ++i) {
                        String name = mLWFData.getTextureName(i);
                        textures[i] = mScene.getTextureManager().createAssetTexture("lwf/evolve/" + name, null);
                    }
                    mLWFData.setTextures(textures);

                    attachLWF(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLWFManager.dispose();
    }

    private void attachLWF(final float x, final float y) {
        // attach lwf
        mLWF = mLWFObject.attachLWF(mLWFData);

        // position in Flash coordinate
        mLWF.moveTo("_root", 0, -700);

        // handler
        mLWF.addEventHandler("done", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "done");
            }
        });
        mLWF.addEventHandler("STOP_DRAWING_BASE_UNITS", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "STOP_DRAWING_BASE_UNITS");
            }
        });
        mLWF.addEventHandler("START_DRAWING_FINAL_UNIT", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "START_DRAWING_FINAL_UNIT");
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLWF != null) {
                mLWF.gotoAndPlay("_root", "start");
            }
        }

        return true;
    }
}
