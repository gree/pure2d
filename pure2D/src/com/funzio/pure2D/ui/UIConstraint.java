/**
 * 
 */
package com.funzio.pure2D.ui;

import android.graphics.PointF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.LinearGroup;
import com.funzio.pure2D.containers.VGroup;

/**
 * @author long.ngo
 */
public class UIConstraint {
    public static enum UNIT {
        UNSET, PIXEL, PERCENT, WRAP
    }

    public float width;
    public UNIT widthUnit = UNIT.UNSET;
    public float height;
    public UNIT heightUnit = UNIT.UNSET;

    public float x;
    public UNIT xUnit = UNIT.UNSET;
    public float y;
    public UNIT yUnit = UNIT.UNSET;

    public float left;
    public UNIT leftUnit = UNIT.UNSET;
    public float right;
    public UNIT rightUnit = UNIT.UNSET;
    public float top;
    public UNIT topUnit = UNIT.UNSET;
    public float bottom;
    public UNIT bottomUnit = UNIT.UNSET;

    public float centerX;
    public UNIT centerXUnit = UNIT.UNSET;
    public float centerY;
    public UNIT centerYUnit = UNIT.UNSET;

    public float childrenGap;
    public UNIT childrenGapUnit = UNIT.UNSET;

    private boolean mHasAttributes = false;

    public UIConstraint() {
        // TODO Auto-generated constructor stub
    }

    public UIConstraint(final XmlPullParser xmlParser) {
        setAttributes(xmlParser);
    }

    public void setAttributes(final XmlPullParser xmlParser) {
        mHasAttributes = false;

        widthUnit = getAttributeUnit(xmlParser, "width");
        if (widthUnit != UNIT.UNSET) {
            if (widthUnit != UNIT.WRAP) {
                width = getAttributeValue(xmlParser, "width");
            }
            mHasAttributes = true;
        }

        heightUnit = getAttributeUnit(xmlParser, "height");
        if (heightUnit != UNIT.UNSET) {
            if (heightUnit != UNIT.WRAP) {
                height = getAttributeValue(xmlParser, "height");
            }
            mHasAttributes = true;
        }

        xUnit = getAttributeUnit(xmlParser, "x");
        if (xUnit != UNIT.UNSET) {
            x = getAttributeValue(xmlParser, "x");
            mHasAttributes = true;
        }

        yUnit = getAttributeUnit(xmlParser, "y");
        if (yUnit != UNIT.UNSET) {
            y = getAttributeValue(xmlParser, "y");
            mHasAttributes = true;
        }

        leftUnit = getAttributeUnit(xmlParser, "left");
        if (leftUnit != UNIT.UNSET) {
            left = getAttributeValue(xmlParser, "left");
            mHasAttributes = true;
        }

        rightUnit = getAttributeUnit(xmlParser, "right");
        if (rightUnit != UNIT.UNSET) {
            right = getAttributeValue(xmlParser, "right");
            mHasAttributes = true;
        }

        topUnit = getAttributeUnit(xmlParser, "top");
        if (topUnit != UNIT.UNSET) {
            top = getAttributeValue(xmlParser, "top");
            mHasAttributes = true;
        }

        bottomUnit = getAttributeUnit(xmlParser, "bottom");
        if (bottomUnit != UNIT.UNSET) {
            bottom = getAttributeValue(xmlParser, "bottom");
            mHasAttributes = true;
        }

        centerXUnit = getAttributeUnit(xmlParser, "centerX");
        if (centerXUnit != UNIT.UNSET) {
            centerX = getAttributeValue(xmlParser, "centerX");
            mHasAttributes = true;
        }

        centerYUnit = getAttributeUnit(xmlParser, "centerY");
        if (centerYUnit != UNIT.UNSET) {
            centerY = getAttributeValue(xmlParser, "centerY");
            mHasAttributes = true;
        }

        childrenGapUnit = getAttributeUnit(xmlParser, "childrenGap");
        if (childrenGapUnit != UNIT.UNSET) {
            childrenGap = getAttributeValue(xmlParser, "childrenGap");
            mHasAttributes = true;
        }
    }

    protected float getAttributeValue(final XmlPullParser xmlParser, final String att) {
        final String valueSt = xmlParser.getAttributeValue(null, att);
        if (valueSt != null && !valueSt.equals("")) {
            if (valueSt.endsWith("%")) {
                return Float.valueOf(valueSt.substring(0, valueSt.length() - 1).trim()) / 100; // pre-cal
            } else {
                return Float.valueOf(valueSt.trim());
            }
        }

        return 0;
    }

    protected UNIT getAttributeUnit(final XmlPullParser xmlParser, final String att) {
        final String valueSt = xmlParser.getAttributeValue(null, att);
        if (valueSt != null && !valueSt.equals("")) {
            if (valueSt.endsWith("wrap_content")) {
                return UNIT.WRAP;
            } else if (valueSt.endsWith("%")) {
                return UNIT.PERCENT;
            } else {
                return UNIT.PIXEL;
            }
        } else {
            return UNIT.UNSET;
        }
    }

    public boolean hasAttributes() {
        return mHasAttributes;
    }

    public void apply(final DisplayObject target, final Container container) {
        if (container == null || !mHasAttributes) {
            return;
        }

        final PointF targetPos = target.getPosition();
        final PointF targetSize = target.getSize();
        final PointF parentSize = container.getSize();
        final float pw = parentSize.x, ph = parentSize.y;
        final float cx = targetPos.x, cy = targetPos.y;
        final float cw = targetSize.x, ch = targetSize.y;
        float l = cx, r = 0, t = 0, b = cy, w = cw, h = ch;

        // left and right
        if (leftUnit == UNIT.PIXEL) {
            l = x;
        } else if (leftUnit == UNIT.PERCENT) {
            l = pw * left;
        }
        if (rightUnit == UNIT.PIXEL) {
            r = right;
        } else if (rightUnit == UNIT.PERCENT) {
            r = pw * right;
        }
        // implicit width
        if (leftUnit != UNIT.UNSET && rightUnit != UNIT.UNSET) {
            w = pw - (l + r);
        }

        // top and bottom
        if (topUnit == UNIT.PIXEL) {
            t = top;
        } else if (topUnit == UNIT.PERCENT) {
            t = ph * top;
        }
        if (bottomUnit == UNIT.PIXEL) {
            b = bottom;
        } else if (bottomUnit == UNIT.PERCENT) {
            b = ph * bottom;
        }
        // implicit height
        if (topUnit != UNIT.UNSET && bottomUnit != UNIT.UNSET) {
            h = ph - (t + b);
        }

        // explicit width
        if (widthUnit == UNIT.PIXEL) {
            w = width;
        } else if (widthUnit == UNIT.PERCENT) {
            w = pw * width;
        } else if (widthUnit == UNIT.WRAP && target instanceof DisplayGroup) {
            ((DisplayGroup) target).setWrapContentWidth(true);
        }

        // explicit height
        if (heightUnit == UNIT.PIXEL) {
            h = height;
        } else if (heightUnit == UNIT.PERCENT) {
            h = ph * height;
        } else if (heightUnit == UNIT.WRAP && target instanceof DisplayGroup) {
            ((DisplayGroup) target).setWrapContentHeight(true);
        }

        // gap
        if (childrenGapUnit != UNIT.UNSET && target instanceof LinearGroup) {
            float g = 0;
            if (childrenGapUnit == UNIT.PIXEL) {
                g = childrenGap;
            } else if (childrenGapUnit == UNIT.PERCENT) {
                g = childrenGap * (target instanceof VGroup ? target.getHeight() : target.getWidth());
            }
            ((LinearGroup) target).setGap(g);
        }

        // x center
        if (centerXUnit != UNIT.UNSET) {
            if (centerXUnit == UNIT.PIXEL) {
                l = centerX;
            } else if (xUnit == UNIT.PERCENT) {
                l = pw * centerX;
            }
            l += (pw - w) * 0.5f;
        }

        // y center
        if (centerYUnit != UNIT.UNSET) {
            if (centerYUnit == UNIT.PIXEL) {
                b = centerY;
            } else if (yUnit == UNIT.PERCENT) {
                b = ph * centerY;
            }
            b += (ph - h) * 0.5f;
        }

        // implicit left base on right and width
        if (leftUnit == UNIT.UNSET && rightUnit != UNIT.UNSET) {
            l = pw - r - w;
        }

        // implicit bottom base on top and height
        if (bottomUnit == UNIT.UNSET && topUnit != UNIT.UNSET) {
            b = ph - t - h;
        }

        // explicit x
        if (xUnit == UNIT.PIXEL) {
            l = x;
        } else if (xUnit == UNIT.PERCENT) {
            l = pw * x;
        }

        // explicit y
        if (yUnit == UNIT.PIXEL) {
            b = y;
        } else if (yUnit == UNIT.PERCENT) {
            b = ph * y;
        }

        // apply with diff check for perf
        if (cx != l || cy != b) {
            target.setPosition(l, b);
        }
        if (cw != w || ch != h) {
            target.setSize(w, h);
        }
    }
}
