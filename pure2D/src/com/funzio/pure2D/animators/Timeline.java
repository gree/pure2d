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
public class Timeline implements Manipulator {
    protected Manipulatable mTarget;

    protected int mElapsedTime = 0;
    protected boolean mRunning = false;

    protected List<Action> mActions = new ArrayList<Action>();
    protected int mNumActions = 0;

    @Override
    public void setTarget(final Manipulatable target) {
        mTarget = target;
    }

    @Override
    public Manipulatable getTarget() {
        return mTarget;
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mRunning) {
            mElapsedTime += deltaTime;

            for (int i = 0; i < mNumActions; i++) {
                final Action action = mActions.get(i);
                if (!action.mEnded) {
                    action.update(deltaTime);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Stop and reset the timeline and its actions.
     */
    public void reset() {
        if (mRunning) {
            stop();
        }

        // also reset the actions
        for (int i = 0; i < mNumActions; i++) {
            final Action action = mActions.get(i);
            action.reset();
        }
    }

    /**
     * Start running
     */
    public void start() {
        startElapse(0);
    }

    /**
     * Start with a specific elapsed time
     * 
     * @param elapsedTime
     */
    public void startElapse(final int elapsedTime) {
        if (mRunning) {
            stop();
        }

        mElapsedTime = elapsedTime;
        mRunning = true;
    }

    /**
     * Stop running
     */
    public void stop() {
        mRunning = false;
    }

    /**
     * Jump to some elapsed time
     * 
     * @param elapsedTimeDelta
     */
    public void elapse(final int elapsedTimeDelta) {
        mElapsedTime += elapsedTimeDelta;
    }

    /**
     * Jump to some elapsed time
     * 
     * @param elapsedTimeAt
     */
    public void setElapsedTime(final int elapsedTimeAt) {
        mElapsedTime = elapsedTimeAt;
    }

    public int getElapsedTime() {
        return mElapsedTime;
    }

    public void end() {
        mRunning = false;

        // TODO
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void addAction(final Action action) {
        if (mActions.add(action)) {
            mNumActions++;
        }
    }

    public void removeAction(final Action action) {
        if (mActions.remove(action)) {
            mNumActions--;
        }
    }

    public void clearActions() {
        mActions.clear();
        mNumActions = 0;
    }

    public abstract static class Action implements Runnable {
        public int mStartDelay = 0;
        public int mStepDelay = 0;
        public int mDuration = 0;

        private boolean mStarted = false;
        private boolean mEnded = false;
        private int mAccumulatedTime = 0;
        private int mElapsedTime = 0;

        public Action(final int stepDelay) {
            mStepDelay = stepDelay;
        }

        public Action(final int startDelay, final int stepDelay) {
            mStartDelay = startDelay;
            mStepDelay = stepDelay;
        }

        public Action(final int startDelay, final int stepDelay, final int duration) {
            mStartDelay = startDelay;
            mStepDelay = stepDelay;
            mDuration = duration;
        }

        protected void reset() {
            mStarted = mEnded = false;
            mAccumulatedTime = 0;
        }

        protected void update(final int deltaTime) {
            if (mEnded) {
                return;
            }

            mElapsedTime += deltaTime;
            mAccumulatedTime += deltaTime;

            // duration check if it's set
            if (mDuration > 0 && mElapsedTime >= mDuration) {
                mAccumulatedTime -= mElapsedTime - mDuration;
                // flag done
                mEnded = true;
            }

            if (!mStarted) {
                if (mAccumulatedTime >= mStartDelay) {
                    mStarted = true;
                    mAccumulatedTime -= mStartDelay;

                    // start the first run
                    run();
                }
            }

            // repeated?
            if (mStarted && mStepDelay > 0 && mAccumulatedTime >= mStepDelay) {
                final int steps = mAccumulatedTime / mStepDelay;
                for (int i = 0; i < steps; i++) {
                    run();

                    mAccumulatedTime -= mStepDelay;
                }
            }
        }
    }

}
