/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.io.File;
import java.io.IOException;

import android.content.res.AssetManager;
import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.RunnableTask;
import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

/**
 * @author long
 */
public class NovaLoader {
    protected final String TAG = NovaLoader.class.getSimpleName();

    protected Listener mListener;
    protected NovaVO mNovaVO;

    public NovaLoader() {
        // TODO Auto-generated constructor stub
    }

    // public void load(final String filePath, final ObjectMapper mapper) {
    // Log.v(TAG, "load(): " + filePath);
    //
    // final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
    // ReadJsonFileTask readTask = new ReadJsonFileTask(filePath, mapper, NovaVO.class);
    // executer.setTaskListener(new TaskListener() {
    //
    // @Override
    // public void onTaskComplete(final Task task) {
    // if (mListener != null) {
    // if (task.isSucceeded()) {
    // Log.v(TAG, "Load success: " + filePath);
    //
    // mNovaVO = (NovaVO) ((ReadJsonFileTask) task).getContent();
    // mListener.onLoad(NovaLoader.this, mNovaVO);
    // } else {
    // Log.e(TAG, "Load failed: " + filePath);
    //
    // mNovaVO = null;
    // mListener.onError(NovaLoader.this);
    // }
    // }
    //
    // }
    // });
    // executer.executeOnPool(readTask);
    // }

    public void load(final AssetManager assets, final String filePath, final ObjectMapper mapper) {
        Log.v(TAG, "load(): " + assets + ", " + filePath);

        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        final RunnableTask readTask = new RunnableTask(new Runnable() {

            @Override
            public void run() {
                try {
                    if (assets == null) {
                        // load from file system
                        mNovaVO = mapper.readValue(new File(filePath), NovaVO.class);
                    } else {
                        // load from asset folder
                        mNovaVO = mapper.readValue(assets.open(filePath), NovaVO.class);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Load failed: " + filePath, e);

                    mNovaVO = null;
                    if (mListener != null) {
                        mListener.onError(NovaLoader.this);
                    }
                    return;
                }

                if (mListener != null) {
                    mListener.onLoad(NovaLoader.this, mNovaVO);
                }
            }
        });

        // start loading
        executer.executeOnPool(readTask);
    }

    public NovaVO getNovaVO() {
        return mNovaVO;
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onLoad(NovaLoader loader, NovaVO vo);

        public void onError(NovaLoader loader);
    }

}
