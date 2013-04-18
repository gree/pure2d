package com.funzio.pure2D.demo.ui;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.atlas.FunzioAtlas;
import com.funzio.pure2D.containers.HList;
import com.funzio.pure2D.containers.VList;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;

public class ListActivity extends StageActivity {
    private static final int LIST_WIDTH = 150;

    private FunzioAtlas mAtlas;
    private String[] mFrameSetNames;
    private Texture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for swiping
        mScene.setUIEnabled(true);
        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTextures();

                // generate the lists
                addHList();
                addVList();
            }
        });

        Resources res = getResources();
        mAtlas = new FunzioAtlas(res.getXml(R.xml.atlas));
        mFrameSetNames = mAtlas.getSubFrameSets().keySet().toArray(new String[mAtlas.getNumSubFrameSets()]);
    }

    private void loadTextures() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.atlas, null);
    }

    private void addVList() {
        VList list = new VList();
        // list.setPositiveOrientation(false);
        list.setGap(10);
        list.setSize(LIST_WIDTH, mDisplaySize.y - LIST_WIDTH);

        for (int n = 0; n < mFrameSetNames.length * 2; n++) {
            // create object
            Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
            obj.setTexture(mTexture);

            // add to container
            list.addChild(obj);
        }
        list.setPosition(0, LIST_WIDTH);

        // add to scene
        mScene.addChild(list);
    }

    private void addHList() {
        HList list = new HList();
        list.setGap(10);
        list.setSize(mDisplaySize.x, LIST_WIDTH);

        for (int n = 0; n < mFrameSetNames.length * 2; n++) {
            // create object
            Clip obj = new Clip(mAtlas.getSubFrameSet(mFrameSetNames[n % mFrameSetNames.length]));
            obj.setTexture(mTexture);

            // add to container
            list.addChild(obj);
        }
        list.setPosition(0, 0);

        // add to scene
        mScene.addChild(list);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        // forward the event to scene
        mScene.onTouchEvent(event);

        return true;
    }
}
