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
        UNSET, PIXEL, PERCENT
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

    public UIConstraint() {
        // TODO Auto-generated constructor stub
    }

    public UIConstraint(final XmlPullParser xmlParser) {
        setAttributes(xmlParser);
    }

    public void setAttributes(final XmlPullParser xmlParser) {
        widthUnit = getAttributeUnit(xmlParser, "width");
        if (widthUnit != UNIT.UNSET) {
            width = getAttributeValue(xmlParser, "width");
        }

        heightUnit = getAttributeUnit(xmlParser, "height");
        if (heightUnit != UNIT.UNSET) {
            height = getAttributeValue(xmlParser, "height");
        }

        xUnit = getAttributeUnit(xmlParser, "x");
        if (xUnit != UNIT.UNSET) {
            x = getAttributeValue(xmlParser, "x");
        }

        yUnit = getAttributeUnit(xmlParser, "y");
        if (yUnit != UNIT.UNSET) {
            y = getAttributeValue(xmlParser, "y");
        }

        leftUnit = getAttributeUnit(xmlParser, "left");
        if (leftUnit != UNIT.UNSET) {
            left = getAttributeValue(xmlParser, "left");
        }

        rightUnit = getAttributeUnit(xmlParser, "right");
        if (rightUnit != UNIT.UNSET) {
            right = getAttributeValue(xmlParser, "right");
        }

        topUnit = getAttributeUnit(xmlParser, "top");
        if (topUnit != UNIT.UNSET) {
            top = getAttributeValue(xmlParser, "top");
        }

        bottomUnit = getAttributeUnit(xmlParser, "bottom");
        if (bottomUnit != UNIT.UNSET) {
            bottom = getAttributeValue(xmlParser, "bottom");
        }

        centerXUnit = getAttributeUnit(xmlParser, "centerX");
        if (centerXUnit != UNIT.UNSET) {
            centerX = getAttributeValue(xmlParser, "centerX");
        }

        centerYUnit = getAttributeUnit(xmlParser, "centerY");
        if (centerYUnit != UNIT.UNSET) {
            centerY = getAttributeValue(xmlParser, "centerY");
        }
    }

    protected float getAttributeValue(final XmlPullParser xmlParser, final String att) {
        final String valueSt = xmlParser.getAttributeValue(null, att);
        if (valueSt != null && !valueSt.equals("")) {
            if (valueSt.endsWith("%")) {
                return Float.valueOf(valueSt.substring(0, valueSt.length() - 1)) / 100; // pre-cal
            } else {
                return Float.valueOf(valueSt);
            }
        }

        return 0;
    }

    protected UNIT getAttributeUnit(final XmlPullParser xmlParser, final String att) {
        final String valueSt = xmlParser.getAttributeValue(null, att);
        if (valueSt != null && !valueSt.equals("")) {
            if (valueSt.endsWith("%")) {
                return UNIT.PERCENT;
            } else {
                return UNIT.PIXEL;
            }
        } else {
            return UNIT.UNSET;
        }
    }
}
