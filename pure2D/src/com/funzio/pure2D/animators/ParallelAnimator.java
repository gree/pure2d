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
package com.funzio.pure2D.animators;

/**
 * @author long
 */
public class ParallelAnimator extends GroupAnimator {
    private int mDoneAnimators = 0;

    public ParallelAnimator(final Animator... animators) {
        super(animators);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Manipulator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            // update all
            for (int i = 0; i < mNumAnimators; i++) {
                mAnimators.get(i).update(deltaTime);
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.GroupAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        // and start
        startAnimators();
    }

    /**
     * Restart all the child animators
     */
    protected void startAnimators() {
        mDoneAnimators = 0;

        for (int i = 0; i < mNumAnimators; i++) {
            final Animator animator = mAnimators.get(i);
            // listening
            animator.setListener(this);
            animator.setTarget(mTarget);

            // and start
            animator.start();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator.AnimatorListener#onAnimationEnd(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationEnd(final Animator animator) {
        if (++mDoneAnimators == mNumAnimators) {
            if (++mLooped > mLoopCount && mLoopCount >= 0) {
                end();
            } else {
                // loop
                startAnimators();
            }
        }
    }

}
