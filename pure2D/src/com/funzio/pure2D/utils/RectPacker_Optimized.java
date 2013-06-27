/**
 * 
 */
package com.funzio.pure2D.utils;

import java.util.ArrayList;
import java.util.TreeSet;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * @author long
 */
public class RectPacker_Optimized {
    private static final String TAG = RectPacker_Optimized.class.getSimpleName();

    private ArrayList<Rect> mRects = new ArrayList<Rect>();
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
    private final ArrayList<Point> mHotPoints = new ArrayList<Point>();
    private final Rect mTempRect = new Rect();
    private final Rect mTempBounds = new Rect();

    public RectPacker_Optimized(final int maxWidth, final boolean forcePO2) {
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
                    final Point p = new Point(newRect.right, hline);
                    if (!mHotPoints.contains(p)) {
                        mHotPoints.add(p);
                    }
                    // break; // wrong
                }
            }
            for (Integer vline : mVLines) {
                if (vline < newRect.right && !isOccupied(vline, newRect.bottom)) {
                    final Point p = new Point(vline, newRect.bottom);
                    if (!mHotPoints.contains(p)) {
                        mHotPoints.add(p);
                    }
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

        mVLines.clear();
        mHLines.clear();
        mWidth = mHeight = 0;
    }

    private Rect getNextRect(final int rectWidth, final int rectHeight) {
        int newArea, minIndex = -1, minArea = Integer.MAX_VALUE;
        final Rect minRect = new Rect();

        int index = 0, size = mHotPoints.size();
        Point point = null;
        for (index = 0; index < size; index++) {
            point = mHotPoints.get(index);
            // Log.e("long", " >>> point " + index + ": " + point.x + " " + point.y);

            // quick point test
            if (isOccupied(point.x, point.y)) {
                mHotPoints.remove(index);
                index--;
                size--;

                continue;
            }

            // rotate
            if (mRotationEnabled) {
                mTempRect.set(point.x, point.y, point.x + rectHeight, point.y + rectWidth);
                if (!isOccupied(mTempRect)) {
                    mTempBounds.set(mBounds);
                    mTempBounds.union(mTempRect);
                    if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {// && mTempBounds.height() <= mMaxWidth
                        if (mForcePO2) {
                            newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                        } else {
                            newArea = mTempBounds.width() * mTempBounds.height();
                        }
                        if (newArea < minArea) {
                            minArea = newArea;
                            minIndex = index;
                            minRect.set(mTempRect);
                        }
                    }
                }
            }

            mTempRect.set(point.x, point.y, point.x + rectWidth, point.y + rectHeight);
            if (!isOccupied(mTempRect)) {
                mTempBounds.set(mBounds);
                mTempBounds.union(mTempRect);
                if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {// && mTempBounds.height() <= mMaxWidth
                    if (mForcePO2) {
                        newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                    } else {
                        newArea = mTempBounds.width() * mTempBounds.height();
                    }
                    if (newArea < minArea) {
                        minArea = newArea;
                        minIndex = index;
                        minRect.set(mTempRect);
                    }
                }
            }

            if (mQuickMode && minArea < Integer.MAX_VALUE) {
                break;
            }
        }

        // remove the points
        if (minIndex >= 0) {
            mHotPoints.remove(minIndex);
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

    public ArrayList<Point> getHotPoints() {
        return mHotPoints;
    }
}
