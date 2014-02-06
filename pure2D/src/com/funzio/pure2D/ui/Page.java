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
