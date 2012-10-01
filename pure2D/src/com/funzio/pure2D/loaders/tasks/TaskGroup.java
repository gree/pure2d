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
public class TaskGroup implements Task, Task.Stoppable {
    private static final String TAG = TaskGroup.class.getSimpleName();

    private List<Task> mTasks = new ArrayList<Task>();
    private long mTaskDelay = 0;
    private TaskListener mTaskListener;

    private Task mCurrentTask;

    private boolean mRunning = false;

    // private Handler mHandler;
    //
    // private int mCurrentIndex = -1;
    //
    // private Runnable mNextTaskRunnable = new Runnable() {
    //
    // @Override
    // public void run() {
    // final Task task = mTasks.get(++mCurrentIndex);
    // task.run();
    //
    // if (mTasks.size() > 0) {
    // // schedule next task
    // mHandler.postDelayed(mNextTaskRunnable, mTaskDelay);
    // }
    // }
    // };

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

        mRunning = true;

        for (int i = 0; i < size; i++) {

            // interrupted?
            if (!mRunning) {
                Log.v("long", "bingoooooooooooo!");
                return false;
            }

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

        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task.Stoppable#stop()
     */
    @Override
    public boolean stop() {
        Log.v("long", "awesomeeeeeeeeeee stop()");
        if (!mRunning) {
            return false;
        }

        mRunning = false;

        if (mCurrentTask instanceof Stoppable) {
            ((Stoppable) mCurrentTask).stop();
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
