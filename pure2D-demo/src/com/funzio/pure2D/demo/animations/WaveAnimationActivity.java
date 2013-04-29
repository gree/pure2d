package com.funzio.pure2D.demo.animations;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.SinWaveAnimator;

public class WaveAnimationActivity extends AnimationActivity {

    @Override
    protected Animator createAnimator() {
        final SinWaveAnimator animator = new SinWaveAnimator(null);
        animator.setDuration(1000);
        animator.start(0, 0, mDisplaySize.x, mDisplaySize.y);
        animator.setWaveNum(5);
        animator.setWaveRadius(0, 200);

        return animator;
    };

}
