package com.funzio.pure2D.demo.casino;

import jp.gree.casino.scene.CasinoScene;

import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.demo.activities.StageActivity;

public class SlotMachineActivity extends StageActivity {
    private CasinoScene mCasinoScene;

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
        return mCasinoScene = new CasinoScene();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mCasinoScene.getMachine() != null) {
                mCasinoScene.getMachine().spin();
            }
        }

        return true;
    }

}
