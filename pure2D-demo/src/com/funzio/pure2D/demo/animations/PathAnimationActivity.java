package com.funzio.pure2D.demo.animations;

import android.graphics.PointF;

import com.funzio.pure2D.animators.PathAnimator;
import com.funzio.pure2D.animators.TweenAnimator;

public class PathAnimationActivity extends AnimationActivity {

    @Override
    protected TweenAnimator createAnimator() {
        final PathAnimator animator = new PathAnimator(null);
        animator.setDuration(2000);
        animator.start(new PointF(0, mDisplaySizeDiv2.y), new PointF(0, mDisplaySize.y), new PointF(mDisplaySize.x, mDisplaySizeDiv2.y), new PointF(mDisplaySize.x, mDisplaySize.y), new PointF(0,
                mDisplaySizeDiv2.y));

        return animator;
    };

}
