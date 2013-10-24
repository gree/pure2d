package com.funzio.pure2D.demo.ui;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.ui.xml.UILoader;

public class XMLUIActivity extends StageActivity {
    private Texture[] mTextures;
    private UILoader mUILoader;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        mScene.setUIEnabled(true);
        // mScene.setAxisSystem(Scene.AXIS_TOP_LEFT);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                }
            }
        });

        mUILoader = new UILoader();
    }

    private void loadTexture() {
        // create texture
        mTextures = new Texture[] { //
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_up, null), mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_down, null),
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_disabled, null),
        };
    }

    private int mObjectSeq = 0;

    private void addObject(final float x, final float y) {

        // create object
        DisplayObject obj = mUILoader.load(getResources().getXml(R.xml.ui_test1));
        obj.setId("obj_" + (++mObjectSeq));
        // loader.load("<Group><Sprite /></Group>");
        // Log.e("long", obj.getObjectTree(""));
        obj.setOriginAtCenter();

        // set positions
        final PointF point = new PointF();
        mScene.screenToGlobal(x, y, point);
        obj.setPosition(point);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {

        if (!mScene.onTouchEvent(event)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mStage.queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        addObject(event.getX(), event.getY());
                    }
                });
            }
        }

        return true;
    }
}
