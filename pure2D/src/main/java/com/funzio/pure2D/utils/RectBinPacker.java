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

    private final Node mRoot;

    public RectBinPacker(final int maxWidth, final boolean forcePO2) {
        mMaxWidth = maxWidth;
        mForcePO2 = forcePO2;

        // minimum size
        mRoot = new Node(0, 0, 512, 512);
    }

    public boolean isRotationEnabled() {
        return mRotationEnabled;
    }

    public void setRotationEnabled(final boolean rotationEnabled) {
        mRotationEnabled = rotationEnabled;
    }

    public Rect occupy(final int w, final int h) {
        final Node node = findNode(mRoot, w, h);
        if (node != null) {
            node.occupy(w, h);

            // update the bounds
            mBounds.union(node.mOccupiedRect);
            // add to list
            mRects.add(node.mOccupiedRect);

            return node.mOccupiedRect;
        } else {
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

    protected Node findNode(final Node root, final int w, final int h) {
        if (root == null) return null;

        if (root.hasChildren()) {
            final Node node = findNode(root.mRight, w, h);
            if (node != null) {
                return node;
            }
            return findNode(root.mDown, w, h);
        } else if (w <= root.width && h <= root.height) {
            return root;
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
        final Node node = mRoot.growRight(w);
        return node.occupy(w, h);
    }

    protected Rect growDown(final int w, final int h) {
        final Node node = mRoot.growDown(h);
        return node.occupy(w, h);
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

        protected Rect mOccupiedRect;
        protected Node mDown;
        protected Node mRight;

        public Node(final int x, final int y, final int w, final int h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public void reset() {
            mOccupiedRect = null;
            mDown = mRight = null;
        }

        public Node growRight(final int w) {
            //mDown = null;
            mRight = new Node(x + width, y, w, height);
            width += w;

            return mRight;
        }

        public Node growDown(final int h) {
            //mRight = null;
            mDown = new Node(x, y + height, width, h);
            height += h;

            return mDown;
        }

        public Rect occupy(final int w, final int h) {
            mOccupiedRect = new Rect(x, y, x + w, y + h);

            // split it
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
            }

            return mOccupiedRect;
        }

        public boolean hasChildren() {
            return mDown != null || mRight != null;
        }

    }
}
