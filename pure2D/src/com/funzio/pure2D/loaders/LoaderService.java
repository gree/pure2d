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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
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

    public static final String INTENT_ON_STARTED = "INTENT_ON_STARTED";
    public static final String INTENT_ON_FINISHED = "INTENT_ON_FINISHED";

    // tasks
    private Vector<Task> mTasks = new Vector<Task>();
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
    }

    @Override
    /**
     * Note: this gets called on the Main thread
     */
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
        if (!(Thread.currentThread().getUncaughtExceptionHandler() instanceof DefaultUncaughtExceptionHandler)) {
            // This is the background thread's uncaught exception handler; should happen in background thread!
            Thread.currentThread().setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
        }

        if (intent == null) {
            Log.e(TAG, "onHandleIntent() with NULL intent!", new Exception());
            return;
        } else {
            Log.v(TAG, "onHandleIntent(), " + intent.getAction());
        }

        if (mBatteryLow) {
            stopSelf();
            return;
        }

        if (intent.getAction().equals(getIntentAction(INTENT_START))) {
            runTasks();
        }
    }

    protected boolean addTask(final Task task) {
        Log.v(TAG, "addTask(): " + task);

        return mTasks.add(task);
    }

    protected boolean addTasks(final Task... tasks) {
        Log.v(TAG, "addTasks(): " + tasks);
        if (tasks == null) {
            return false;
        }

        boolean success = true;
        for (final Task task : tasks) {
            success &= mTasks.add(task);
        }

        return success;
    }

    protected boolean addTasks(final List<Task> tasks) {
        Log.v(TAG, "addTasks(): " + tasks.size());

        return mTasks.addAll(tasks);
    }

    protected boolean removeTask(final Task task) {
        return mTasks.remove(task);
    }

    protected void clearTasks() {
        mTasks.clear();
    }

    protected boolean runTasks() {
        final int size = mTasks.size();
        if (size == 0) {
            return false;
        }

        // flag
        // broadcast started event
        sendBroadcast(new Intent(getIntentAction(INTENT_ON_STARTED)));

        // run the tasks
        for (int i = 0; i < size; i++) {

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
