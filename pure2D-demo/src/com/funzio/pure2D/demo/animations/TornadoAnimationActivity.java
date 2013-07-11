package com.funzio.pure2D.demo.animations;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.TornadoAnimator;

public class TornadoAnimationActivity extends AnimationActivity {

    @Override
    protected Animator createAnimator() {
        final TornadoAnimator animator = new TornadoAnimator(null);
        animator.setDuration(5000);
        animator.setCircles(mDisplaySizeDiv2.x, 10, 0.25f, BOUNCE);
        animator.start(mDisplaySizeDiv2.x, OBJ_SIZE / 2f, mDisplaySizeDiv2.x, mDisplaySize.y - OBJ_SIZE / 2f);

        return animator;
    };

}
