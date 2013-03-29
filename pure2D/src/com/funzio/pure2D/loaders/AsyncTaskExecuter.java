/**
 * 
 */
package com.funzio.pure2D.loaders;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.loaders.tasks.Task.TaskListener;

/**
 * @author sajjadtabib
 */
public class AsyncTaskExecuter<T extends Task> extends AsyncTask<T, Float, List<T>> {

    private TaskListener mTaskListener;
    protected int mNumTasks = 0;
    protected int mNumTasksCompleted = 0;

    private boolean mStopWhenTaskFailed = false;

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected List<T> doInBackground(final T... taskList) {
        mNumTasks = taskList.length;

        final List<T> executedTasks = new ArrayList<T>();
        for (int i = 0; i < mNumTasks; i++) {
            final T task = taskList[i];
            // execute now
            if (task.run()) {
                mNumTasksCompleted++;
            }

            // callback
            if (mTaskListener != null) {
                mTaskListener.onTaskComplete(task);
            }

            // add to the list
            executedTasks.add(task);

            if (mStopWhenTaskFailed && !task.isSucceeded()) {
                break;
            }
        }

        return executedTasks;
    }

    // @Override
    // protected void onPostExecute(final List<T> result) {
    // super.onPostExecute(result);
    //
    // if (mTaskListener != null) {
    // final int size = result.size();
    // for (int i = 0; i < size; i++) {
    // mTaskListener.onTaskComplete(result.get(i));
    // }
    // }
    // }

    public float getProgress() {
        return (float) mNumTasksCompleted / (float) mNumTasks;
    }

    public TaskListener getTaskListener() {
        return mTaskListener;
    }

    public void setTaskListener(final TaskListener taskListener) {
        mTaskListener = taskListener;
    }

    public boolean isStopWhenTaskFailed() {
        return mStopWhenTaskFailed;
    }

    public void setStopWhenTaskFailed(final boolean stopWhenTaskFailed) {
        mStopWhenTaskFailed = stopWhenTaskFailed;
    }

    /**
     * If Thread Pool supported, use it to execute; otherwise execute normally
     * 
     * @param params
     * @return
     */
    @SuppressLint("NewApi")
    public AsyncTask<T, Float, List<T>> executeOnPool(final T... params) {
        // run now
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            return execute(params);
        }

    }

}
