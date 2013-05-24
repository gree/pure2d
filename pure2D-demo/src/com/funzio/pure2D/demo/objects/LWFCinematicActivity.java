package com.funzio.pure2D.demo.objects;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFCinematicActivity extends StageActivity {
    private static final String TAG = LWFCinematicActivity.class.getSimpleName();

    private LWFObject mLWFObject;
    private LWF mLWF;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);

        mScene.setListener(new Scene.Listener() {

             @Override
             public void onSurfaceCreated(final GL10 gl) {
                attachLWF(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
             }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScene.dispose();
    }

    private void attachLWF(final float x, final float y) {
        // attach lwf
        mLWF = mLWFObject.attachLWF(getAssets(), "lwf/evolve/evolve.lwf");

        // position in Flash coordinate
        mLWF.moveTo("_root", 0, -700);

        // handler
        mLWF.addEventHandler("done", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "done");
            }
        });
        mLWF.addEventHandler("STOP_DRAWING_BASE_UNITS", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "STOP_DRAWING_BASE_UNITS");
            }
        });
        mLWF.addEventHandler("START_DRAWING_FINAL_UNIT", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "START_DRAWING_FINAL_UNIT");
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLWF != null)
                mLWF.gotoAndPlay("_root", "start");
        }

        return true;
    }
}
