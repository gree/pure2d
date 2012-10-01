/**
 * 
 */
package com.funzio.pure2D.loaders;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Vector;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.funzio.pure2D.loaders.tasks.IntentTask;
import com.funzio.pure2D.loaders.tasks.Task;

/**
 * @author long
 */
public class LoaderService extends IntentService {
    private static final String TAG = LoaderService.class.getSimpleName();

    public static final float LOW_BATTERY_THRESHOLD = 0.3f;
    public static final int DEFAULT_TASK_DELAY = 0;

    public static final String INTENT_START = "INTENT_START";
    public static final String INTENT_STOP = "INTENT_STOP";

    public static final String INTENT_ON_STARTED = "INTENT_ON_STARTED";
    public static final String INTENT_ON_STOPPED = "INTENT_ON_STOPPED";
    public static final String INTENT_ON_FINISHED = "INTENT_ON_FINISHED";

    // tasks
    private Vector<Task> mTasks = new Vector<Task>();
    private volatile boolean mRunning = false;
    protected int mTaskDelay = DEFAULT_TASK_DELAY;

    // low battery handling
    private final String mName;
    private boolean mBatteryLow;
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            mBatteryLow = isBatteryLow(intent);
        }
    };

    public LoaderService(final String name) {
        super(name);

        mName = name;
        Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate(), " + mName);

        super.onCreate();

        // listen to some events
        registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy(), " + mName);

        super.onDestroy();

        // clean up
        unregisterReceiver(mBatteryInfoReceiver);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.v(TAG, "onHandleIntent(), " + intent.getAction());

        if (mBatteryLow) {
            stopSelf();
            return;
        }

        if (intent.getAction().equals(getIntentAction(INTENT_START))) {
            runTasks(intent);
        } else if (intent.getAction().equals(getIntentAction(INTENT_STOP))) {
            // flag
            mRunning = false;
            stopSelf();
        }
    }

    protected boolean addTask(final Task task) {
        Log.v(TAG, "addTask(), " + task.toString());

        return mTasks.add(task);
    }

    protected boolean removeTask(final Task task) {
        return mTasks.remove(task);
    }

    protected void clearTasks() {
        mTasks.clear();
    }

    protected boolean runTasks(final Intent intent) {
        final int size = mTasks.size();
        if (size == 0 || mRunning) {
            return false;
        }

        // flag
        mRunning = true;
        // broadcast started event
        sendBroadcast(new Intent(getIntentAction(INTENT_ON_STARTED)));

        // run the tasks
        for (int i = 0; i < size; i++) {

            // interrupted?
            if (!mRunning) {
                // broadcast stopped event
                sendBroadcast(new Intent(getIntentAction(INTENT_ON_STOPPED)));
                return false;
            }

            final Task task = mTasks.remove(0);
            task.run();
            // if there is complete intent
            if (task instanceof IntentTask) {
                final Intent taskCompleteIntent = ((IntentTask) task).getCompleteIntent();
                if (taskCompleteIntent != null) {
                    // broadcast task complete
                    sendBroadcast(taskCompleteIntent);
                }
            }

            if (mTaskDelay > 0) {
                try {
                    Thread.sleep(mTaskDelay);
                } catch (InterruptedException ex) {
                    Log.e(TAG, "INTERRUPTED ERROR!", ex);
                }
            }
        }

        // broadcast finished event
        sendBroadcast(new Intent(getIntentAction(INTENT_ON_FINISHED)));

        return true;
    }

    private boolean isBatteryLow(final Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0
                && intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / (float) intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100) < LOW_BATTERY_THRESHOLD;
    }

    protected String getIntentAction(final String action) {
        return mName + "." + action;
    }

    public String getName() {
        return mName;
    }

    /**
     * @return the delay between tasks
     */
    public int getTaskDelay() {
        return mTaskDelay;
    }

    /**
     * Set the delay between tasks
     * 
     * @param delay
     */
    public void setTaskDelay(final int delay) {
        mTaskDelay = delay;
    }

    private static class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread thread, final Throwable ex) {
            Log.e(TAG, "UNCAUGHT ERROR!", ex);
        }
    }
}
