/**
 * 
 */
package com.funzio.pure2D.loaders;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.funzio.pure2D.loaders.tasks.Task;

/**
 * @author sajjadtabib
 *
 */
public class AsyncTaskExecuter<T extends Task> extends AsyncTask<T, Void, List<T>> {

    private OnTaskCompleteListener<T> mTaskCompleteListener;

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected List<T> doInBackground(final T... taskList) {

        List<T> executedTasks = new ArrayList<T>();
        for (int i = 0; i < taskList.length; i++) {
            T task = taskList[i];
            task.run();
            executedTasks.add(task);
        }

        return executedTasks;
    }

    @Override
    protected void onPostExecute(final List<T> result) {
        super.onPostExecute(result);
        if (mTaskCompleteListener != null) {
            mTaskCompleteListener.onTaskComplete(result);
        }
    }

    public OnTaskCompleteListener<T> getTaskCompleteListener() {
        return mTaskCompleteListener;
    }

    public void setTaskCompleteListener(final OnTaskCompleteListener<T> taskCompleteListener) {
        mTaskCompleteListener = taskCompleteListener;
    }

    public interface OnTaskCompleteListener<T extends Task> {
        void onTaskComplete(List<T> tasks);
    }

}
