/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package com.funzio.pure2D.demo.mw;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.units.AircraftCarrier;
import com.funzio.crimecity.particles.units.Frigate;
import com.funzio.crimecity.particles.units.Submarine;
import com.funzio.crimecity.particles.units.Unit;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;

public class SeaUnitsActivity extends StageActivity {

    private int mDrags = 0;
    private Class<? extends Unit> mAttackerType = Frigate.class;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                ParticleAdapter.getInstance().setSurface(mStage);
                ParticleAdapter.getInstance().setGLState(glState);
                ParticleAdapter.getInstance().onSurfaceCreated(glState.mGL, null);
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_mw_sea_attacker;
    }

    private void addAttacker(final float x, final float y) {

        Unit attacker = null;
        CCMapDirection direction = CCMapDirection.SOUTHWEST;
        if (mAttackerType == Frigate.class) {
            attacker = new Frigate(direction);
        } else if (mAttackerType == Submarine.class) {
            attacker = new Submarine(direction);
        } else if (mAttackerType == AircraftCarrier.class) {
            attacker = new AircraftCarrier(direction, mScene);
        }

        // null check
        if (attacker != null) {
            attacker.setPosition(x, y);

            // add to scene
            mScene.addChild(attacker);
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();
        boolean doOnDrag = false;
        if (action == MotionEvent.ACTION_MOVE) {
            mDrags++;
            doOnDrag = mDrags % 10 == 0;
        }

        if (action == MotionEvent.ACTION_DOWN || doOnDrag) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addAttacker(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

    public void onClickRadio(final View view) {
        switch (view.getId()) {
            case R.id.radio_frigate:
                mAttackerType = Frigate.class;
                break;

            case R.id.radio_submarine:
                mAttackerType = Submarine.class;
                break;

            case R.id.radio_aircraft_carrier:
                mAttackerType = AircraftCarrier.class;
                break;
        }
    }
}
