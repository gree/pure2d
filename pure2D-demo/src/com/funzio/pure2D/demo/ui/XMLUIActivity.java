package com.funzio.pure2D.demo.ui;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.ui.Button;
import com.funzio.pure2D.ui.xml.UILoader;

public class XMLUIActivity extends StageActivity {
    private Texture[] mTextures;

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

        Resources res = getResources();
        UILoader loader = new UILoader();
        loader.load(res.getXml(R.xml.test_ui));
        // loader.load("<Group><Sprite /></Group>");
    }

    private void loadTexture() {
        // create texture
        mTextures = new Texture[] { //
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_up, null), mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_down, null),
                mScene.getTextureManager().createDrawableTexture(R.drawable.btn_bingo_disabled, null),
        };
    }

    private void addObject(final float x, final float y) {
        // create object
        Button obj = new Button();
        obj.setTextures(mTextures);
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
        mScene.onTouchEvent(event);

        return true;
    }
}
