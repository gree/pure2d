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
     * @see com.funzio.pure2D.animators.BaseAnimator#start()
     */
    @Override
    public void start() {
        super.start();

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
