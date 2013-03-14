/**
 * 
 */
package com.funzio.pure2D.animators;

/**
 * @author long
 */
public class SequenceAnimator extends GroupAnimator {
    protected int mCurrentIndex = -1;
    protected Animator mCurrentAnimator;

    public SequenceAnimator(final Animator... animators) {
        super(animators);
    }

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
     * @see com.funzio.pure2D.animators.GroupAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        mCurrentIndex = -1;
        next();
    }

    protected Animator next() {
        if (mCurrentIndex < mNumAnimators - 1) {
            mCurrentAnimator = mAnimators.get(++mCurrentIndex);

            // listening
            mCurrentAnimator.setTarget(mTarget);
            mCurrentAnimator.setListener(this);

            // and start
            mCurrentAnimator.start();
        } else {
            mCurrentAnimator = null;
        }

        return mCurrentAnimator;
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Animator.AnimatorListener#onAnimationEnd(com.funzio.pure2D.animators.Animator)
     */
    @Override
    public void onAnimationEnd(final Animator animator) {
        if (next() == null) {
            if (++mLooped > mLoopCount && mLoopCount >= 0) {
                end();
            } else {
                // loop
                start();
            }
        }
    }

}
