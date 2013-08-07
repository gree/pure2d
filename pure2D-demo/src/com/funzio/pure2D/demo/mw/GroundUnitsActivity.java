package com.funzio.pure2D.demo.mw;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.crimecity.particles.units.Abrams;
import com.funzio.crimecity.particles.units.Humvee;
import com.funzio.crimecity.particles.units.Leopard;
import com.funzio.crimecity.particles.units.M2Bradley;
import com.funzio.crimecity.particles.units.Unit;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;

public class GroundUnitsActivity extends StageActivity {

    private int mDrags = 0;
    private Class<? extends Unit> mAttackerType = Abrams.class;

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
        return R.layout.stage_mw_ground_attacker;
    }

    private void addAttacker(final float x, final float y) {

        Unit attacker = null;
        CCMapDirection direction = CCMapDirection.SOUTHWEST;// Math.random() >= 0.5 ? CCMapDirection.SOUTHWEST : CCMapDirection.NORTHEAST;
        if (mAttackerType == Abrams.class) {
            attacker = new Abrams(direction);
        } else if (mAttackerType == Leopard.class) {
            attacker = new Leopard(direction);
        } else if (mAttackerType == M2Bradley.class) {
            attacker = new M2Bradley(direction);
        } else if (mAttackerType == Humvee.class) {
            attacker = new Humvee(direction);
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
            case R.id.radio_abrams:
                mAttackerType = Abrams.class;
                break;

            case R.id.radio_leopard:
                mAttackerType = Leopard.class;
                break;

            case R.id.radio_m2bradley:
                mAttackerType = M2Bradley.class;
                break;

            case R.id.radio_humvee:
                mAttackerType = Humvee.class;
                break;
        }
    }
}
