package com.funzio.pure2D.demo.objects;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFCinematicActivity extends StageActivity {
    private static final String TAG = LWFCinematicActivity.class.getSimpleName();

    private LWFObject mLWFObject;
    private LWFData mLWFData;
    private LWF mLWF;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);

        mScene.setListener(new Scene.Listener() {

             @Override
             public void onSurfaceCreated(final GL10 gl) {
                mLWFData = new LWFData(mScene, getAssets(), "lwf/evolve/evolve.lwf");

                attachLWF(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
             }
        });
    }

    private void attachLWF(final float x, final float y) {
        // create lwf
        mLWF = new LWF(mLWFData);

        // attach lwf
        mLWFObject.attachLWF(mLWF);

        // position
        mLWF.moveTo("_root", 0, -700);

        // handler
        mLWF.addEventHandler("done", new LWF.Handler() {
            @Override
            public void call() {
                Log.v(TAG, "done");
            }
        });
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mLWF != null)
                mLWF.gotoAndPlay("_root", 1);
        }

        return true;
    }
}
