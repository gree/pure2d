/**
 * 
 */
package com.funzio.pure2D.animators;

import com.funzio.pure2D.utils.Reusable;

/**
 * @author long
 */
public interface Animator extends Manipulator, Reusable {
    public void start();

    public void stop();

    public void end();

    public boolean isRunning();

    public void setLifespan(int lifespan);

    public int getLifespan();

    public void setData(Object data);

    public Object getData();

    public boolean isAccumulating();

    public void setAccumulating(final boolean accumulating);

    public void setListener(AnimatorListener listener);

    public AnimatorListener getListener();

    public interface AnimatorListener {
        // public void onAnimationStop(Animator animator);

        public void onAnimationEnd(Animator animator);

        // public void onAnimationStart(Animator animator);

        public void onAnimationUpdate(Animator animator, float value);
    }
}
