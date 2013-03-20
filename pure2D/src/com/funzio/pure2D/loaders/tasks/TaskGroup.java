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

    private List<Task> mTasks = new ArrayList<Task>();

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
                if (taskSuccess && mTaskListener instanceof TaskListener2) {
                    ((TaskListener2) mTaskListener).onTaskProgress((float) ++mNumTasksCompleted / (float) size);
                }
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

    protected boolean addTasks(final List<Task> tasks) {
        Log.v(TAG, "addTasks(), " + tasks.size());

        return mTasks.addAll(tasks);
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
