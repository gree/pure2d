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
package com.funzio.pure2D;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * @author long
 */
public interface Parentable {

    public void removeAllChildren();

    public boolean swapChildren(final int index1, final int index2);

    public int getNumChildren();

    public int getNumGrandChildren();

    public void invalidate();

    public void invalidate(int flags);

    public PointF getSize();

    public boolean isClippingEnabled();

    public void setClippingEnabled(final boolean clippingEnabled);

    public void localToGlobal(final PointF local, final PointF result);

    public void globalToLocal(final PointF global, final PointF result);

    public Matrix getMatrix();

    public Scene getScene();

    public boolean queueEvent(Runnable r);
}
