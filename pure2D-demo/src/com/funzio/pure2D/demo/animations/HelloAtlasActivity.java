package com.funzio.pure2D.demo.animations;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;

public class HelloAtlasActivity extends StageActivity {
    private Texture mTexture;
    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
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

                    // generate a lot of squares
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });

        Resources res = getResources();
        mAtlas = new FunzioAtlas(res.getXml(R.xml.atlas));
        mFrameSetNames = mAtlas.getSubFrameSets().keySet().toArray(new String[mAtlas.getNumSubFrameSets()]);
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
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.atlas, null);

        mAtlasSprite = new Sprite();
        mAtlasSprite.setTexture(mTexture);
        float scale = mDisplaySize.x / mTexture.getSize().x;
        mAtlasSprite.setScale(scale);
        mScene.addChild(mAtlasSprite);
    }

    private void addObject(final float x, final float y) {
        // create object
        Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[mRandom.nextInt(mFrameSetNames.length)]));
        obj.setTexture(mTexture);
        // obj.setFps(30);

        // center origin
        obj.setOriginAtCenter();

        // random positions
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
