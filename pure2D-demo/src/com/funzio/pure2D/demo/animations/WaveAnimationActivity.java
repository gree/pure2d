package com.funzio.pure2D.demo.animations;

import com.funzio.pure2D.animators.SinWaveAnimator;
import com.funzio.pure2D.animators.TweenAnimator;

public class WaveAnimationActivity extends AnimationActivity {

    @Override
    protected TweenAnimator createAnimator() {
        final SinWaveAnimator animator = new SinWaveAnimator(null);
        animator.setDuration(1000);
        animator.start(0, 0, mDisplaySize.x, mDisplaySize.y);
        animator.setWaveNum(5);
        animator.setWaveRadius(100);

        return animator;
    };

}
