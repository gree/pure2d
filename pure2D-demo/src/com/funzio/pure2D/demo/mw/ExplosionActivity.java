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

import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.explosions.BlackExplosion;
import com.funzio.crimecity.particles.explosions.Explosion;
import com.funzio.crimecity.particles.explosions.ExplosionCombo;
import com.funzio.crimecity.particles.fire.TankFire;
import com.funzio.crimecity.particles.gunshots.Gunshots;
import com.funzio.crimecity.particles.smoke.SmokePuff;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.particles.ParticleEmitter;

public class ExplosionActivity extends StageActivity {

    private int mDrags = 0;
    private Class<? extends ParticleEmitter> mParticleType = Explosion.class;

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

                if (firstTime) {
                    addEmitter(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.stage_mw_explosion;
    }

    private void addEmitter(final float x, final float y) {

        ParticleEmitter emitter = null;
        if (mParticleType == Explosion.class) {
            emitter = new Explosion(ParticleAdapter.TEXTURE_MANAGER.mFireTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        } else if (mParticleType == BlackExplosion.class) {
            emitter = new BlackExplosion(ParticleAdapter.TEXTURE_MANAGER.mFireTexture);
        } else if (mParticleType == TankFire.class) {
            emitter = new TankFire(ParticleAdapter.TEXTURE_MANAGER.mFireGreyTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        } else if (mParticleType == Gunshots.class) {
            emitter = new Gunshots(ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        } else if (mParticleType == SmokePuff.class) {
            emitter = new SmokePuff(ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        } else if (mParticleType == ExplosionCombo.class) {
            emitter = new ExplosionCombo(ParticleAdapter.TEXTURE_MANAGER.mFireTexture, ParticleAdapter.TEXTURE_MANAGER.mSmokeTexture);
        }

        // null check
        if (emitter != null) {
            ((DisplayObject) emitter).setPosition(x, y);

            // add to scene
            mScene.addChild((DisplayObject) emitter);
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();
        boolean doOnDrag = false;
        if (action == MotionEvent.ACTION_MOVE) {
            mDrags++;
            doOnDrag = mDrags % 5 == 0;
        }

        if (action == MotionEvent.ACTION_DOWN || doOnDrag) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addEmitter(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

    public void onClickRadio(final View view) {

        switch (view.getId()) {
            case R.id.radio_explosion:
                mParticleType = Explosion.class;
                break;

            case R.id.radio_black_explosion:
                mParticleType = BlackExplosion.class;
                break;

            case R.id.radio_tank_fire:
                mParticleType = TankFire.class;
                break;

            case R.id.radio_gunshots:
                mParticleType = Gunshots.class;
                break;

            case R.id.radio_smoke_puff:
                mParticleType = SmokePuff.class;
                break;

            case R.id.radio_explosion_combo:
                mParticleType = ExplosionCombo.class;
                break;
        }
    }
}
