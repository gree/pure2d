package com.funzio.pure2D.demo.animations;

import android.view.View;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.animators.ParallelAnimator;
import com.funzio.pure2D.animators.RotateAnimator;
import com.funzio.pure2D.animators.SequenceAnimator;
import com.funzio.pure2D.demo.R;

public class ParallelAnimationActivity extends AnimationActivity {

    private ParallelAnimator mParallel;
    private SequenceAnimator mSequence;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.animations.AnimationActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_parallel_animations;
    }

    @Override
    protected Animator createAnimator() {
        final MoveAnimator move = new MoveAnimator(null);
        move.setDuration(1000);
        move.setValues(mDisplaySizeDiv2.x, 0, mDisplaySizeDiv2.x, mDisplaySize.y - OBJ_SIZE / 2);

        final RotateAnimator rotate = new RotateAnimator(null);
        rotate.setDuration(1000);
        rotate.setValues(0, 360);

        mSequence = new SequenceAnimator();
        mSequence.add(move);
        mSequence.add(rotate);

        mParallel = new ParallelAnimator();
        mParallel.add(move);
        mParallel.add(rotate);
        mParallel.start();

        return mParallel;
    };

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.animations.AnimationActivity#onClickRadio(android.view.View)
     */
    @Override
    public void onClickRadio(final View view) {
        // general switches
        switch (view.getId()) {
            case R.id.radio_parallel:
                setAnimator(mParallel);
                break;

            case R.id.radio_sequence:
                setAnimator(mSequence);
                break;

            case R.id.radio_once:
                mParallel.setLoopCount(0);
                mSequence.setLoopCount(0);
                break;

            case R.id.radio_repeat:
                mParallel.setLoopCount(-1);
                mSequence.setLoopCount(-1);
                break;
        }

        super.onClickRadio(view);
    }
}
