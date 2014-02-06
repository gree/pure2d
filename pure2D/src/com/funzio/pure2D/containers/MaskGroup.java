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
package com.funzio.pure2D.containers;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.Maskable;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.StencilBuffer;

/**
 * @author long
 */
public class MaskGroup extends DisplayGroup implements Maskable {

    private StencilBuffer mStencilBuffer = new StencilBuffer();
    private boolean mRenderChildren = false;

    public MaskGroup() {
        super();
    }

    @Override
    public boolean draw(final GLState glState) {
        mStencilBuffer.setGLState(glState);

        mStencilBuffer.startMask();
        final boolean success = super.draw(glState);
        mStencilBuffer.endMask();

        // if show children
        if (mRenderChildren) {
            super.draw(glState);
        }

        return success;
    }

    public void enableMask() {
        // use default
        mStencilBuffer.startTest(GL10.GL_EQUAL, 1, 0xFF);
    }

    public void enableMask(final int func, final int ref, final int mask) {
        mStencilBuffer.startTest(func, ref, mask);
    }

    public void disableMask() {
        mStencilBuffer.endTest();
    }

    public boolean getRenderChildren() {
        return mRenderChildren;
    }

    public void setRenderChildren(final boolean show) {
        mRenderChildren = show;
    }
}
