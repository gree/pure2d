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
    private int mMaxWidth = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    // for sorting the lines
    private TreeSet<Integer> mHLines = new TreeSet<Integer>();
    private TreeSet<Integer> mVLines = new TreeSet<Integer>();

    public RectPacker(final int maxWidth) {
        mMaxWidth = maxWidth;
    }

    public Rect occupy(final int rectWidth, final int rectHeight) {
        Rect newRect;
        if (mWidth == 0) {
            mHLines.add(0);
            mVLines.add(0);
            newRect = new Rect(0, 0, rectWidth - 1, rectHeight - 1);
        } else {
            newRect = getNextRect(rectWidth, rectHeight);
        }

        // update the bounds
        mBounds.union(newRect);
        // add to the list
        mRects.add(newRect);

        // add the lines
        mHLines.add(newRect.right + 1);
        mVLines.add(newRect.bottom + 1);

        // find the size
        mWidth = Pure2DUtils.getNextPO2(mBounds.width());
        mHeight = Pure2DUtils.getNextPO2(mBounds.height());

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
        final Rect newRect = new Rect();
        final Rect newBounds = new Rect();
        int newArea;
        int minArea = Integer.MAX_VALUE;
        final Rect minRect = new Rect();

        for (Integer vline : mVLines) {
            for (Integer hline : mHLines) {
                newRect.set(hline, vline, hline + rectWidth - 1, vline + rectHeight - 1);
                if (!intersect(newRect)) {
                    newBounds.set(mBounds);
                    newBounds.union(newRect);
                    if (newBounds.width() <= mMaxWidth) {
                        newArea = Pure2DUtils.getNextPO2(newBounds.width()) * Pure2DUtils.getNextPO2(newBounds.height());
                        if (newArea < minArea) { // || (newArea == minArea && (hline < minRect.left || vline < minRect.top))
                            minArea = newArea;
                            minRect.set(newRect);
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
