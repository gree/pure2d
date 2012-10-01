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
public class TaskGroup implements Task {
    private static final String TAG = TaskGroup.class.getSimpleName();

    private List<Task> mTasks = new ArrayList<Task>();
    private long mTaskDelay = 0;
    private TaskListener mTaskListener;
    private boolean mRunning = false;
    private Task mCurrentTask;

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
        mRunning = true;

        final int size = mTasks.size();
        for (int i = 0; i < size; i++) {

            mCurrentTask = mTasks.get(i);
            mCurrentTask.run();

            if (mTaskListener != null) {
                // callback
                mTaskListener.onTaskComplete(mCurrentTask);
            }

            if (mTaskDelay > 0) {
                try {
                    Thread.sleep(mTaskDelay);
                } catch (InterruptedException ex) {
                    Log.e(TAG, "INTERRUPTED ERROR!", ex);
                }
            }
        }

        // clear
        mCurrentTask = null;

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task#stop()
     */
    @Override
    public boolean stop() {
        if (!mRunning) {
            return false;
        }

        // unflag
        mRunning = false;

        // stop current task if there's any
        if (mCurrentTask != null) {
            mCurrentTask.stop();
            mCurrentTask = null;
        }

        return true;
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
}
