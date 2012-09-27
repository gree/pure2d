package com.funzio.pure2D.samples.activities.containers;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.R;
import com.funzio.pure2D.containers.VGroup;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public class VGroupActivity extends StageActivity {
    private List<Texture> mTextures = new ArrayList<Texture>();
    private PointF mRegisteredPoint = new PointF();
    private VGroup mContainer;

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

        // need to get the GL reference first
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
        mContainer = new VGroup();
        mContainer.setGap(50);
        mContainer.setSize(200, mDisplaySize.y);

        for (int n = 0; n < 3; n++) {
            Texture texture = mTextures.get(n % mTextures.size());
            // create object
            Sprite sq = new Sprite();
            sq.setTexture(texture);
            // sq.setSize(200, 100 + n * 30);

            // add to container
            mContainer.addChild(sq);
        }
        mContainer.setPosition(mDisplaySizeDiv2.x - mContainer.getContentSize().x / 2, 0);

        // add to scene
        mScene.addChild(mContainer);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mRegisteredPoint.x = x;
            mRegisteredPoint.y = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            float delta = y - mRegisteredPoint.y;
            mContainer.scrollBy(0, delta);
            mRegisteredPoint.x = x;
            mRegisteredPoint.y = y;
        }

        // if (action == MotionEvent.ACTION_DOWN) {
        // Log.v("long", "touch");
        // if (event.getPointerCount() == 1) {
        // mContainer.scrollBy(0, 10.5f);
        // } else {
        // mContainer.scrollBy(0, -10.5f);
        // }
        // }

        return true;
    }

    public void onClickRepeating(final View view) {
        mContainer.setRepeating(((CheckBox) view).isChecked());
        if (!mContainer.isRepeating()) {
            mContainer.scrollTo(0, 0);
        }
    }
}
