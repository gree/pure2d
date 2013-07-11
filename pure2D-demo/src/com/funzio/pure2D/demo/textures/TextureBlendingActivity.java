package com.funzio.pure2D.demo.textures;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.shapes.Sprite;

public class TextureBlendingActivity extends StageActivity {
    private Sprite mGirl;
    private Sprite mChecker;
    private Sprite mGuy;

    @Override
    protected int getLayout() {
        return R.layout.stage_texture_masking;
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
                addObjects();
            }
        });
    }

    private void addObjects() {
        mChecker = new Sprite();
        mChecker.setTexture(mScene.getTextureManager().createDrawableTexture(R.drawable.checker, null));
        mChecker.setOriginAtCenter();
        mChecker.setScale(4, 4);
        mChecker.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mScene.addChild(mChecker);

        mGirl = new Sprite();
        mGirl.setTexture(mScene.getTextureManager().createDrawableTexture(R.drawable.mw_girl, null));
        mGirl.setOriginAtCenter();
        mGirl.setScale(4, 4);
        mGirl.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mGirl.setBlendFunc(BlendModes.MULTIPLY_FUNC);
        mScene.addChild(mGirl);

        mGuy = new Sprite();
        mGuy.setTexture(mScene.getTextureManager().createDrawableTexture(R.drawable.mw_guy, null));
        mGuy.setOriginAtCenter();
        mGuy.setScale(4, 4);
        mGuy.setPosition(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
        mGuy.setBlendFunc(BlendModes.ADD_FUNC);
        mScene.addChild(mGuy);
    }

    public void onClickCheckbox(final View view) {
        if (view.getId() == R.id.cb_mw_girl) {
            mGirl.setVisible(((CheckBox) findViewById(R.id.cb_mw_girl)).isChecked());
        } else if (view.getId() == R.id.cb_mw_guy) {
            mGuy.setVisible(((CheckBox) findViewById(R.id.cb_mw_guy)).isChecked());
        } else if (view.getId() == R.id.cb_checker) {
            mChecker.setVisible(((CheckBox) findViewById(R.id.cb_checker)).isChecked());
        }
    }
}
