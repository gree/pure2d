package com.funzio.pure2D.demo.objects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.shapes.Rectangular;

public class BouncerActivity extends StageActivity {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // generate a lot of squares
        addObjects(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y), OBJ_INIT_NUM);
    }

    private void addObjects(final float x, final float y, final int num) {
        // generate a lot of squares
        for (int i = 0; i < num; i++) {

            // create object
            Rectangular sq = new Bouncer();
            sq.setSize(30, 30);
            sq.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), 0.7f));

            // random positions
            sq.setPosition(x + mRandom.nextInt(201) - 100, y + mRandom.nextInt(201) - 100);

            // add to scene
            mScene.addChild(sq);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObjects(event.getX(), mDisplaySize.y - event.getY(), OBJ_STEP_NUM);
                }
            });
        }

        return true;
    }
}
