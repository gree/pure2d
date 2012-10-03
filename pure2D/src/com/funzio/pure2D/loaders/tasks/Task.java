/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

/**
 * @author long
 */
public interface Task {
    public static boolean LOGGING = true;

    public boolean run();

    public static interface TaskListener {
        void onTaskComplete(final Task task);
    }
}
