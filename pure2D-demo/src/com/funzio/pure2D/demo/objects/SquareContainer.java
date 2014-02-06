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
package com.funzio.pure2D.demo.objects;

import java.util.Random;

import android.graphics.Point;
import android.graphics.PointF;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.shapes.Rectangular;

/**
 * @author long
 */
public class SquareContainer extends DisplayGroup {

    private Random mRandom = new Random();
    private PointF mBounds;
    private Point mVeloc;

    private Rectangular mSquare = new Rectangular();

    public SquareContainer() {
        mSquare.setColor(new GLColor(1, 1, 1, 0.5f));
        addChild(mSquare);

        mVeloc = new Point(1 + mRandom.nextInt(1), 1 + mRandom.nextInt(1));
        mVeloc.x *= Math.random() > 0.5 ? 1 : -1;
        mVeloc.y *= Math.random() > 0.5 ? 1 : -1;
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        mSquare.setSize(w, h);
        setOrigin(new PointF(w / 2, h / 2));
    };

    @Override
    public boolean update(final int deltaTime) {
        rotate(1);
        // setScale(2);

        if (mPosition.x > mBounds.x) {
            mPosition.x = mBounds.x;
            mVeloc.x = -Math.abs(mVeloc.x);
        } else if (mPosition.x <= 0) {
            mPosition.x = 0;
            mVeloc.x = Math.abs(mVeloc.x);
        }

        if (mPosition.y > mBounds.y) {
            mPosition.y = mBounds.y;
            mVeloc.y = -Math.abs(mVeloc.y);
        } else if (mPosition.y <= 0) {
            mPosition.y = 0;
            mVeloc.y = Math.abs(mVeloc.y);
        }

        float factor = (float) deltaTime / 10;
        move(mVeloc.x * factor, mVeloc.y * factor);

        return super.update(deltaTime);
    }

    @Override
    public void onAdded(final Container parent) {
        super.onAdded(parent);

        if (mParent != null) {
            mBounds = mParent.getSize();
        }
    }

}
