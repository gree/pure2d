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
package com.funzio.pure2D.shapes;

import android.graphics.PointF;

import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;

/**
 * @author long
 */
public class LineArray extends DisplayGroup {
    private float mThickness = 1;
    private ColorBuffer mColorBuffer;

    public LineArray() {
    }

    public void setPoints(final PointF[] points) {
        removeAllChildren();

        for (int i = 1; i < points.length; i++) {
            Line line = new Line();
            line.setPoints(points[i - 1], points[i]);
            line.setThickness(mThickness);
            line.setColor(mColor);
            line.setColorBuffer(mColorBuffer);
            addChild(line);
        }
    }

    /**
     * @return the thickness
     */
    public float getThickness() {
        return mThickness;
    }

    /**
     * @param thickness the thickness to set
     */
    public void setThickness(final float thickness) {
        mThickness = thickness;

        for (int i = 0; i < mNumChildren; i++) {
            Line line = (Line) mChildren.get(i);
            line.setThickness(mThickness);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setColor(com.funzio.pure2D.gl.GLColor)
     */
    @Override
    public void setColor(final GLColor color) {
        super.setColor(color);

        for (int i = 0; i < mNumChildren; i++) {
            Line line = (Line) mChildren.get(i);
            line.setColor(color);
        }
    }

    public void setColorBuffer(final ColorBuffer buffer) {
        mColorBuffer = buffer;
        for (int i = 0; i < mNumChildren; i++) {
            Line line = (Line) mChildren.get(i);
            line.setColorBuffer(mColorBuffer);
        }
    }

    public ColorBuffer getColorBuffer() {
        return mColorBuffer;
    }
}
