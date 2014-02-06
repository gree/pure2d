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

    protected boolean mStopOnTaskFailed = false;

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

            if (mStopOnTaskFailed && !task.isSucceeded()) {
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

    public boolean isStopOnTaskFailed() {
        return mStopOnTaskFailed;
    }

    public void setStopOnTaskFailed(final boolean stopOnTaskFailed) {
        mStopOnTaskFailed = stopOnTaskFailed;
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
