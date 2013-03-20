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
import com.funzio.pure2D.loaders.tasks.Task.TaskListener2;

/**
 * @author sajjadtabib
 */
public class AsyncTaskExecuter<T extends Task> extends AsyncTask<T, Float, List<T>> {

    private TaskListener mTaskListener;

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected List<T> doInBackground(final T... taskList) {

        final List<T> executedTasks = new ArrayList<T>();
        for (int i = 0; i < taskList.length; i++) {
            final T task = taskList[i];
            // execute now
            task.run();

            // callback
            if (mTaskListener != null) {
                publishProgress((float) i / (float) taskList.length);
                mTaskListener.onTaskComplete(task);
            }

            // add to the list
            executedTasks.add(task);
        }

        return executedTasks;
    }

    @Override
    protected void onProgressUpdate(final Float... progress) {
        if (mTaskListener instanceof TaskListener2) {
            ((TaskListener2) mTaskListener).onTaskProgress(progress[0]);
        }
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

    public TaskListener getTaskListener() {
        return mTaskListener;
    }

    public void setTaskListener(final TaskListener taskListener) {
        mTaskListener = taskListener;
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
