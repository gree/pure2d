/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

/**
 * @author long
 */
public interface Task {

    public void reset();

    public boolean run();

    public boolean isSucceeded();

    public static interface TaskListener {
        void onTaskComplete(final Task task);
    }
}
