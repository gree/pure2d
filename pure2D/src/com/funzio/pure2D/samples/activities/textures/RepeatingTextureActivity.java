package com.funzio.pure2D.samples.activities.textures;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.R;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public class RepeatingTextureActivity extends StageActivity {
    private static final int OBJ_WIDTH = 480;
    private Texture mTexture;
    private PointF mTextureScale;
    private Sprite mSprite;
    private CheckBox mCheckboxRepeatX;
    private CheckBox mCheckboxRepeatY;

    @Override
    protected int getLayout() {
        return R.layout.stage_texture_repeating;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCheckboxRepeatX = (CheckBox) findViewById(R.id.cb_repeat_x);
        mCheckboxRepeatY = (CheckBox) findViewById(R.id.cb_repeat_y);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // load the textures
                loadTexture();

                // create the sprite
                mSprite = addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);

                // mTextureScale = new PointF(3, 3); // how many time you want to repeat?
                mTextureScale = new PointF(OBJ_WIDTH / mTexture.getSize().x, OBJ_WIDTH / mTexture.getSize().y); // or automatically find the repeating times according to the size
                applyRepeating();
            }
        });
    }

    private void loadTexture() {
        // create texture
        mTexture = mScene.getTextureManager().createDrawableTexture(R.drawable.cc_128, null);
        // tell texture to repeat on both directions when necessary
        mTexture.setRepeat(GL10.GL_REPEAT, GL10.GL_REPEAT);
    }

    private Sprite addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        // use some texture
        obj.setTexture(mTexture);

        // set the obj to be bigger than the texture
        obj.setSize(OBJ_WIDTH, OBJ_WIDTH);
        // center origin
        obj.setOriginAtCenter();

        // set position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        return obj;
    }

    private void applyRepeating() {
        final TextureCoordBuffer coords = TextureCoordBuffer.getDefault();
        // scale the texture
        coords.scale(mCheckboxRepeatX.isChecked() ? mTextureScale.x : 1, mCheckboxRepeatY.isChecked() ? mTextureScale.y : 1);
        mSprite.setTextureCoordBuffer(coords);
    }

    public void onClickRepeat(final View view) {
        applyRepeating();
    }
}
