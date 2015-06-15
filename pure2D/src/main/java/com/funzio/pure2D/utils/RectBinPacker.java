/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.utils;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author long
 */
public class RectBinPacker {
    private static final String TAG = RectBinPacker.class.getSimpleName();

    private final List<Rect> mRects = new ArrayList<Rect>();
    private Rect mBounds = new Rect();
    private final int mMaxWidth;
    private final boolean mForcePO2;
    private boolean mRotationEnabled = false;

    private Node mRoot;

    public RectBinPacker(final int maxWidth, final boolean forcePO2) {
        this(512, maxWidth, forcePO2);
    }

    public RectBinPacker(final int minWidth, final int maxWidth, final boolean forcePO2) {
        mMaxWidth = maxWidth;
        mForcePO2 = forcePO2;

        // minimum size
        mRoot = new Node(0, 0, minWidth, minWidth);
    }

    public boolean isRotationEnabled() {
        return mRotationEnabled;
    }

    public void setRotationEnabled(final boolean rotationEnabled) {
        mRotationEnabled = rotationEnabled;
    }

    public Rect occupy(final int w, final int h) {
        final Node node = mRoot.findNode(w, h);
        if (node != null) {
            final Rect newRect = node.occupy(w, h);

            // update the bounds
            mBounds.union(newRect);
            // add to list
            mRects.add(newRect);

            return newRect;
        } else {
            // TODO check rotation

            // grow it
            final Rect newRect = growAndOccupy(w, h);
            if (newRect == null) {
                Log.e(TAG, String.format("Error: ran out of space of (%d, %d) for (%d, %d)!", getWidth(), getHeight(), w, h), new Exception());
            } else {

                // update the bounds
                mBounds.union(newRect);
                // add to list
                mRects.add(newRect);

                return newRect;
            }
        }

        return null;
    }

    protected Rect growAndOccupy(final int w, final int h) {
        final boolean canGrowDown = (w <= mRoot.width) && (mRoot.height + h <= mMaxWidth);
        final boolean canGrowRight = (h <= mRoot.height) && (mRoot.width + w <= mMaxWidth);

        final boolean shouldGrowRight = canGrowRight && (mRoot.height >= (mRoot.width + w)); // attempt to keep square-ish by growing right when height is much greater than width
        final boolean shouldGrowDown = canGrowDown && (mRoot.width >= (mRoot.height + h)); // attempt to keep square-ish by growing down  when width  is much greater than height

        if (shouldGrowRight)
            return growRight(w, h);
        else if (shouldGrowDown)
            return growDown(w, h);
        else if (canGrowRight)
            return growRight(w, h);
        else if (canGrowDown)
            return growDown(w, h);

        return null; // need to ensure sensible root starting size to avoid this happening
    }

    protected Rect growRight(final int w, final int h) {
        final Node down = mRoot;
        final Node right = new Node(mRoot.x + mRoot.width, mRoot.y, w, mRoot.height);

        // new root
        mRoot = new Node(mRoot.x, mRoot.y, mRoot.width + w, mRoot.height);
        mRoot.split(down, right);

        // occupy it
        return right.occupy(w, h);
    }

    protected Rect growDown(final int w, final int h) {
        final Node down = new Node(mRoot.x, mRoot.y + mRoot.height, mRoot.width, h);
        final Node right = mRoot;

        // new root
        mRoot = new Node(mRoot.x, mRoot.y, mRoot.width, mRoot.height + h);
        mRoot.split(down, right);

        // occupy it
        return down.occupy(w, h);
    }

    public void reset() {
        mRoot.reset();
        mRects.clear();
        mBounds.setEmpty();
    }

    public Rect getRect(final int index) {
        return mRects.get(index);
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.width()) : mBounds.width();
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.height()) : mBounds.height();
    }

    public static class Node {
        private int x;
        private int y;
        private int width;
        private int height;

        private Node mDown;
        private Node mRight;
        private boolean mOccupied;

        public Node(final int x, final int y, final int w, final int h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public void reset() {
            mDown = mRight = null;
            mOccupied = false;
        }

        public Node findNode(final int w, final int h) {
            if (mOccupied) {
                if (mRight != null) {
                    final Node node = mRight.findNode(w, h);
                    if (node != null) {
                        return node;
                    }
                }

                if (mDown != null) {
                    final Node node = mDown.findNode(w, h);
                    if (node != null) {
                        return node;
                    }
                }
            } else if (w <= width && h <= height) {
                return this;
            }

            return null;
        }

        public boolean split(final Node down, final Node right) {
            // sanity check
            if (mOccupied) return false;

            mOccupied = true;
            mDown = down;
            mRight = right;

            return true;
        }

        public Rect occupy(final int w, final int h) {
            // sanity check
            if (mOccupied) return null;

            final Rect rect = new Rect(x, y, x + w, y + h);
            mOccupied = true;   // flag

            // split it
            if (height > h) {
                mDown = new Node(x, y + h, width, height - h);
            }
            if (width > w) {
                mRight = new Node(x + w, y, width - w, h);
            }

            /*// selective split
            final int r = (width - w) * h;
            final int d = w * (height - h);
            if (d > r) {
                // bigger down
                mDown = new Node(x, y + h, width, height - h);
                if (r > 0) {
                    mRight = new Node(x + w, y, width - w, h);
                }
            } else if (r > d) {
                // bigger right
                mRight = new Node(x + w, y, width - w, height);
                if (d > 0) {
                    mDown = new Node(x, y + h, w, height - h);
                }
            }*/

            return rect;
        }

    }
}
