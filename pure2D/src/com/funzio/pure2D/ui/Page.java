/**
 * 
 */
package com.funzio.pure2D.ui;

import com.funzio.pure2D.animators.Animator;
import com.funzio.pure2D.animators.Animator.AnimatorListener;
import com.funzio.pure2D.animators.MoveAnimator;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long.ngo
 */
public class Page extends DisplayGroup implements Pageable, AnimatorListener {

    protected static final int TRANSITION_DURATION = 1000;

    protected MoveAnimator mTransitionAnimator;
    protected TransitionListener mTransitionListener;
    protected boolean mPageActive = false;
    protected boolean mPageFloating = false;

    public Page() {
        super();

        mTransitionAnimator = new MoveAnimator(NovaConfig.INTER_DECELERATE);
        mTransitionAnimator.setDuration(TRANSITION_DURATION);
        mTransitionAnimator.setListener(this);
        addManipulator(mTransitionAnimator);
    }

    public boolean isPageFloating() {
        return mPageFloating;
    }

    public void setPageFloating(final boolean pageFloating) {
        mPageFloating = pageFloating;
    }

    @Override
    public void transitionIn(final boolean pushing) {
        mPageActive = true;

        mTransitionAnimator.stop();
        mTransitionAnimator.start(0, -mSize.y, 0, 0);
    }

    @Override
    public void transitionOut(final boolean pushing) {
        mPageActive = false;
        mTransitionAnimator.stop();
        mTransitionAnimator.start(0, -mSize.y);
    }

    /**
     * For internal use only!
     * 
     * @hide
     */
    @Override
    public void setTransitionListener(final TransitionListener listener) {
        mTransitionListener = listener;
    }

    /**
     * For internal use only!
     * 
     * @hide
     */
    @Override
    public TransitionListener getTransitionListener() {
        return mTransitionListener;
    }

    @Override
    public boolean isPageActive() {
        return mPageActive;
    }

    @Override
    public void onAnimationEnd(final Animator animator) {
        if (animator == mTransitionAnimator) {
            if (mTransitionListener != null) {
                if (mPageActive) {
                    mTransitionListener.onTransitionInComplete(this);
                } else {
                    mTransitionListener.onTransitionOutComplete(this);
                }
            }
        }
    }

    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        // TODO Auto-generated method stub

    }
}
