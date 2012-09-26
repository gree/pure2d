/**
 * 
 */
package com.funzio.pure2D.animators;

/**
 * @author long
 */
public interface Animator extends Manipulator {
    public void start();

    public void stop();

    public void end();

    public boolean isRunning();

    public void setListener(AnimatorListener listener);

    public AnimatorListener getListener();

    public interface AnimatorListener {
        // public void onAnimationStop(Animator animator);

        public void onAnimationEnd(Animator animator);

        // public void onAnimationStart(Animator animator);

        public void onAnimationUpdate(Animator animator);
    }
}
