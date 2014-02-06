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

    public void setStartDelay(int startDelay);

    public int getStartDelay();

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
