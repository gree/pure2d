/**
 * 
 */
package com.funzio.pure2D.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.graphics.Rect;

/**
 * @author long
 */
public class RectPacker {

    private List<Rect> mRects = new ArrayList<Rect>();
    private Rect mBounds = new Rect();
    private final int mMaxWidth;
    private final boolean mForcePO2;
    private int mWidth = 0;
    private int mHeight = 0;

    // for sorting the lines
    private final TreeSet<Integer> mHLines = new TreeSet<Integer>();
    private final TreeSet<Integer> mVLines = new TreeSet<Integer>();
    private final Rect mTempRect = new Rect();
    private final Rect mTempBounds = new Rect();

    public RectPacker(final int maxWidth, final boolean forcePO2) {
        mMaxWidth = maxWidth;
        mForcePO2 = forcePO2;
    }

    public Rect occupy(final int rectWidth, final int rectHeight) {
        Rect newRect;
        if (mWidth == 0) {
            mHLines.add(0);
            mVLines.add(0);
            newRect = new Rect(0, 0, rectWidth, rectHeight);
        } else {
            newRect = getNextRect(rectWidth, rectHeight);
        }

        // update the bounds
        mBounds.union(newRect);
        // add to the list
        mRects.add(newRect);

        // add the lines
        mHLines.add(newRect.right);
        mVLines.add(newRect.bottom);

        // find the size
        mWidth = mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.width()) : mBounds.width();
        mHeight = mForcePO2 ? Pure2DUtils.getNextPO2(mBounds.height()) : mBounds.height();

        return newRect;
    }

    public void reset() {
        mBounds.setEmpty();
        mRects.clear();
        mVLines.clear();
        mHLines.clear();
        mWidth = mHeight = 0;
    }

    private Rect getNextRect(final int rectWidth, final int rectHeight) {
        int newArea;
        int minArea = Integer.MAX_VALUE;
        final Rect minRect = new Rect();

        for (Integer vline : mVLines) {
            for (Integer hline : mHLines) {
                // rotate
                mTempRect.set(hline, vline, hline + rectHeight, vline + rectWidth);
                if (!intersect(mTempRect)) {
                    mTempBounds.set(mBounds);
                    mTempBounds.union(mTempRect);
                    if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {
                        if (mForcePO2) {
                            newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                        } else {
                            newArea = mTempBounds.width() * mTempBounds.height();
                        }
                        if (newArea < minArea) { // || (newArea == minArea && (hline < minRect.left || vline < minRect.top))
                            minArea = newArea;
                            minRect.set(mTempRect);
                        }
                    }
                }

                mTempRect.set(hline, vline, hline + rectWidth, vline + rectHeight);
                if (!intersect(mTempRect)) {
                    mTempBounds.set(mBounds);
                    mTempBounds.union(mTempRect);
                    if (mTempBounds.width() <= mMaxWidth && mTempBounds.height() <= mMaxWidth) {
                        if (mForcePO2) {
                            newArea = Pure2DUtils.getNextPO2(mTempBounds.width()) * Pure2DUtils.getNextPO2(mTempBounds.height());
                        } else {
                            newArea = mTempBounds.width() * mTempBounds.height();
                        }
                        if (newArea < minArea) { // || (newArea == minArea && (hline < minRect.left || vline < minRect.top))
                            minArea = newArea;
                            minRect.set(mTempRect);
                        }
                    }
                }
            }
        }

        return minRect;
    }

    public Rect getRect(final int index) {
        return mRects.get(index);
    }

    private boolean intersect(final Rect rect) {
        final int size = mRects.size();
        for (int i = 0; i < size; i++) {
            if (rect.intersect(mRects.get(i))) {
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
}
