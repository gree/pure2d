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
