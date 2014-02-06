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
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * @author long
 */
public class StencilBuffer {
    protected static final String TAG = StencilBuffer.class.getSimpleName();

    private GL10 mGL;

    private boolean mStartedMask = false;

    private int[] mColorMasks;
    private int[] mDepthMasks;

    private int mFunc = GL10.GL_NEVER;
    private int mRef = 1;
    private int mMask = 0xFF;

    public StencilBuffer() {
    }

    public StencilBuffer(final int func, final int ref, final int mask) {
        mFunc = func;
        mRef = ref;
        mMask = mask;
    }

    public void setGLState(final GLState glState) {
        mGL = glState.mGL;
    }

    public void startMask() {
        if (mStartedMask) {
            return;
        }
        mStartedMask = true;

        // mGL.glClear(GL10.GL_DEPTH_BUFFER_BIT);

        // keep the values
        if (mColorMasks == null) {
            mColorMasks = new int[4];
        }
        if (mDepthMasks == null) {
            mDepthMasks = new int[1];
        }
        mGL.glGetIntegerv(GL11.GL_COLOR_WRITEMASK, mColorMasks, 0);
        mGL.glGetIntegerv(GL11.GL_DEPTH_WRITEMASK, mDepthMasks, 0);

        mGL.glEnable(GL10.GL_STENCIL_TEST);
        mGL.glColorMask(false, false, false, false);
        mGL.glDepthMask(false);

        mGL.glStencilFunc(mFunc, mRef, mMask);
        mGL.glStencilOp(GL10.GL_REPLACE, GL10.GL_KEEP, GL10.GL_KEEP); // draw 1s on test fail (always)

        // draw stencil pattern
        mGL.glStencilMask(0xFF);
        mGL.glClear(GL10.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF
    }

    public void endMask() {
        // restore the values
        mGL.glColorMask(mColorMasks[0] == 1, mColorMasks[1] == 1, mColorMasks[2] == 1, mColorMasks[3] == 1);
        mGL.glDepthMask(mDepthMasks[0] == 1);

        // nothing will be written in any condition
        mGL.glStencilMask(0x00);

        // disable
        mGL.glDisable(GL10.GL_STENCIL_TEST);

        mStartedMask = false;
    }

    public void startTest(final int func, final int ref, final int mask) {
        // draw where stencil's value is 0
        // mGL.glStencilFunc(GL10.GL_EQUAL, 0, 0xFF);

        // OR draw only where stencil's value is 1
        // mGL.glStencilFunc(GL10.GL_EQUAL, 1, 0xFF);

        mGL.glStencilFunc(func, ref, mask);
        mGL.glEnable(GL10.GL_STENCIL_TEST);
    }

    public void endTest() {
        mGL.glDisable(GL10.GL_STENCIL_TEST);
    }

    public int getFunc() {
        return mFunc;
    }

    public void setFunc(final int func) {
        mFunc = func;
    }

    public int getRef() {
        return mRef;
    }

    public void setRef(final int ref) {
        mRef = ref;
    }

    public int getMask() {
        return mMask;
    }

    public void setMask(final int mask) {
        mMask = mask;
    }
}
