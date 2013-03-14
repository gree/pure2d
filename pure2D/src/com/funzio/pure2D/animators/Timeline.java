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
    protected int mNumEndedActions = 0;

    protected int mDuration = 0; // 0 == unlimited
    protected Listener mListener;

    public Timeline(final int duration, final Listener listener) {
        mDuration = duration;
        mListener = listener;
    }

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

            // has duration? check it
            if (mDuration > 0 && mElapsedTime >= mDuration) {
                if (mListener != null) {
                    // callback
                    mListener.onTimelineComplete(this);
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

        mNumEndedActions = 0;
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

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public void end() {
        mRunning = false;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void addAction(final Action action) {
        if (mActions.add(action)) {
            // couple
            action.mTimeline = this;
            mNumActions++;
        }
    }

    public void removeAction(final Action action) {
        if (mActions.remove(action)) {
            // couple
            action.mTimeline = null;
            mNumActions--;
        }
    }

    public void clearActions() {
        mActions.clear();
        mNumActions = 0;
    }

    protected void onActionEnd(final Action action) {
        if (++mNumEndedActions == mNumActions && mDuration <= 0) {
            // complete!
            if (mListener != null) {
                // callback
                mListener.onTimelineComplete(this);
            }
        }
    }

    /**
     * Timeline Listener
     * 
     * @author long
     */
    public static interface Listener {
        public void onTimelineComplete(Timeline timeline);
    }

    /**
     * Timeline Action
     * 
     * @author long
     */
    public abstract static class Action implements Runnable {
        public int mStartDelay = 0;
        public int mStepDelay = 0;
        public int mDuration = 0;

        private boolean mStarted = false;
        private boolean mEnded = false;
        private int mAccumulatedTime = 0;
        private int mElapsedTime = 0;

        // the assigned timeline
        private Timeline mTimeline;

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

            // accumulate
            mAccumulatedTime += deltaTime;

            if (!mStarted) {
                if (mAccumulatedTime >= mStartDelay) {
                    // woohoo!
                    mStarted = true;

                    // start elapsing
                    mElapsedTime = mAccumulatedTime - mStartDelay;
                    mAccumulatedTime -= mStartDelay;

                    // start the first run
                    run();
                }
            } else {
                // elapse more
                mElapsedTime += deltaTime;
            }

            if (mStarted) {
                // duration check if it's set
                if (mDuration > 0 && mElapsedTime >= mDuration) {
                    mAccumulatedTime -= mElapsedTime - mDuration;
                    // flag done
                    mEnded = true;
                }

                // repeated?
                if (mStepDelay > 0 && mAccumulatedTime >= mStepDelay) {
                    final int steps = mAccumulatedTime / mStepDelay;
                    for (int i = 0; i < steps; i++) {
                        run();

                        mAccumulatedTime -= mStepDelay;
                    }
                }

                if (mEnded) {
                    // callback
                    mTimeline.onActionEnd(this);
                }
            }
        }
    }

}
