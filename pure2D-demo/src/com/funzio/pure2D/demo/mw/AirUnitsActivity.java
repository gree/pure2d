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

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.units.B52Stratofortress;
import com.funzio.crimecity.particles.units.F15;
import com.funzio.crimecity.particles.units.Harrier;
import com.funzio.crimecity.particles.units.RAH;
import com.funzio.crimecity.particles.units.StealthBomber;
import com.funzio.crimecity.particles.units.Unit;
import com.funzio.crimecity.particles.units.V22Osprey;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;

public class AirUnitsActivity extends StageActivity {

    private int mDrags = 0;
    private Class<? extends Unit> mAttackerType = F15.class;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setColor(COLOR_GREEN);
        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                ParticleAdapter.getInstance().setSurface(mStage);
                ParticleAdapter.getInstance().onSurfaceCreated(glState.mGL, null);
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_mw_air_attacker;
    }

    private void addAttacker(final float x, final float y) {

        Unit attacker = null;
        if (mAttackerType == F15.class) {
            attacker = new F15();
        } else if (mAttackerType == B52Stratofortress.class) {
            attacker = new B52Stratofortress();
        } else if (mAttackerType == StealthBomber.class) {
            attacker = new StealthBomber();
        } else if (mAttackerType == RAH.class) {
            attacker = new RAH();
        } else if (mAttackerType == Harrier.class) {
            attacker = new Harrier();
        } else if (mAttackerType == V22Osprey.class) {
            attacker = new V22Osprey();
        }

        // null check
        if (attacker != null) {
            attacker.setTarget(new PointF(x, y));

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
            case R.id.radio_f15:
                mAttackerType = F15.class;
                break;

            case R.id.radio_b52:
                mAttackerType = B52Stratofortress.class;
                break;

            case R.id.radio_stealth_bomber:
                mAttackerType = StealthBomber.class;
                break;

            case R.id.radio_rah:
                mAttackerType = RAH.class;
                break;

            case R.id.radio_harrier:
                mAttackerType = Harrier.class;
                break;

            case R.id.radio_v22osprey:
                mAttackerType = V22Osprey.class;
                break;
        }
    }
}
