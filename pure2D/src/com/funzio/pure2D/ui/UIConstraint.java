/**
 * 
 */
package com.funzio.pure2D.ui;

import org.xmlpull.v1.XmlPullParser;

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
}
