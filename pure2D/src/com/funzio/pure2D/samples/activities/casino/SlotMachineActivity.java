package com.funzio.pure2D.samples.activities.casino;

import javax.microedition.khronos.opengles.GL10;

import jp.gree.casino.machine.SlotMachine;
import jp.gree.casino.scene.CasinoScene;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.samples.activities.StageActivity;

public class SlotMachineActivity extends StageActivity {
    private SlotMachine mMachine;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                createMachine();
            }
        });
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#createScene()
     */
    @Override
    protected BaseScene createScene() {
        return new CasinoScene();
    }

    private void createMachine() {
        mMachine = new SlotMachine((CasinoScene) mScene, 5);

        // add to scene
        mScene.addChild(mMachine);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mMachine != null) {
                mMachine.spin();
            }
        }

        return true;
    }

}
