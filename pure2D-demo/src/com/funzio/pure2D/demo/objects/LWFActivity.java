package com.funzio.pure2D.demo.objects;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
//import com.funzio.pure2D.gl.GL10;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFActivity extends StageActivity {

    private LWFData mLWFData;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

             @Override
             public void onSurfaceCreated(final GL10 gl) {
                mLWFData = new LWFData(mScene, getAssets(), "lwf/YetiBlue.lwf");

                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
             }
        });
    }

    private void addObject(final float x, final float y) {
        // create object
        LWFObject lwf = new LWFObject(mLWFData);

        // center origin
        lwf.setOriginAtCenter();

        // position
        lwf.setPosition(x - 500, y);

        // add to scene
        mScene.addChild(lwf);
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
