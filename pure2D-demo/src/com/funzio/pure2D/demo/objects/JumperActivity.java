package com.funzio.pure2D.demo.objects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.uni.UniGroup;

public class JumperActivity extends StageActivity {
    private UniGroup mUniGroup;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    mUniGroup = new UniGroup();
                    mUniGroup.setSize(mDisplaySize.x, mDisplaySize.y);
                    mScene.addChild(mUniGroup);
                    // generate a lot of squares
                    addObjects(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y, OBJ_INIT_NUM);
                }
            }
        });
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    private void addObjects(final float screenX, final float screenY, final int num) {
        mScene.screenToGlobal(screenX, screenY, mTempPoint);

        // generate a lot of squares
        for (int i = 0; i < num; i++) {

            // create object
            Jumper sq = new Jumper();
            sq.setSize(30, 30);
            sq.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), 0.7f));

            // random positions
            sq.setPosition(mTempPoint.x + mRandom.nextInt(201) - 100, mTempPoint.y + mRandom.nextInt(201) - 100);

            // add to scene
            mUniGroup.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObjects(event.getX(), event.getY(), OBJ_STEP_NUM * 5);
                }
            });
        }

        return true;
    }
}
