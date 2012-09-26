/**
 * 
 */
package com.funzio.pure2D.utils;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

/**
 * @author long
 */
public class CopyOfRectPacker {

    private List<Rect> mRects = new ArrayList<Rect>();
    private Rect mBounds = new Rect();
    private int mWidth = 0;
    private int mHeight = 0;

    public CopyOfRectPacker() {
    }

    public Rect occupy(final int rectWidth, final int rectHeight) {
        Rect newRect;
        if (mWidth == 0) {
            newRect = new Rect(0, 0, rectWidth - 1, rectHeight - 1);
        } else {
            newRect = getNextRect(rectWidth, rectHeight);
        }

        // update the bounds
        mBounds.union(newRect);
        // add to the list
        mRects.add(newRect);

        // find the size
        mWidth = Pure2DUtils.getNextPO2(mBounds.right);
        mHeight = Pure2DUtils.getNextPO2(mBounds.bottom);

        return newRect;
    }

    public void reset() {
        mBounds.setEmpty();
        mRects.clear();
        mWidth = mHeight = 0;
    }

    private Rect getNextRect(final int rectWidth, final int rectHeight) {
        final int size = mRects.size();
        final Rect newRect = new Rect();
        int newArea;
        final Rect newBounds = new Rect();
        int minArea = Integer.MAX_VALUE;
        final Rect minRect = new Rect();
        for (int i = 0; i < size; i++) {
            final Rect tempRect = mRects.get(i);

            // right rect
            newRect.set(tempRect.right + 1, tempRect.top, tempRect.right + rectWidth, tempRect.top + rectHeight);
            if (!intersect(newRect)) {
                newBounds.set(mBounds);
                newBounds.union(newRect);
                newArea = Pure2DUtils.getNextPO2(newBounds.right) * Pure2DUtils.getNextPO2(newBounds.bottom);
                if (newArea < minArea) {
                    minArea = newArea;
                    minRect.set(newRect);
                }
            }

            // bottom rect
            newRect.set(tempRect.left, tempRect.bottom + 1, tempRect.left + rectWidth, tempRect.bottom + rectHeight);
            if (!intersect(newRect)) {
                newBounds.set(mBounds);
                newBounds.union(newRect);
                newArea = Pure2DUtils.getNextPO2(newBounds.right) * Pure2DUtils.getNextPO2(newBounds.bottom);
                if (newArea < minArea) {
                    minArea = newArea;
                    minRect.set(newRect);

                    // remove the gap on the left
                    for (int j = 0; j < size; j++) {
                        final Rect leftRect = mRects.get(j);
                        if (minRect.left > leftRect.right + 1) {
                            newRect.set(leftRect.right + 1, minRect.top, leftRect.right + rectWidth, minRect.bottom);
                            if (!intersect(newRect)) {
                                minRect.set(newRect);
                            }
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
