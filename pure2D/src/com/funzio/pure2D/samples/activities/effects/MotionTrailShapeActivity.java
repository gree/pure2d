package com.funzio.pure2D.samples.activities.effects;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.PolyLine;

public class MotionTrailShapeActivity extends StageActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
    }

    private void addObject(final float x, final float y) {
        final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
        final GLColor color2 = new GLColor(color1);
        color2.a = 0;

        // create object
        PolyLine obj = new PolyLine();
        obj.setColor(color1);
        obj.setThickRange(10, 30);
        obj.setPoints(new PointF(x, y), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)));

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
