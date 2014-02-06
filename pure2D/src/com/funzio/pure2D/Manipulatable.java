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

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author long
 */
public interface Manipulatable {
    public void setPosition(final float x, final float y);

    public void setPosition(final PointF position);

    public PointF getPosition();

    public void moveTo(final float x, final float y);

    public void move(final float dx, final float dy);

    public PointF getSize();

    public void setSize(final PointF size);

    public void setSize(final float w, final float h);

    public void setRotation(final float degree);

    public float getRotation();

    public void rotate(final float degreeDelta);

    public void setScale(final float sx, final float sy);

    public void setScale(final float scale);

    public PointF getScale();

    public RectF getBounds();

    public void setX(final float x);

    public float getX();

    public void setY(final float y);

    public float getY();

    public float getWidth();

    public float getHeight();
}
