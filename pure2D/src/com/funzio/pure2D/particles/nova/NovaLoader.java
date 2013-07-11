/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;

import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

/**
 * @author long
 */
public class NovaLoader {
    protected final String TAG = NovaLoader.class.getSimpleName();

    protected Listener mListener;

    public NovaLoader(final Listener listener) {
        mListener = listener;
    }

    /**
     * Load a specific Nova file, asynchronously. Note: some old Android version required to call this on UI Thread first off.
     * 
     * @param assets
     * @param filePath
     */
    public void loadAsync(final AssetManager assets, final String filePath) {
        Log.v(TAG, "loadAsync(): " + filePath);

        final ReadTextFileTask readTask = new ReadTextFileTask(assets, filePath);
        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        executer.setTaskListener(new Task.TaskListener() {

            @Override
            public void onTaskComplete(final Task task) {

                if (task.isSucceeded()) {
                    Log.v(TAG, "Load success: " + filePath);

                    try {
                        if (mListener != null) {
                            mListener.onLoad(NovaLoader.this, filePath, new NovaVO(((ReadTextFileTask) task).getContent()));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Load failed: " + filePath, e);

                        if (mListener != null) {
                            mListener.onError(NovaLoader.this, filePath);
                        }
                    }

                } else {
                    Log.e(TAG, "Load failed: " + filePath);

                    if (mListener != null) {
                        mListener.onError(NovaLoader.this, filePath);
                    }
                }

            }

        });

        // start loading
        executer.executeOnPool(readTask);
    }

    /**
     * Load a specific Nova file, synchronously
     * 
     * @param assets
     * @param filePath
     */
    public void load(final AssetManager assets, final String filePath) {
        Log.v(TAG, "load(): " + filePath);

        final ReadTextFileTask readTask = new ReadTextFileTask(assets, filePath);
        if (readTask.run()) {
            Log.v(TAG, "Load success: " + filePath);

            try {
                if (mListener != null) {
                    mListener.onLoad(NovaLoader.this, filePath, new NovaVO(readTask.getContent()));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Load failed: " + filePath, e);

                if (mListener != null) {
                    mListener.onError(NovaLoader.this, filePath);
                }
            }

        } else {
            Log.e(TAG, "Load failed: " + filePath);

            if (mListener != null) {
                mListener.onError(NovaLoader.this, filePath);
            }
        }
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onLoad(NovaLoader loader, String filePath, NovaVO vo);

        public void onError(NovaLoader loader, String filePath);
    }

}
