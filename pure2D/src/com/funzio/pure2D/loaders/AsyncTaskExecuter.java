/**
 * 
 */
package com.funzio.pure2D.loaders;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.loaders.tasks.Task.TaskListener;

/**
 * @author sajjadtabib
 */
public class AsyncTaskExecuter<T extends Task> extends AsyncTask<T, Void, List<T>> {

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
                mTaskListener.onTaskComplete(task);
            }

            // add to the list
            executedTasks.add(task);
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

    public TaskListener getTaskListener() {
        return mTaskListener;
    }

    public void setTaskListener(final TaskListener taskListener) {
        mTaskListener = taskListener;
    }

}
