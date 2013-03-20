package com.funzio.pure2D.demo.effects;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;

public class MotionTrailShapeActivity extends StageActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                for (int i = 0; i < 50; i++) {
                    addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                }
            }
        });
    }

    // private void addObject(float x, float y) {
    // final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
    // final GLColor color2 = new GLColor(color1);
    // color2.a = 0;
    //
    // // create object
    // Polyline obj = new Polyline();
    // obj.setColor(color1);
    // obj.setStrokeRange(10, 50);
    //
    // PointF[] points = new PointF[5];
    // points[0] = new PointF(x, y);
    // for (int i = 1; i < points.length; i++) {
    // x += RANDOM.nextInt(300) - 150;
    // y += RANDOM.nextInt(300) - 150;
    // points[i] = new PointF(x, y);
    // }
    // obj.setPoints(points);
    //
    // // obj.setPoints(new PointF(x, y), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)), new PointF(x + RANDOM.nextInt(300), y + RANDOM.nextInt(300)));
    // // obj.setPoints(new PointF(x, y), new PointF(x + 300, y), new PointF(x + 100, y + 100), new PointF(x + 400, y - 300), new PointF());
    //
    // // add to scene
    // mScene.addChild(obj);
    // }

    private void addObject(final float x, final float y) {
        final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
        final GLColor color2 = new GLColor(color1);// new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 0.5f);
        color2.a = 0.1f;

        // create object
        Bouncer obj = new Bouncer();
        obj.setSize(30, 30);
        obj.setOriginAtCenter();
        obj.setColor(color1);
        obj.setPosition(x, y);
        // add to scene
        mScene.addChild(obj);

        MotionTrailShape trail = new MotionTrailShape();
        trail.setColor(color1);
        trail.setStrokeRange(30, 1);
        trail.setNumPoints(20);
        trail.setStrokeColorRange(color1, color2);
        trail.setMinLength(100);
        trail.setTarget(obj);
        mScene.addChild(trail);

    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        addObject(event.getX(), mDisplaySize.y - event.getY());
                    }
                }
            });
        }

        return true;
    }
}
