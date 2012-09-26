package com.funzio.pure2D.samples.activities.objects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Rectangular;

public class HelloObjectActivity extends StageActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
    }

    private void addObject(final float x, final float y) {
        // create object
        Rectangular obj = new Rectangular();
        obj.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat() + 0.5f));
        obj.setSize(128, 128);

        // center origin
        obj.setOriginAtCenter();

        // random positions
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
}
