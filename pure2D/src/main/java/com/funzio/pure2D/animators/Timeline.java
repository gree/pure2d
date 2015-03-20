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

import java.util.ArrayList;

import com.funzio.pure2D.Manipulatable;

/**
 * @author long
 */
public class Timeline implements Manipulator {
    protected Manipulatable mTarget;

    protected int mElapsedTime = 0;
    protected boolean mRunning = false;

    protected ArrayList<Action> mActions = new ArrayList<Action>();
    protected int mNumActions = 0;
    protected int mNumEndedActions = 0;

    protected int mLifespan = 0; // <=0 ~ unlimited
    protected Listener mListener;

    public Timeline(final int lifespan, final Listener listener) {
        mLifespan = lifespan;
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
            if (mLifespan > 0 && mElapsedTime >= mLifespan) {
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

    public int getLifespan() {
        return mLifespan;
    }

    public void setLifespan(final int lifespan) {
        mLifespan = lifespan;
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
        if (++mNumEndedActions == mNumActions && mLifespan <= 0) {
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
