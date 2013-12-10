package com.funzio.pure2D.demo.animations;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.JsonAtlas;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
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
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTexture();

                    // generate a lot of clips
                    for (int i = 0; i < 1000; i++) {
                        addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                    }
                }
            }
        });

        try {
            mAtlas = new JsonAtlas(mScene.getAxisSystem());
            mAtlas.load(getAssets(), "atlas/coin_01_60.json", 1);
        } catch (Exception e) {
            Log.e("MultiAtlasActivity", "Loading Atlas Error!", e);
        }
    }

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

    private void addObject(final float screenX, final float screenY) {
        // create object
        Clip obj = new Clip(mAtlas.getMasterFrameSet());
        obj.setTexture(mTexture);
        obj.playAt(RANDOM.nextInt(obj.getNumFrames()));
        // obj.setFps(30);

        // center origin
        // obj.setOriginAtCenter();

        // position
        mScene.screenToGlobal(screenX, screenY, mTempPoint);
        obj.setPosition(mTempPoint);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            int len = event.getPointerCount();
            for (int i = 0; i < len; i++) {
                final float screenX = event.getX(i);
                final float screenY = event.getY(i);
                mStage.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        addObject(screenX, screenY);
                    }
                });
            }
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
