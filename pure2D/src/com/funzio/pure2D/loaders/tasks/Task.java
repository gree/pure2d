/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

/**
 * @author long
 */
public interface Task {
    public boolean run();

    public boolean stop();

    public static interface TaskListener {
        void onTaskComplete(final Task task);
    }
}
