package com.funzio.pure2D.demo.textures;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite9;

public class Sprite9Activity extends StageActivity {
    private Texture mTexture;
    protected boolean m9PatchEnabled = true;

    @Override
    protected int getLayout() {
        return R.layout.stage_texture_9_patch;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // load the textures
                loadTexture();

                // create first obj
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.panel_collection_reward, null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite9 obj = new Sprite9();
        obj.setTexture(mTexture);
        obj.setSize(mRandom.nextInt(400) + 100, mRandom.nextInt(400) + 100);

        if (m9PatchEnabled) {
            obj.setPatches(20, 20, 20, 20);
        }

        // center origin
        obj.setOriginAtCenter();

        // set positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }

    public void onClick9Patch(final View view) {
        if (view.getId() == R.id.cb_texture_9_patch) {
            m9PatchEnabled = ((CheckBox) findViewById(R.id.cb_texture_9_patch)).isChecked();
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    final int num = mScene.getNumChildren();
                    for (int n = 0; n < num; n++) {
                        Sprite9 obj = (Sprite9) mScene.getChildAt(n);
                        if (m9PatchEnabled) {
                            obj.setPatches(20, 20, 20, 20);
                        } else {
                            obj.setPatches(0, 0, 0, 0);
                        }
                    }
                }
            });
        }
    }
}
