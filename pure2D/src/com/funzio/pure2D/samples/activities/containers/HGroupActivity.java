package com.funzio.pure2D.samples.activities.containers;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.R;
import com.funzio.pure2D.containers.HGroup;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public class HGroupActivity extends StageActivity {
    private List<Texture> mTextures = new ArrayList<Texture>();
    private HGroup mContainer;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_repeating;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to allow swiping
        mScene.setUIEnabled(true);
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                // load the textures
                loadTextures();

                // generate a lot of squares
                addGroup(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_175, // cc
                R.drawable.mw_175, // mw
                R.drawable.ka_175, // ka
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void addGroup(final float x, final float y) {
        mContainer = new HGroup();
        mContainer.setGap(10);
        mContainer.setSize(mDisplaySize.x, 200);
        mContainer.setSwipeEnabled(true);

        for (int n = 0; n < 3; n++) {
            Texture texture = mTextures.get(n % mTextures.size());
            // create object
            Sprite sq = new Sprite();
            sq.setTexture(texture);

            // add to container
            mContainer.addChild(sq);
        }
        mContainer.setPosition(0, mDisplaySizeDiv2.y - mContainer.getSize().y / 2);

        // add to scene
        mScene.addChild(mContainer);
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

    public void onClickRepeating(final View view) {
        mContainer.setRepeating(((CheckBox) view).isChecked());
        if (!mContainer.isRepeating()) {
            mContainer.scrollTo(0, 0);
        }
    }
}
