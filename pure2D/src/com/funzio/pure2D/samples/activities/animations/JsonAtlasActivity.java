package com.funzio.pure2D.samples.activities.animations;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;

public class JsonAtlasActivity extends StageActivity {
    private Texture mTexture;
    private JsonAtlas mAtlas;
    private Sprite mAtlasSprite;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTexture();

                // generate a lot of squares
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });

        try {
            mAtlas = new JsonAtlas(getAssets(), "atlas/coin_01_60.json", 1);
        } catch (Exception e) {
            Log.e("JsonAtlasActivity", Log.getStackTraceString(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_atlas;
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createAssetTexture("atlas/coin_01_60.png", null);

        mAtlasSprite = new Sprite();
        mAtlasSprite.setTexture(mTexture);
        mScene.addChild(mAtlasSprite);
    }

    private void addObject(final float x, final float y) {
        // create object
        Clip obj = new Clip(mAtlas.getMasterFrameSet());
        obj.setTexture(mTexture);
        // obj.setFps(30);

        // center origin
        obj.setOriginAtCenter();

        // position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addObject(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

    public void onClickAtlas(final View view) {
        if (view.getId() == R.id.cb_show_atlas) {
            if (mAtlasSprite != null) {
                mAtlasSprite.setVisible(((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked());
                // for testing clipping
                // if (((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked()) {
                // mAtlasSprite.setPosition(0, 0);
                // } else {
                // mAtlasSprite.setPosition(0, mDisplaySize.y);
                // }
            }
        }
    }
}
