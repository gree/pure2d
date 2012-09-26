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
