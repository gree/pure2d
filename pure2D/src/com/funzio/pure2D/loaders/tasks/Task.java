/**
 * 
 */
package com.funzio.pure2D.loaders.tasks;

/**
 * @author long
 */
public interface Task {
    public boolean run();

    public static interface TaskListener {
        void onTaskComplete(final Task task);
    }

    public static interface Stoppable {
        public boolean stop();
    }
}
