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

/**
 * @author long
 */
public class ActionQueue extends BaseAnimator {
    private ArrayList<Action> mActions = new ArrayList<Action>();
    private Action mCurrentAction;
    private int mCurrentIndex = -1;
    private int mNumActions = 0;

    // some default values, do not change this
    private boolean mAutoStart = false;
    private boolean mAutoRemove = true;

    private Action mDelayAction = new Action() {
        @Override
        public void run() {
            // nothing here, just delay
        }
    };

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Manipulator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            if (mCurrentAction != null && mElapsedTime >= mCurrentAction.mDelay) {
                mCurrentAction.run();
                mElapsedTime -= mCurrentAction.mDelay;

                // if the current action has a duration
                if (mElapsedTime >= mCurrentAction.mDuration) {
                    mElapsedTime -= mCurrentAction.mDuration;
                    // has next?
                    if (next() == null) {
                        end();
                    }
                } else {
                    // add delay
                    mDelayAction.mDelay = mCurrentAction.mDuration;
                    mCurrentAction = mDelayAction;
                }
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        next();
    }

    protected Action next() {
        if (mNumActions > 0) {
            if (mAutoRemove) {
                mCurrentIndex = 0;
                mCurrentAction = mActions.remove(mCurrentIndex);
                mNumActions--;
            } else {
                mCurrentIndex = (++mCurrentIndex) % mNumActions;
                mCurrentAction = mActions.get(mCurrentIndex);
            }
        } else {
            mCurrentIndex = -1;
            mCurrentAction = null;
        }

        return mCurrentAction;
    }

    public void add(final Action action) {
        if (mActions.add(action)) {
            mNumActions++;

            // auto start
            if (mAutoStart && !mRunning) {
                start();
            }
        }
    }

    public void remove(final Action action) {
        if (mActions.remove(action)) {
            mNumActions--;
        }
    }

    public void clear() {
        mActions.clear();
        mNumActions = 0;
        mCurrentIndex = -1;
        mCurrentAction = null;
    }

    /**
     * @return the autoStart
     */
    public boolean isAutoStart() {
        return mAutoStart;
    }

    /**
     * @param autoStart the autoStart to set
     */
    public void setAutoStart(final boolean autoStart) {
        mAutoStart = autoStart;
    }

    public boolean isAutoRemove() {
        return mAutoRemove;
    }

    public void setAutoRemove(final boolean autoRemove) {
        mAutoRemove = autoRemove;
    }

    public abstract static class Action {
        public int mDelay = 0;
        public int mDuration = 0;

        public Action() {
        }

        public abstract void run();

        public Action(final int delay) {
            mDelay = delay;
        }

        public Action(final int delay, final int duration) {
            mDelay = delay;
            mDuration = duration;
        }
    }

}
