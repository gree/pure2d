package com.funzio.pure2D.demo.animations;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.WhirlAnimator;

public class WhirlAnimationActivity extends AnimationActivity {

    @Override
    protected Animator createAnimator() {
        final WhirlAnimator animator = new WhirlAnimator(null);
        animator.setDuration(5000);
        // animator.setCircleInterpolator(ACCELERATE);
        animator.start(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y, 0, mDisplaySizeDiv2.x - OBJ_SIZE * 0.5f, 0, 1440);

        return animator;
    };

}
