package com.funzio.pure2D.demo.objects;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
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
        // position
        obj.setPosition(x, y);
        // add to scene
        mScene.addChild(obj);

        // test polyline
        // Polyline line = new Polyline();
        // line.setStrokeRange(50, 50);
        // line.setStrokeColors(new GLColor(1, 1, 0, 0.5f));
        // // line.setPoints(new PointF(0, 0), new PointF(100, 50), new PointF(200, 0), new PointF(200, 300), new PointF(0, 300), new PointF(0, 200), new PointF(-100, 200), new PointF(-100, 400),
        // // new PointF(-100, 500), new PointF(-100, 600), new PointF(0, 600), new PointF(-200, 800), new PointF(-200, 400), new PointF(-200, 500), new PointF(-200, 0), new PointF(-500, 500));
        // line.setPoints(new PointF(0, 0), new PointF(100, 100), new PointF(200, 0), new PointF(0, 100));
        // line.setPosition(x, y);
        // line.setDebugFlags(Pure2D.DEBUG_FLAG_WIREFRAME);
        // mScene.addChild(line);

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
