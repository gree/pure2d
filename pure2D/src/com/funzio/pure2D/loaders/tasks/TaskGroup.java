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
        final int size = mTasks.size();

        if (size == 0) {
            // no task to run
            return false;
        }

        for (int i = 0; i < size; i++) {

            final Task task = mTasks.get(i);
            task.run();

            if (mTaskListener != null) {
                // callback
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

        return true;
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
}
