/**
 * 
 */
package com.funzio.pure2D.animators;

import java.util.ArrayList;
import java.util.List;

/**
 * @author long
 */
public class SequenceAnimator extends BaseAnimator implements Animator.AnimatorListener {
    private List<Animator> mAnimators = new ArrayList<Animator>();
    private int mCurrentIndex = -1;
    private Animator mCurrentAnimator;
    private int mNumAnimators = 0;

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Manipulator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            if (mCurrentAnimator != null) {
                mCurrentAnimator.update(deltaTime);
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#start()
     */
    @Override
    public void start() {
        super.start();

        mCurrentIndex = -1;
        next();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#end()
     */
    @Override
    public void end() {
        super.end();

        mCurrentAnimator = null;
    }

    synchronized protected Animator next() {
        if (mCurrentIndex < mNumAnimators - 1) {
            mCurrentAnimator = mAnimators.get(++mCurrentIndex);

            // set up
            mCurrentAnimator.setTarget(mTarget);
            mCurrentAnimator.setListener(this);

            // and start
            mCurrentAnimator.start();
        } else {
            mCurrentAnimator = null;
        }

        return mCurrentAnimator;
    }

    synchronized public void add(final Animator action) {
        mAnimators.add(action);
        mNumAnimators++;
    }

    synchronized public void remove(final Animator action) {
        mAnimators.remove(action);
        mNumAnimators--;
    }

    synchronized public void clear() {
        mAnimators.clear();
        mNumAnimators = 0;
        mCurrentIndex = -1;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator.AnimatorListener#onAnimationEnd(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationEnd(final Animator animator) {
        if (next() == null) {
            end();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator.AnimatorListener#onAnimationUpdate(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationUpdate(final Animator animator) {
        if (mListener != null) {
            mListener.onAnimationUpdate(animator);
        }
    }

}
