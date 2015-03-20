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
package com.funzio.pure2D.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * @author long
 */
public class RectPacker {
    private static final String TAG = RectPacker.class.getSimpleName();

    private static final Comparator<Point> COMPARATOR = new Comparator<Point>() {
        public int compare(final Point left, final Point right) {
            return (left.x * left.x + left.y + left.y) - (right.x * right.x + right.y * right.y); // square
            // return (left.x + 1) * (left.y + 1) - (right.x + 1) * (right.y + 1);
        }
    };

    private final List<Rect> mRects = new ArrayList<Rect>();
    private int mRectNum = 0;
    private Rect mBounds = new Rect();
    private final int mMaxWidth;
    private final boolean mForcePO2;
    private boolean mRotationEnabled = true;
    private boolean mQuickMode = true;
    private int mWidth = 0;
    private int mHeight = 0;

    // for sorting the lines
    private final TreeSet<Integer> mHLines = new TreeSet<Integer>();
    private final TreeSet<Integer> mVLines = new TreeSet<Integer>();
    private final TreeSet<Point> mHotPoints = new TreeSet<Point>(COMPARATOR);
    private final ArrayList<Point> mPoints2Remove = new ArrayList<Point>();
    private final Rect mTempRect = new Rect();
    private final Rect mTempBounds = new Rect();

    public RectPacker(final int maxWidth, final boolean forcePO2) {
        mMaxWidth = maxWidth;
        mForcePO2 = forcePO2;
    }

    public boolean isRotationEnabled() {
        return mRotationEnabled;
    }

    public void setRotationEnabled(final boolean rotationEnabled) {
        mRotationEnabled = rotationEnabled;
    }

    public boolean isQuickMode() {
        return mQuickMode;
    }

    public void setQuickMode(final boolean quickMode) {
        mQuickMode = quickMode;
    }

    public Rect occupy(final int rectWidth, final int rectHeight) {
        // Log.e("long", "occupy() " + rectWidth + " x " + rectHeight);

        Rect newRect;
        if (mWidth == 0) {
            mHLines.add(0);
            mVLines.add(0);
            newRect = new Rect(0, 0, rectWidth, rectHeight);
        } else {
            newRect = getNextRect(rectWidth, rectHeight);
        }

        if (newRect != null) {
            // find the new points
            for (Integer hline : mHLines) {
                if (hline < newRect.bottom && !isOccupied(newRect.right, hline)) {
                    mHotPoints.add(new Point(newRect.right, hline));
                    // break; // wrong
                }
            }
            for (Integer vline : mVLines) {
                if (vline < newRect.right && !isOccupied(vline, newRect.bottom)) {
                    mHotPoints.add(new Point(vline, newRect.bottom));
                    // break; // wrong
                }
            }

            // update the bounds
            mBounds.union(newRect);
            // add the rect to the list
            mRects.add(newRect);
            mRectNum++;

            // add the lines
            mHLines.add(newRect.bottom);
            mVLines.add(newRect.right);

            // Log.e("long", mHotPoints.toString());

            // find the size
            mWidth = mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.width()) : mBounds.width();
            mHeight = mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.height()) : mBounds.height();
        } else {
            Log.e(TAG, "Error: ran out of space!", new Exception());
        }

        return newRect;
    }

    public void reset() {
        mBounds.setEmpty();

        mRects.clear();
        mRectNum = 0;

        mHotPoints.clear();
        mVLines.clear();
        mHLines.clear();

        mPoints2Remove.clear();
        mWidth = mHeight = 0;
    }

    private Rect getNextRect(final int rectWidth, final int rectHeight) {
        int w, h;
        int newArea, minArea = Integer.MAX_VALUE;
        final Rect minRect = new Rect();
        Point destPoint = null;

        for (Point point : mHotPoints) {
            // Log.e("long", " >>> point " + ": " + point.x + " " + point.y);

            // quick point test
            if (isOccupied(point.x, point.y)) {
                // mHotPoints.remove(point);
                mPoints2Remove.add(point);

                continue;
            }

            mTempRect.set(point.x, point.y, point.x + rectWidth, point.y + rectHeight);
            if (!isOccupied(mTempRect)) {
                mTempBounds.set(mBounds);
                mTempBounds.union(mTempRect);
                if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {// && mTempBounds.height() <= mMaxWidth

                    w = mTempBounds.width();
                    h = mTempBounds.height();
                    if (mForcePO2) {
                        w = Pure2DUtils.getNextPO2(w);
                        h = Pure2DUtils.getNextPO2(h);
                    }
                    newArea = w * w + h * h;
                    // if (mForcePO2) {
                    // newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                    // } else {
                    // newArea = mTempBounds.width() * mTempBounds.height();
                    // }
                    if (newArea < minArea) {
                        minArea = newArea;
                        minRect.set(mTempRect);
                        destPoint = point;
                    }
                }
            }

            // rotate
            if (mRotationEnabled) {
                mTempRect.set(point.x, point.y, point.x + rectHeight, point.y + rectWidth);
                if (!isOccupied(mTempRect)) {
                    mTempBounds.set(mBounds);
                    mTempBounds.union(mTempRect);
                    if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {// && mTempBounds.height() <= mMaxWidth

                        w = mTempBounds.width();
                        h = mTempBounds.height();
                        if (mForcePO2) {
                            w = Pure2DUtils.getNextPO2(w);
                            h = Pure2DUtils.getNextPO2(h);
                        }
                        newArea = w * w + h * h;
                        // if (mForcePO2) {
                        // newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                        // } else {
                        // newArea = mTempBounds.width() * mTempBounds.height();
                        // }
                        if (newArea < minArea) {
                            minArea = newArea;
                            minRect.set(mTempRect);
                            destPoint = point;
                        }
                    }
                }
            }

            if (mQuickMode && minArea < Integer.MAX_VALUE) {
                break;
            }
        }

        // remove occupied points
        if (mPoints2Remove.size() > 0) {
            for (Point p : mPoints2Remove) {
                mHotPoints.remove(p);
            }
            mPoints2Remove.clear();
        }
        if (destPoint != null) {
            mHotPoints.remove(destPoint);
        }

        if (minArea < Integer.MAX_VALUE) {
            return minRect;
        } else {
            // uh oh, no point found!
            return null;
        }
    }

    public Rect getRect(final int index) {
        return mRects.get(index);
    }

    private boolean isOccupied(final Rect rect) {
        for (int i = 0; i < mRectNum; i++) {
            if (Rect.intersects(mRects.get(i), rect)) {
                return true;
            }
        }

        return false;
    }

    private boolean isOccupied(final int x, final int y) {
        Rect rect;
        for (int i = 0; i < mRectNum; i++) {
            rect = mRects.get(i);
            if (x >= rect.left && x < rect.right && y >= rect.top && y < rect.bottom) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return mHeight;
    }

    public TreeSet<Point> getHotPoints() {
        return mHotPoints;
    }
}
