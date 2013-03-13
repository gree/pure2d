/**
 * 
 */
package com.funzio.pure2D.animators;

import java.util.ArrayList;
import java.util.List;

import com.funzio.pure2D.Manipulatable;

/**
 * @author long
 */
public abstract class GroupAnimator extends BaseAnimator implements Animator.AnimatorListener {
    protected List<Animator> mAnimators = new ArrayList<Animator>();
    protected int mNumAnimators = 0;

    // looping
    protected int mLooped = 0;
    protected int mLoopCount = 0; // negative = UNLIMITED

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#start()
     */
    @Override
    public void start() {
        super.start();

        mLooped = 0;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#setTarget(com.funzio.pure2D.Manipulatable)
     */
    @Override
    public void setTarget(final Manipulatable target) {
        super.setTarget(target);

        for (int i = 0; i < mNumAnimators; i++) {
            mAnimators.get(i).setTarget(target);
        }
    }

    public void add(final Animator animator) {
        if (mAnimators.add(animator)) {
            mNumAnimators++;
        }
    }

    public void remove(final Animator animator) {
        if (mAnimators.remove(animator)) {
            mNumAnimators--;
        }
    }

    public void clear() {
        mAnimators.clear();
        mNumAnimators = 0;
    }

    public int getLoopCount() {
        return mLoopCount;
    }

    public void setLoopCount(final int value) {
        mLoopCount = value;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator.AnimatorListener#onAnimationUpdate(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationUpdate(final Animator animator, final float value) {
        if (mListener != null) {
            mListener.onAnimationUpdate(animator, value);
        }
    }

}
