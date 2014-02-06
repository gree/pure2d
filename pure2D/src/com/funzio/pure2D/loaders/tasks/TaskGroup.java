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
package com.funzio.pure2D.loaders.tasks;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author long
 */
public class TaskGroup implements Task, Retriable {
    private static final String TAG = TaskGroup.class.getSimpleName();

    protected List<Task> mTasks = new ArrayList<Task>();

    protected TaskListener mTaskListener;
    protected long mTaskDelay = 0;
    protected int mNumTasksCompleted = 0;

    private boolean mSucceeded = false;

    private int mRetriedAlready = 0; // number of times already retried
    private int mRetryMax = 0; // max number of retries
    private int mRetryDelay = 0; // delay between retries

    public TaskGroup() {
    }

    public TaskGroup(final int retryMax) {
        mRetryMax = retryMax;
    }

    public TaskGroup(final int retryMax, final int retryDelay) {
        mRetryMax = retryMax;
        mRetryDelay = retryDelay;
    }

    public void reset() {
        mSucceeded = false;
        mRetriedAlready = 0;
        mNumTasksCompleted = 0;
    }

    public void addTask(final Task task) {
        mTasks.add(task);
    }

    public void addTasks(final Task... tasks) {
        Log.v(TAG, "addTasks(): " + tasks);
        if (tasks == null) {
            return;
        }

        for (final Task task : tasks) {
            mTasks.add(task);
        }
    }

    public boolean addTasks(final List<Task> tasks) {
        Log.v(TAG, "addTasks(): " + tasks.size());

        return mTasks.addAll(tasks);
    }

    public void removeTask(final Task task) {
        mTasks.remove(task);
    }

    public void clearTasks() {
        mTasks.clear();
    }

    @Override
    public boolean run() {

        mSucceeded = runTasks();
        if (!mSucceeded) {
            mSucceeded = retry();
        }

        return mSucceeded;
    }

    protected boolean runTasks() {
        final int size = mTasks.size();

        if (size == 0) {
            // no task to run, done!
            return true;
        }

        boolean success = true;
        for (int i = 0; i < size; i++) {

            final Task task = mTasks.get(i);

            // only run task that has not succeeded yet
            if (task.isSucceeded()) {
                continue;
            }

            // run now
            boolean taskSuccess = task.run();
            success &= taskSuccess;
            if (mTaskListener != null) {
                // callback
                if (taskSuccess) {
                    mNumTasksCompleted++;
                }

                // complete doesn't mean success
                mTaskListener.onTaskComplete(task);
            }

            if (mTaskDelay > 0) {
                try {
                    Thread.sleep(mTaskDelay);
                } catch (InterruptedException ex) {
                    Log.e(TAG, "INTERRUPTED ERROR!", ex);
                }
            }
        }

        return success;
    }

    public float getProgress() {
        return (float) mNumTasksCompleted / (float) mTasks.size();
    }

    protected boolean retry() {
        if (mRetriedAlready < mRetryMax || mRetryMax == RETRY_UNLIMITED) {
            if (mRetryDelay > 0) {
                try {
                    Thread.sleep(mRetryDelay);
                } catch (InterruptedException e) {
                    // TODO nothing
                }
            }
            mRetriedAlready++;

            // run the failed tasks again
            if (runTasks()) {
                return true;
            } else {
                // recursively retry
                return retry();
            }
        }

        return false;
    }

    public List<Task> getFailedTasks() {
        final int size = mTasks.size();
        final ArrayList<Task> list = new ArrayList<Task>();
        for (int i = 0; i < size; i++) {

            final Task task = mTasks.get(i);
            // only get the task that has not succeeded yet
            if (!task.isSucceeded()) {
                list.add(task);
            }

        }

        return list;
    }

    public boolean isSucceeded() {
        return mSucceeded;
    }

    public List<Task> getTasks() {
        return mTasks;
    }

    public long getTaskDelay() {
        return mTaskDelay;
    }

    public void setTaskDelay(final long taskDelay) {
        mTaskDelay = taskDelay;
    }

    public TaskListener getTaskListener() {
        return mTaskListener;
    }

    public void setTaskListener(final TaskListener taskListener) {
        mTaskListener = taskListener;
    }

    public int getRetryMax() {
        return mRetryMax;
    }

    public void setRetryMax(final int retryMax) {
        mRetryMax = retryMax;
    }

    public int getRetryDelay() {
        return mRetryDelay;
    }

    public void setRetryDelay(final int retryDelay) {
        mRetryDelay = retryDelay;
    }
}
